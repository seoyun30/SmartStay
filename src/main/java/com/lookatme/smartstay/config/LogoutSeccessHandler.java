package com.lookatme.smartstay.config;

import com.lookatme.smartstay.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LogoutSeccessHandler extends SimpleUrlLogoutSuccessHandler {




    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {

        String role = null;
        if(authentication != null){
            role = authentication.getAuthorities().iterator().next().getAuthority();
            System.out.println(authentication.getAuthorities().iterator().next().getAuthority());
            System.out.println(authentication.getAuthorities().iterator().next().getAuthority());
            System.out.println(authentication.getAuthorities().iterator().next().getAuthority());
        }

        HttpSession session = request.getSession(false);

        String url = "/";
        if(role != null && role.equals("CHIEF")) {
            url = "/a";
            System.out.println(url);
        }else if (role != null && role.equals("USER")) {
            url = "/";
            System.out.println(url);

        }
        System.out.println(url);

        //로그아웃시 섹션을 제거한다.
        if(session != null) {
            session.invalidate();
        }


        super.setDefaultTargetUrl(url);
        super.onLogoutSuccess(request, response, authentication);
    }
}


