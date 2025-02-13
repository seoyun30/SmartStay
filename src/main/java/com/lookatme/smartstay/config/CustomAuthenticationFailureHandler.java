package com.lookatme.smartstay.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.spec.ECField;

@Component
@Log4j2
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {


        log.info("로그인실패:" + exception.getClass().getName());
        log.info("로그인 실패 메시지:" + exception.getMessage());

        if (exception instanceof BadCredentialsException) {
            log.info("비밀번호 오류:" + exception.getMessage());
            response.sendRedirect("/member/login?error=true");
            return;
        }


        if (exception instanceof AuthenticationException) {
            log.info("가입 승인 대기중인 관리자: " + exception.getMessage());
            response.sendRedirect("/member/login?error=approval"); // 승인 대기 상태
            return;
        }


        // 그 외 로그인 실패 처리 (ID 또는 비밀번호 오류 등)
        response.sendRedirect("/member/login?error=true");
    }
}
