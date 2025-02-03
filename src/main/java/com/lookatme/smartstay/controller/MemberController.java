package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.modelmapper.ModelMapper;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.sql.Struct;
import java.util.Collections;
import java.util.List;

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
    public String adMypageModifyGet(Principal principal, HttpSession session, Model model){

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

    @GetMapping("/adPowerList") // 권한승인(총판)
    public String adPowerList(Principal principal, Model model) {

        List<MemberDTO> adPowerList = memberService.adPowerList();
        model.addAttribute("adPowerList", adPowerList);

        return "adPowerList";
    }

    @GetMapping("/cmPowerList") // 권한승인(치프, 매니져)
    public String cmPowerList(Principal principal, Model model){

        List<MemberDTO> cmPowerList = memberService.cmPowerList(Long);
        model.addAttribute("cmPowerList", cmPowerList);


        return "cmPowerList";
    }

    @PostMapping("/power") //권한 승인
    public String power(@RequestParam("email") String email, MemberDTO memberDTO, Model model){

        try {
            memberService.powerMember(email);
            model.addAttribute("message", "승인완료");
        }catch (Exception e) {
            model.addAttribute("message", "승인오류" + e.getMessage());
        }
        return "redirect:/cmPowerList";
        //return "member/managerAcceptList";
    }




}
