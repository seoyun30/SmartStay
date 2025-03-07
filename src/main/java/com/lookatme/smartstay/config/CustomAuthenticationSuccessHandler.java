package com.lookatme.smartstay.config;

import com.lookatme.smartstay.entity.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import okhttp3.Request;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

//사용자 인증 성공처리 핸들러
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        HttpSession session = request.getSession();

        //로그인에 사용한 정보를 읽어와서 저장
        //UserDetails을 사용자가 오버라이딩 변경을 하면 해당정보를 전달
        UserDetails user = (UserDetails) authentication.getPrincipal();

//        Member member = (Member) user;  // 만약 UserDetails가 Member 객체라면
//        Long hotelNum = member.getHotel().getHotel_num();  // Member에서 hotel_num을 얻음
//
//        // 세션에 hotel_num 저장
//        session.setAttribute("hotel_num", hotelNum);

        // 요청 URI에 따라 다른 페이지로 리다이렉트
//            String requestURI = request.getRequestURI();
//            String referer = request.getHeader("referer");
//            //이전페이지를 가져오는 request객체의 header정보중 키값 referer
//        System.out.println(requestURI);
//        System.out.println(referer);
//        System.out.println(referer);
//        System.out.println(referer.substring( referer.lastIndexOf("/", referer.lastIndexOf("/")-1) ));
//        System.out.println(referer.substring( referer.lastIndexOf("/", referer.lastIndexOf("/")-1) ));
//        System.out.println(referer.substring( referer.lastIndexOf("/", referer.lastIndexOf("/")-1) ));
//
//        String url = referer.substring( referer.lastIndexOf("/", referer.lastIndexOf("/")-1) );
//
//        if (url.equals("/member/login") || url.equals("/member/login?error")) {
//            response.sendRedirect("/");
//        } else if (url.equals("/member/adLogin") || url.equals("/member/adLogin?error")) {
//            response.sendRedirect("/adMain");
//        } else {
//           // 기본 경로
//            response.sendRedirect("/");
//        }

        System.out.println(user.toString());
        System.out.println(user.toString());
        System.out.println(user.toString());
        System.out.println(user.toString());

        String role = null;
        if(authentication != null){
            role = authentication.getAuthorities().iterator().next().getAuthority();
            System.out.println(authentication.getAuthorities().iterator().next().getAuthority());
            System.out.println(authentication.getAuthorities().iterator().next().getAuthority());
            System.out.println(authentication.getAuthorities().iterator().next().getAuthority());
        }


        String url = "/";
        if(role != null && role.equals("SUPERADMIN")) {
            url = "/adMain";
            System.out.println(url);

        } else if (role != null && role.equals("CHIEF")) {
            url = "/adMain";
            System.out.println(url);

        } else if (role != null && role.equals("MANAGER")) {
            url = "/adMain";
            System.out.println(url);

        } else if (role != null && role.equals("USER")) {
            url = "/";
            System.out.println(url);
        }  else if (role != null && role.equals("대기")) {
            url = "/member/login?error=approval";
            System.out.println(url);
        }

//        response.sendRedirect(url);
//        response.sendRedirect("/"); //시작페이지로 이동



        String referer = request.getHeader("referer");

        if (referer != null && referer.contains("/member/loginPW")) {
            url = "/member/changePW";
        }
            response.sendRedirect(url);
    }

}

