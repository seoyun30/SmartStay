package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.CareDTO;
import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.service.CareService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/care")

public class CareController {

    private final CareService careService;

    @GetMapping("/careRegister")
    public String careRegisterGet(Model model, Principal principal){

        model.addAttribute("careDTO", new CareDTO());
        model.addAttribute("memberDTO", new MemberDTO());

        return "care/careRegister";
    }

    @PostMapping("/careRegister")
    public String careRegisterPost(@Valid CareDTO careDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws Exception {

        if (bindingResult.hasErrors()){
            log.info(bindingResult.getAllErrors());

            return "care/careRegister";
        }

        Long care_num = careService.careRegister(careDTO);
        redirectAttributes.addFlashAttribute("msg", "케어서비스가 등록되었습니다. 케어서비스 번호 : " + care_num);

        return "redirect:/care/careRead?care_num=" + care_num;
    }

    @GetMapping("/careList")
    public String careList(Model model) {

        List<CareDTO> list = careService.careList();
        model.addAttribute("list", list);

        return "care/careList";
    }

    @GetMapping("/careRead")
    public String careRead(Long care_num, Model model, RedirectAttributes redirectAttributes) {

        try {
            CareDTO careDTO = careService.careRead(care_num);
            model.addAttribute("careDTO", careDTO);

            return "care/careRead";

        }catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 케어서비스입니다.");

            return "redirect:/care/careList";
        }
    }

    @GetMapping("/careModify")
    public String careModifyGet(Long care_num, Model model) {

        CareDTO careDTO = careService.careRead(care_num);

        if (careDTO != null) {
            model.addAttribute("careDTO", careDTO);

            return "care/careModify";
        }else {

            return "redirect:/care/careList";
        }
    }

    @PostMapping("/careModify")
    public String careModifyPost(@Valid CareDTO careDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()){
            log.info(bindingResult.getAllErrors());

            return "care/careModify";
        }

        careService.careModify(careDTO);

        redirectAttributes.addFlashAttribute("msg", "케어서비스가 등록되었습니다. 케어서비스 번호 : " + careDTO.getCare_num());

        return "redirect:/care/careRead?care_num=" + careDTO.getCare_num();
    }

}

