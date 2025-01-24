package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.Util.PagenationUtil;
import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/manager")
public class HotelController {

    private final HotelService hotelService;
    private PagenationUtil pagenation;
    private final PagenationUtil pagenationUtil;

    //매장 상세보기
    @GetMapping("/managerRead")
    public String managerRead(Long hotel_num, Model model) {
        HotelDTO hotelDTO = hotelService.hotelRead(hotel_num);
        model.addAttribute("manager", hotelDTO);
        return "manager/managerRead";
    }

    //매장 수정
    @GetMapping("/managerModify")
    public String managerModifyGet(Long hotel_num, Model model) {
        HotelDTO hotelDTO = hotelService.hotelRead(hotel_num);
        model.addAttribute("manager", hotelDTO);
        return "manager/managerModify";
    }
    @PostMapping("/managerModify")
    public String managerModifyPost(@Valid HotelDTO hotelDTO, BindingResult bindingResult, @RequestParam("delnumList") List<Long> delnumList,
                                    @RequestParam("multipartFiles") List<MultipartFile> multipartFiles, ImageDTO imageDTO, RedirectAttributes redirectAttributes) throws Exception {
        if (bindingResult.hasErrors()) {
            log.info("유효성 검사 실패 :"+bindingResult.getAllErrors());
            return "manager/managerModify";
        }

        hotelService.hotelUpdate(hotelDTO, multipartFiles);
        redirectAttributes.addFlashAttribute("msg", "수정 완료되었습니다.");

        return "redirect:/manager/managerRead";
    }
}
