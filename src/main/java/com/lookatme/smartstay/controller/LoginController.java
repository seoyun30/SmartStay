package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class LoginController {

    private final MemberService memberService;

    @GetMapping("/adTerms") // 약관페이지(총판,매니져)
    public String adTerms(){
        return "member/adTerms";
    }

    @GetMapping("/terms") //약관페이지(유저)
    public String terms(){
        return "member/adTerms";
    }

    @GetMapping("/adSignup") //회원가입페이지(총판, 매니져)
    public String adSignupGet(){
        return "member/adSignup";
    }

    @GetMapping("/signup") //회원가입페이지(유저)
    public String signupGet(){
        return "member/signup";
    }

   @PostMapping("/signup") //회원가입 포스트
    public String signupPost(MemberDTO memberDTO){
        return "member/signup";
   }

   @GetMapping("/adLogin") //로그인페이지(총판,매니져)
    public String adLoginGet(){
        return "member/adSignup";
        //return "member/adLogin";
   }

   @GetMapping("/login") //로그인페이지(유저)
    public String loginGet(){
        return "member/login";
   }

   @PostMapping("/login") //로그인
    public String loginPost(MemberDTO memberDTO){
        return "member/adLogin";
        //return "member/login";
   }

   @PostMapping("/logout") //로그아웃
    public String logout(Principal principal){
        return null;
   }

   @GetMapping("/adFindID") // 아이디찾기(관리자)
    public String adFindIDGet(){
        return "member/adFindID";
   }

    @PostMapping("/adFindID") //아이디찾기(관리자)
    public String adFindIDPost(MemberDTO memberDTO){
        return "member/adFindID";
    }

   @GetMapping("/findID") //아이디찾기(유저)
    public String findIDGet(){
        return "member/findID";
   }

   @PostMapping("/findID") //아이디찾기(유저)
    public String findIDPost(MemberDTO memberDTO){
        return "member/findID";
   }

   @GetMapping("/adFindPW") //비밀번호찾기(관리자)
    public String adFindPWGet(){
        return "member/adFindPW";
   }

   @PostMapping("/adFindPW") //회원정보 확인(관리자)
    public String adFindPWPost(MemberDTO memberDTO){
        return "member/adFindPW";
   }

    @GetMapping("/findPW") //비밀번호찾기(유저)
    public String findPWGet(){
        return "member/findPW";
    }

    @PostMapping("/findPW") //회원정보 확인(유저)
    public String findPWPost(MemberDTO memberDTO){
        return "member/findPW";
    }

    @GetMapping("/adChangePW") //비밀번호 재설정(관리자)
    public String adChangePWGet(){
        return "member/adChangePW";
    }

    @PostMapping("/adChangePW") //비밀번호 재설정(관리자)
    public String adChangePW(MemberDTO memberDTO){
        return "member/adChangePW";
    }

    @GetMapping("/changePW") //비밀번호 재설정(관리자)
    public String changePWGet(){
        return "member/changePW";
    }

    @PostMapping("/changePW") //비밀번호 재설정(관리자)
    public String changePW(MemberDTO memberDTO){
        return "member/changePW";
    }


}