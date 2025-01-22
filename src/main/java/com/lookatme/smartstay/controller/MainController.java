package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.ManagerDTO;
import com.lookatme.smartstay.service.ManagerService;
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

    private final ManagerService managerService;

    @GetMapping("/")
    public String main(Model model) {

        List<ManagerDTO> list = managerService.mainHotel();
        model.addAttribute("list", list);

        return "main";
    }

    @GetMapping("/search")
    public String search(Model model) {

        List<ManagerDTO> results = managerService.hotelList();
        model.addAttribute("results", results);

        return "search";
    }
}
