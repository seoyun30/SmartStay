package com.lookatme.smartstay.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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

        // 요청 URI에 따라 다른 페이지로 리다이렉트
            String requestURI = request.getRequestURI();
            String referer = request.getHeader("referer");
            //이전페이지를 가져오는 request객체의 header정보중 키값 referer
        System.out.println(requestURI);
        System.out.println(referer);
        System.out.println(referer);
        System.out.println(referer.substring( referer.lastIndexOf("/", referer.lastIndexOf("/")-1) ));
        System.out.println(referer.substring( referer.lastIndexOf("/", referer.lastIndexOf("/")-1) ));
        System.out.println(referer.substring( referer.lastIndexOf("/", referer.lastIndexOf("/")-1) ));

        String url = referer.substring( referer.lastIndexOf("/", referer.lastIndexOf("/")-1) );

        if (url.equals("/member/login")) {
            response.sendRedirect("/");
        } else if (url.equals("/member/adLogin")) {
            response.sendRedirect("/adMain");
        } else {
            response.sendRedirect("/"); // 기본 경로
        }

//        response.sendRedirect("/"); //시작페이지로 이동

    }
}
