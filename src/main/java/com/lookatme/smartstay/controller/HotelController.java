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

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/hotel")
public class HotelController {

    private final HotelService hotelService;
    private PagenationUtil pagenation;
    private final PagenationUtil pagenationUtil;

    //등록
    @GetMapping("/hotelRegister")
    public String hotelRegisterGet(Model model) {
        model.addAttribute("hotelDTO", new HotelDTO());
        return "hotel/hotelRegister";
    }
    @PostMapping("/hotelRegister")
    public String hotelRegisterPost(Model model, HotelDTO hotelDTO, Principal principal, //수정
                                    List<MultipartFile> multi, RedirectAttributes redirectAttributes) throws Exception {
        log.info("hotelRegister : " + hotelDTO);
        multi.forEach(multipartFile -> {log.info("multipartFile : " + multipartFile);});
        hotelService.insert(hotelDTO, principal, multi);
        redirectAttributes.addFlashAttribute("msg", "등록이 완료되었습니다.");
        return "redirect:/hotel/hotelList";
    }

    //목록
    @GetMapping("/hotelList") //슈퍼어드민만 사용
    public String hotelList(Principal principal, PageRequestDTO pageRequestDTO, Model model) {
        log.info("목록진입");

        List<HotelDTO> hotelDTOList =  hotelService.myHotelList(principal.getName());
        model.addAttribute("hotelDTOList", hotelDTOList);
        return "hotel/hotelList";
    }

    //상세보기
    @GetMapping("/hotelRead")
    public String hotelRead(Long hotel_num, Model model) {
        HotelDTO hotelDTO = hotelService.read(hotel_num);
        model.addAttribute("hotelDTO", hotelDTO);
        return "hotel/hotelRead";
    }

    //수정
    @GetMapping("/hotelModify")
    public String hotelModifyGet(Long hotel_num, Model model) {
        HotelDTO hotelDTO = hotelService.read(hotel_num);
        model.addAttribute("hotelDTO", hotelDTO);// end접속해제
        return "hotel/hotelModify";
    }
    @PostMapping("/hotelModify")
    public String hotelModifyPost(@Valid HotelDTO hotelDTO, BindingResult bindingResult, @RequestParam("delnumList") List<Long> delnumList,
                                  @RequestParam("multipartFiles") List<MultipartFile> multipartFiles, ImageDTO imageDTO, RedirectAttributes redirectAttributes) throws Exception {
        //@RequestParam("delnumList") List<Long> delnumList, @RequestParam("multipartFiles") List<MultipartFile> multipartFiles
        //사진등록, 사진삭제번호 할때 사용하는 방법

        if (bindingResult.hasErrors()) {
            log.info("유효성 검사 실패:" + bindingResult.getAllErrors());
            return "hotel/hotelModify";
        }
        log.info("유효성 통과");

        hotelService.update(hotelDTO, multipartFiles );
        redirectAttributes.addFlashAttribute("msg", "수정 완료되었습니다.");

        log.info("수정 완료");
        return "redirect:/hotel/hotelList";
    }
    //삭제
    @PostMapping("/hotelDelete")
    public String hotelDelete(long id) {
        log.info("삭제할 번호 :"+id);
        hotelService.delete(id);
        return "redirect:/hotel/hotelList";
    }
}
