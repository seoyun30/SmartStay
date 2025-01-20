package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/adMypage") // 마이페이지 정보보기(관리자)
    public String adMypage(Principal principal){
        return "member/adMypage";
    }

    @GetMapping("/mypage") // 마이페이지 정보보기(유저)
    public String mypage(Principal principal){
        return "member/mypage";
    }

    @GetMapping("/adMypageModify") // 마이페이지 정보수정(관리자)
    public String adMypageModifyGet(Principal principal){
        return "member/adMypageModify";
    }

    @GetMapping("/mypageModify") // 마이페이지 정보수정(유저)
    public String mypageModifyGet(Principal principal){
        return "member/mypageModify";
    }

    @PostMapping("/mypageModify") // 마이페이지 정보수정
    public String mypageModifyPost(MemberDTO memberDTO){
        return "member/mypageModify";
        //return "member/adMypageModify";
    }

    @GetMapping("/allMemberList") //전체 회원 목록
    public String allMemberList(){
        return "member/allMemberList";
    }

    @GetMapping("/chiefAcceptList") // 권한승인(총판)
    public String chiefAcceptList(){
        return "member/chiefAcceptList";
    }

    @GetMapping("/managerAcceptList") // 권한승인(매니져)
    public String managerAcceptList(){
        return "member/managerAcceptList";
    }

    @PostMapping("/accept") //권한 승인
    public String accept(MemberDTO memberDTO, Principal principal){
        return "member/chiefAcceptList";
        //return "member/managerAcceptList";
    }




}
