package com.lookatme.smartstay.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//2. 보안권한 설정, 암호화, 로그인, 로그아웃, csrf
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    //비밀번호를 암호화처리
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //맵핑에 접근제한
    //static에 있는 폴더를 모두 사용으로 지정
    //html은 작업에 따라 사용할 권한 부여
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((auth)->
        { //각 자원, html 권한
            //permitAll(); 모든 사용자 사용가능
            auth.requestMatchers("/assets/**", "/css/**", "/js/**").permitAll();
            //세부페이지 또는 Controller가 완성되면 삭제
            auth.requestMatchers("/**").permitAll(); //모든 매핑 허용
            auth.requestMatchers("/h2-console/**").permitAll(); //모든 매핑 허용
            //메인페이지 및 서브페이지
            auth.requestMatchers("/", "/search").permitAll();
            //회원관련(모든 사용자)-로그인, 회원가입, 임시비밀번호발급
            auth.requestMatchers("/login", "/register", "/password").permitAll();
            //인증된 사용자만 접근 가능
            auth.requestMatchers("/modify","/logout").permitAll(); //수정,로그아웃
            //매핑명을 작업이름/매핑명
            //auth.requestMatchers("/modify/**").authenticated(); modify로 시작하는 모든 맵핑에 제한
        });

        //로그인 정보
        http.formLogin(login->login
                .loginPage("/member/login") //로그인은 /login맵핑으로
                .defaultSuccessUrl("/") //로그인 성공시 / 페이지로 이동
                .usernameParameter("email") //userid를 username으로 사용
                .permitAll() //모든 사용자가 로그인폼 사용
                .successHandler(new CustomAuthenticationSuccessHandler())); //로그인 성공시처리할 클래스

        //csrf 변조방지
        http.csrf(AbstractHttpConfigurer::disable);

        //로그아웃 정보
        http.logout(logout->logout
                .logoutUrl("/logout") //로그아웃 맵핑
                .logoutSuccessUrl("/login") //로그아웃 성공시 로그인 페이지로 이동
        );

        return http.build();
    }
}
