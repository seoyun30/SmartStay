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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MainController {

    private final HotelService hotelService;
    private final MemberService memberService;

    @GetMapping("/adMain")
    public String adMain(Model model, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return "redirect:/member/login";
        }

        String email = authentication.getName();
        MemberDTO memberDTO = memberService.findbyEmail(email);

        if (memberDTO == null) {
            log.error("회원 정보가 없습니다.");
            return "redirect:/member/login";
        }

        List<HotelDTO> hotelDTOS = hotelService.getHotelByMember(memberDTO);
        model.addAttribute("list", hotelDTOS);

        return "adMain";
    }

    @GetMapping("/")
    public String main(Model model) {

        List<HotelDTO> list = hotelService.hotelList();
        model.addAttribute("list", list);

        return "main";
    }

    @GetMapping("/mainHotel")
    public String mainHotel(Model model) {

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

    @GetMapping("/searchList")
    public String searchList(@RequestParam(value = "query", required = false) String query, Model model) {

        List<HotelDTO> results;

        if (query == null || query.trim().isEmpty()) {
            results = List.of();
            model.addAttribute("message","검색어를 입력하세요.");
        }else {
            results = hotelService.searchList(query);
        }

        model.addAttribute("results", results);
        model.addAttribute("query", query);

        return "searchList";
    }

    @GetMapping("/searchRead")
    public String searchRead(Long hotel_num, Model model) {

        HotelDTO hotelDTO = hotelService.read(hotel_num);
        model.addAttribute("hotelDTO", hotelDTO);

        return "searchRead";
    }

}
