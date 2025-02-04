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
import org.springframework.web.bind.annotation.*;

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
    public String adPowerList(Model model) {

        List<MemberDTO> adPowerList = memberService.adPowerList(null);
        model.addAttribute("adPowerList", adPowerList);

        return "member/adPowerList";


    }

    @PostMapping("/adPowerMember") //권한 승인
    public String adePowerMember(@RequestParam("email") String email, Model model){

        try {
            List<MemberDTO> adPowerList = memberService.adPowerList(email);
            model.addAttribute("adPowerList", adPowerList);
            model.addAttribute("message", "변경완료");

        } catch (Exception e) {
            model.addAttribute("message", "변경오류");
        }

        return "redirect:/member/adPowerList";

    }

    @GetMapping("/cmPowerList") // 권한승인(치프, 매니져)
    public String cmPowerList(Principal principal, Model model){

        log.info(principal);
        log.info(principal.getName());
        List<MemberDTO> cmPowerList = memberService.cmPowerList(principal.getName());
        model.addAttribute("cmPowerList", cmPowerList);


        return "member/cmPowerList";
    }


    @PostMapping("/powerMember") //권한 승인
    public String powerMember(@RequestParam("email") String email, Model model){


        try {
            memberService.powerMember(email);
            List<MemberDTO> cmPowerList = memberService.cmPowerList(email);
            model.addAttribute("cmPowerList", cmPowerList);
            model.addAttribute("message", "변경완료");

        } catch (Exception e) {
            model.addAttribute("message", "변경오류");
        }
        return "redirect:/member/cmPowerList";

    }


}
