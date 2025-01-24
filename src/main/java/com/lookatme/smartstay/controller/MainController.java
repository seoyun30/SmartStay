package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.HotelDTO;
import com.lookatme.smartstay.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MainController {

    private final HotelService hotelService;

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
    public String adMain(Model model) {

        return "adMain";
    }
}
