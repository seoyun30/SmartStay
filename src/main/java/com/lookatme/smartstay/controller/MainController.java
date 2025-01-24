package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.HotelDTO;
import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.service.HotelService;
import com.lookatme.smartstay.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MainController {

    private final HotelService hotelService;
    private final MemberService memberService;

    @GetMapping("/")
    public String main(Model model) {

        List<HotelDTO> list = hotelService.hotelList();
        model.addAttribute("list", list);

        return "main";
    }

    @GetMapping("/search")
    public String search(Model model) {

        List<HotelDTO> results = hotelService.hotelList();
        model.addAttribute("results", results);

        return "search";
    }

    @GetMapping("/adMain")
    public String adMain(Model model, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return "redirect:/login";  // 로그인되지 않았으면 로그인 페이지로 리다이렉트
        }

        String email = authentication.getName();
        MemberDTO memberDTO = memberService.findbyEmail(email);

        if (memberDTO == null) {
            log.error("회원 정보가 없습니다.");
            return "redirect:/login";  // 회원 정보가 없으면 로그인 페이지로 리다이렉트
        }

        List<HotelDTO> hotelDTOS = hotelService.getHotelByMember(memberDTO);
        model.addAttribute("list", hotelDTOS);

        return "adMain";
    }
}
