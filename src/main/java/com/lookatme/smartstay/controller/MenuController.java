package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.dto.MenuDTO;
import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.service.MenuService;
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
@RequestMapping("/menu")

public class MenuController {

    private final MenuService menuService;

    @GetMapping("/menuRegister")
    public String menuRegisterGet(Model model, Principal principal){

        model.addAttribute("menuDTO", new MenuDTO());
        model.addAttribute("memberDTO", new MemberDTO());

        return "menu/menuRegister";
    }

    @PostMapping("/menuRegister")
    public String menuRegisterPost(@Valid MenuDTO menuDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws Exception {

        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors());

            return "menu/menuRegister";
        }

        Long menu_num = menuService.menuRegister(menuDTO);
        redirectAttributes.addFlashAttribute("msg", "메뉴가 등록되었습니다. 메뉴 번호 : " + menu_num);

        return "redirect:/menu/menuRead?menu_num=" + menu_num;
    }

    @GetMapping("/menuList")
    public String menuList(Model model){

        List<MenuDTO> list = menuService.menuList();
        model.addAttribute("list", list);

        return "menu/menuList";
    }

    @GetMapping("/menuRead")
    public String menuRead(Long menu_num, Model model, RedirectAttributes redirectAttributes){

        try {
            MenuDTO menuDTO = menuService.menuRead(menu_num);

            model.addAttribute("menuDTO", menuDTO);

            return "menu/menuRead";

        }catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 메뉴입니다.");
            return "redirect:/menu/menuList";
        }
    }

    @GetMapping("/menuModify")
    public String menuModifyGet(Long menu_num, PageRequestDTO pageRequestDTO, Model model){

        MenuDTO menuDTO = menuService.menuRead(menu_num);
        if (menuDTO != null){
            model.addAttribute("menuDTO", menuDTO);
            return "menu/menuModify";
        }else {
            return "redirect:/menu/menuList";
        }
    }

    @PostMapping("/menuModify")
    public String menuModifyPost(@Valid MenuDTO menuDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()){
            log.info(bindingResult.getAllErrors());

            return "menu/menuModify";
        }

        try {
            menuService.menuModify(menuDTO);
            redirectAttributes.addFlashAttribute("msg", "메뉴가 수정되었습니다. 메뉴 번호 : " + menuDTO.getMenu_num());

            return "redirect:/menu/menuRead?menu_num=" + menuDTO.getMenu_num();

        }catch (EntityNotFoundException e) {
            log.error("Menu not found: {}", menuDTO.getMenu_num(), e);
            redirectAttributes.addFlashAttribute("msg", "수정하려는 룸이 존재하지 않습니다.");

            return "redirect:/menu/menuList";

        }catch (Exception e) {
            log.error("Error during menu modification: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("msg", "메뉴 수정 중 오류가 발생했습니다.");

            return "redirect:/menu/menuList";
        }

    }
}

