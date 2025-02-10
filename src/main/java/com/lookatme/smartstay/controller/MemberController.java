package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.dto.PageResponseDTO;
import com.lookatme.smartstay.repository.MemberRepository;
import com.lookatme.smartstay.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String mypageModifyPost(MemberDTO memberDTO, Model model){

        log.info("정보업데이트" + memberDTO);
        log.info("정보업데이트" + memberDTO.getPassword().length());

        if(memberDTO.getPassword().length() >= 1){
            log.info("비밀번호 유효성검사");

            if(memberDTO.getPassword().length() < 8  || memberDTO.getPassword().length() > 20){
                //리다이렉트
//                     리다이렉트시 메시지를 전달해야 한다.
//                     메시지는 : 비밀번호는 8자 이상이여야 합니다.
                log.info("비밀번호 유효성검사");

                String  msg = "비밀번호는 8 ~ 20 글자로 입력해주세요.";
//                redirectAttributes.addFlashAttribute("msg", msg);

//                return "redirect:/member/mypage";

                memberDTO = memberService.findbyEmail(memberDTO.getEmail());

                log.info("memberDTO" + memberDTO);

                if(memberDTO == null){
                    return "redirect:/member/mypage";
                }


                model.addAttribute("memberDTO", memberDTO);
                model.addAttribute("msg", msg);

                return "member/mypageModify";
            }

        }
//        if(!passwordEncoder.matches(memberDTO.getPassword(), member.getPassword())){
//            throw new IllegalStateException("현재 비밀번호가 일치하지 않습니다.");
//        } else {
//            passwordEncoder = new BCryptPasswordEncoder();
//
//            model.addAttribute("memberDTO", memberDTO);
//        }

        try {
            memberService.updateMember(memberDTO);

        }catch (Exception e){

            return "redirect:/member/mypage";
        }

        return "redirect:/member/mypage";

    }

    @GetMapping("/memberList") //전체 회원 목록
    public String memberList(Principal principal, PageRequestDTO pageRequestDTO, Model model){

        log.info("진입" );

        PageResponseDTO<MemberDTO> pageResponseDTO =
                memberService.memberList(pageRequestDTO);

        model.addAttribute("pageResponseDTO", pageResponseDTO);

        return "member/memberList";
    }

    @GetMapping("/searchMember")
    public String searchMember(@RequestParam("keyword") String keyword, Model model){

        List<MemberDTO> memberDTOList = memberService.searchMember(keyword);

        model.addAttribute("memberList", memberDTOList);
        model.addAttribute("keyword", keyword);

        return "member/memberList";
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
