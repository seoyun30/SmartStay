package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.ChiefDTO;
import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.dto.PageResponseDTO;
import com.lookatme.smartstay.service.ChiefService;
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

    private final ChiefService chiefService;

    @GetMapping("/")
    public String main(Model model) {

        List<ChiefDTO> list = chiefService.mainHotel();
        model.addAttribute("list", list);

        return "main";
    }
}
