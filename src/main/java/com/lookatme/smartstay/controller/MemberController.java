package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.repository.MemberRepository;
import com.lookatme.smartstay.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @GetMapping("/adMypage") // 마이페이지 정보보기(관리자)
    public String adMypage(Principal principal){
        return "member/adMypage";
    }

    @GetMapping("/mypage") // 마이페이지 정보보기(유저)
    public String mypage(Model model, Authentication authentication) {

        String email = authentication.getName();
        MemberDTO memberDTO = memberService.readMember(email);
        log.info(memberDTO);
        model.addAttribute("memberDTO", memberDTO);
        return "member/mypage";

    }

    @GetMapping("/adMypageModify") // 마이페이지 정보수정(관리자)
    public String adMypageModifyGet(Principal principal, HttpSession session, Model model){

        return "member/adMypageModify";
    }

    @GetMapping("/mypageModify") // 마이페이지 정보수정(유저)
    public String mypageModifyGet(Model model, Principal principal){

        MemberDTO memberDTO = memberService.findbyEmail(principal.getName());

        log.info("memberDTO" + memberDTO);

        if(memberDTO == null){
            return "redirect:/member/mypage";
        }

        model.addAttribute("memberDTO", memberDTO);

        return "member/mypageModify";
    }

    @PostMapping("/mypageModify") // 마이페이지 정보수정
    public String mypageModifyPost(@Valid MemberDTO memberDTO, BindingResult bindingResult, Principal principal, Model model){

        log.info("정보업데이트" + memberDTO);

        if(bindingResult.hasErrors()){
            log.info("에러발생됨 " + bindingResult.getAllErrors());

            model.addAttribute("memberDTO", memberDTO);

            return "member/mypageModify";
        }

        try {
            Member member = memberRepository.findByEmail(principal.getName());


            if(memberDTO.getPassword() != null && !memberDTO.getPassword().trim().isEmpty()){
                String encodedPassword = new BCryptPasswordEncoder().encode(memberDTO.getPassword());
                memberDTO.setPassword(encodedPassword);

            } else {
                Member existingMember = memberRepository.findByEmail(principal.getName());
                memberDTO.setPassword(existingMember.getPassword());
            }

            memberService.updateMember(principal.getName(), memberDTO);

        }catch (EntityNotFoundException e) {
            return "redirect:/member/mypage";
        }
        return "redirect:/member/mypage";

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
