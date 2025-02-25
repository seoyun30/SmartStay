package com.lookatme.smartstay.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

//2. 보안권한 설정, 암호화, 로그인, 로그아웃, csrf
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    //비밀번호를 암호화처리
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    //맵핑에 접근제한
    //static에 있는 폴더를 모두 사용으로 지정
    //html은 작업에 따라 사용할 권한 부여
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((auth) ->
        { //각 자원, html 권한
            //permitAll(); 모든 사용자 사용가능
            auth.requestMatchers("/assets/**", "/css/**", "/js/**").permitAll();
            //세부페이지 또는 Controller가 완성되면 삭제
            auth.requestMatchers("/**").permitAll(); //모든 매핑 허용
            auth.requestMatchers("/h2-console/**").permitAll(); //모든 매핑 허용
            //메인페이지 및 서브페이지
            auth.requestMatchers("/", "/search", "/images/**").permitAll();
            //회원관련(모든 사용자)-로그인, 회원가입, 임시비밀번호발급
           /* auth.requestMatchers("/login", "/logout", "/register", "/password").permitAll();*/
            auth.requestMatchers( "/member/login", "/register", "/password").permitAll();
            //인증된 사용자만 접근 가능
            auth.requestMatchers("/modify", "/member/logout").permitAll(); //수정,로그아웃
            //매핑명을 작업이름/매핑명
            auth.requestMatchers("/cart/**").authenticated(); //cart로 시작하는 모든 맵핑에 제한
            auth.requestMatchers("/pay/**").authenticated(); //pay로 시작하는 모든 맵핑에 제한
            auth.anyRequest().authenticated();
        });


        http.formLogin(login -> login
                .loginPage("/member/login") //로그인은 /login맵핑으로 //인증을 요할때 권한을 요할때 로그인이 되어있지 않다면
                // 해당 url로 이동   일반유저 로그인
                .loginProcessingUrl("/member/login")
                .usernameParameter("email") //userid를 username으로 사용
                .permitAll() //모든 사용자가 로그인폼 사용
                .failureHandler(new CustomAuthenticationFailureHandler()) //로그인 실패시 처리할 클래스
                .successHandler(new CustomAuthenticationSuccessHandler())
                );

        //csrf 변조방지
        http.csrf(AbstractHttpConfigurer::disable);

        //로그아웃 정보
        http.logout(logout -> logout
                        .logoutUrl("/member/logout")
                                .logoutSuccessHandler(new LogoutSeccessHandler())

        )
                .exceptionHandling(
                        a -> a.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                                .accessDeniedHandler(new AccessDeniedHandlerImpl())

                );


       /* http.logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/member/adLogout"))
                        .invalidateHttpSession(true)
                        *//*.logoutUrl("/adLogout") //로그아웃 맵핑*//*
                        .logoutSuccessUrl("/member/adLogin") //로그아웃 성공시 로그인 페이지로 이동
                )
                .exceptionHandling(
                        a -> a.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                                .accessDeniedHandler(new AccessDeniedHandlerImpl())

                )
        ;*/
        return http.build();
    }



}
