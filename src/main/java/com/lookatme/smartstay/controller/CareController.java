package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.repository.HotelRepository;
import com.lookatme.smartstay.repository.ImageRepository;
import com.lookatme.smartstay.service.CareService;
import com.lookatme.smartstay.service.HotelService;
import com.lookatme.smartstay.service.ImageService;
import com.lookatme.smartstay.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/care")

public class CareController {

    private final CareService careService;
    private final ImageService imageService;
    private final ImageRepository imageRepository;
    private final MemberService memberService;
    private final HotelRepository hotelRepository;
    private final HotelService hotelService;

    @GetMapping("/careRegister")
    public String careRegisterGet(Model model, Principal principal){

        if (principal == null){
            return "redirect:/member/login";
        }

        HotelDTO hotelDTO = hotelService.myHotel(principal.getName());
        if (hotelDTO == null) {
            return "redirect:/adMain";
        }
        CareDTO careDTO = new CareDTO();
        careDTO.setHotelDTO(hotelDTO);
        careDTO.setCare_price(0L);
        model.addAttribute("careDTO", careDTO);

        return "care/careRegister";
    }

    @PostMapping("/careRegister")
    public String careRegisterPost(CareDTO careDTO, RedirectAttributes redirectAttributes,
                                   @RequestParam(value = "multipartFiles", required = false)  List<MultipartFile> multipartFiles,
                                   @RequestParam(value = "mainImageIndex", required = false, defaultValue = "0") Long mainImageIndex,
                                   Principal principal) throws Exception {

        HotelDTO hotelDTO = hotelService.myHotel(principal.getName());
        if (hotelDTO == null) {
            return "redirect:/adMain";
        }
        if (multipartFiles != null && multipartFiles.stream().allMatch(MultipartFile::isEmpty)) {
            multipartFiles = null;
        }
        try {
            careService.careRegister(careDTO, hotelDTO.getHotel_num(), multipartFiles, mainImageIndex);
            redirectAttributes.addFlashAttribute("msg", "케어가 등록되었습니다.");
            return "redirect:/care/careList";

        }catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
            return "redirect:/care/careRegister";
        }
    }

    @GetMapping("/careList")
    public String careList(PageRequestDTO pageRequestDTO, Model model, Principal principal,
                           @RequestParam(defaultValue = "care_num") String sortField,
                           @RequestParam(defaultValue = "asc") String sortDir,
                           @RequestParam(value = "careName", required = false) String careName,
                           @RequestParam(value = "careDetail", required = false) String careDetail) {

        if (principal == null){
            return "redirect:/member/login";
        }

        HotelDTO hotelDTO = hotelService.myHotel(principal.getName());
        if (hotelDTO == null) {
            return "redirect:/adMain";
        }
        model.addAttribute("hotel_name", hotelDTO.getHotel_name());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);

        if ((careName == null || careName.trim().isEmpty()) && (careDetail == null || careDetail.trim().isEmpty())) {
            PageResponseDTO<CareDTO> pageResponseDTO = careService.careList(hotelDTO, pageRequestDTO, sortField, sortDir);
            model.addAttribute("pageResponseDTO", pageResponseDTO);
            model.addAttribute("isSearch", false);
        }else {
            List<CareDTO> results = careService.searchList(careName, careDetail, sortField, sortDir);
            model.addAttribute("results", results != null ? results : Collections.emptyList());
            model.addAttribute("careName", careName);
            model.addAttribute("careDetail", careDetail);
            model.addAttribute("isSearch", true);
            model.addAttribute("pageResponseDTO", null);
        }
        return "care/careList";
    }

    @GetMapping("/careRead")
    public String careRead(@RequestParam(required = false) Long care_num, Principal principal,
                           Model model, RedirectAttributes redirectAttributes) {

        if (principal == null){
            return "redirect:/member/login";
        }

        if (care_num == null) {
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 케어입니다.");
            return "redirect:/care/careList";
        }

        try {
            CareDTO careDTO = careService.careRead(care_num);
            model.addAttribute("careDTO", careDTO);

            if (careDTO.getHotelDTO() != null) {
                model.addAttribute("hotel_name", careDTO.getHotelDTO().getHotel_name());
            }else {
                model.addAttribute("hotel_name", "호텔 정보 없음");
            }
            return "care/careRead";

        }catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 케어입니다.");
            return "redirect:/care/careList";
        }
    }

    @GetMapping("/careModify")
    public String careModifyGet(@RequestParam Long care_num, Model model, Principal principal) {

        if (principal == null){
            return "redirect:/member/login";
        }

        CareDTO careDTO = careService.careRead(care_num);

        HotelDTO hotelDTO = hotelService.myHotel(principal.getName());

        if (hotelDTO == null) {
            return "redirect:/adMain";
        }

        if (!careDTO.getHotelDTO().getHotel_num().equals(hotelDTO.getHotel_num())) {
            throw new SecurityException("권한이 없습니다.");
        }
        model.addAttribute("careDTO", careDTO);

        return "care/careModify";
    }

    @PostMapping("/careModify")
    public String careModifyPost(CareDTO careDTO, RedirectAttributes redirectAttributes,
                                 @RequestParam(value = "multipartFiles", required = false) List<MultipartFile> multipartFiles,
                                 @RequestParam(value = "delnumList", required = false) List<Long> delnumList,
                                 Principal principal) throws Exception {

       HotelDTO hotelDTO = hotelService.myHotel(principal.getName());
       if (hotelDTO == null) {
           return "redirect:/adMain";
       }

       if (multipartFiles != null && multipartFiles.stream().allMatch(MultipartFile::isEmpty)) {
           multipartFiles = null;
       }

       if (delnumList != null && delnumList.isEmpty()) {
           delnumList = null;
       }

       try {
           careService.careModify(careDTO, multipartFiles, hotelDTO, delnumList);
           redirectAttributes.addFlashAttribute("msg", "케어가 수정되었습니다. 케어 번호 : " +careDTO.getCare_num());

       }catch (Exception e) {
           log.error("케어 수정 중 오류 발생 : {}", e.getMessage());
           redirectAttributes.addFlashAttribute("msg", "케어 수정 중 오류가 발생했습니다.");
           return "redirect:/care/careModify?care_num=" + careDTO.getCare_num();
       }

       return "redirect:/care/careList";
    }

    @PostMapping("/careDelete")
    public String careDelete(@RequestParam("id") Long care_num, RedirectAttributes redirectAttributes) {

       try {
           careService.careDelete(care_num);
           redirectAttributes.addFlashAttribute("successMessage", "삭제가 완료되었습니다.");
       }catch (Exception e) {
           redirectAttributes.addFlashAttribute("errorMessage", "삭제 중 오류가 발생했습니다.");
       }
       return "redirect:/care/careList";
    }

    @DeleteMapping("/deleteImage/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable Long imageId) {
        try {
            imageService.deleteImage(imageId);
            imageRepository.deleteById(imageId);
            return ResponseEntity.ok("이미지가 성공적으로 삭제되었습니다.");
        }catch (Exception e) {
            log.error("이미지 삭제 실패 : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 삭제 실패");
        }
    }

    @GetMapping("/findCareList")
    @ResponseBody
    public ResponseEntity<PageResponseDTO<CareDTO>> findCareList(@RequestParam("hotel_num") Long hotel_num,
                                                                  PageRequestDTO pageRequestDTO) {

        PageResponseDTO<CareDTO> careDTOPageResponseDTO = careService.findCareList(hotel_num, pageRequestDTO);

        return ResponseEntity.ok(careDTOPageResponseDTO);
    }
}

