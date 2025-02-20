package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.repository.HotelRepository;
import com.lookatme.smartstay.repository.ImageRepository;
import com.lookatme.smartstay.service.HotelService;
import com.lookatme.smartstay.service.ImageService;
import com.lookatme.smartstay.service.MemberService;
import com.lookatme.smartstay.service.MenuService;
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
@RequestMapping("/menu")

public class MenuController {

    private final MenuService menuService;
    private final ImageService imageService;
    private final ImageRepository imageRepository;
    private final MemberService memberService;
    private final HotelRepository hotelRepository;
    private final HotelService hotelService;

    @GetMapping("/menuRegister")
    public String menuRegisterGet(Model model, Principal principal){

        if (principal == null) {
            return "redirect:/member/login";
        }

        HotelDTO hotelDTO = hotelService.myHotel(principal.getName());
        if (hotelDTO == null){
            return "redirect:/adMain";
        }
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setHotelDTO(hotelDTO);
        model.addAttribute("menuDTO", menuDTO);

        return "menu/menuRegister";
    }

    @PostMapping("/menuRegister")
    public String menuRegisterPost(MenuDTO menuDTO,RedirectAttributes redirectAttributes,
                                   @RequestParam(value = "multipartFiles", required = false)  List<MultipartFile> multipartFiles,
                                   @RequestParam(value = "mainImageIndex", required = false, defaultValue = "0") Long mainImageIndex,
                                   Principal principal) throws Exception {

        HotelDTO hotelDTO = hotelService.myHotel(principal.getName());
        if (hotelDTO == null){
            return "redirect:/adMain";
        }
        if (multipartFiles != null && multipartFiles.stream().allMatch(MultipartFile::isEmpty)){
            multipartFiles = null;
        }
        try {
            menuService.menuRegister(menuDTO, hotelDTO.getHotel_num(), multipartFiles, mainImageIndex);
            redirectAttributes.addFlashAttribute("msg", "메뉴가 등록되었습니다.");
            return "redirect:/menu/menuList";

        }catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
            return "redirect:/menu/menuRegister";
        }
    }

    @GetMapping("/menuList")
    public String menuList(PageRequestDTO pageRequestDTO, Model model, Principal principal,
                           @RequestParam(value = "query", required = false) String query) {

        if (principal == null) {
            return "redirect:/member/login";
        }

        HotelDTO hotelDTO = hotelService.myHotel(principal.getName());
        if (hotelDTO == null){
            return "redirect:/adMain";
        }

        if (query == null || query.trim().isEmpty()){
            PageResponseDTO<MenuDTO> pageResponseDTO = menuService.menuList(hotelDTO, pageRequestDTO);
            model.addAttribute("hotel_name", hotelDTO.getHotel_name());
            model.addAttribute("pageResponseDTO", pageResponseDTO);
            model.addAttribute("isSearch", false);
        }else {
            List<MenuDTO> results = menuService.searchList(query);
            model.addAttribute("results", results != null ? results : Collections.emptyList());
            model.addAttribute("query", query);
            model.addAttribute("isSearch", true);
            model.addAttribute("pageResponseDTO", null);
        }
        return "menu/menuList";
    }

    @GetMapping("/menuRead")
    public String menuRead(@RequestParam(required = false) Long menu_num, Principal principal,
                           Model model, RedirectAttributes redirectAttributes){

        if (principal == null) {
            return "redirect:/member/login";
        }

        if (menu_num == null){
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 메뉴입니다.");
            return "redirect:/menu/menuList";
        }

        try {
            MenuDTO menuDTO = menuService.menuRead(menu_num);
            model.addAttribute("menuDTO", menuDTO);

            if (menuDTO.getHotelDTO() != null) {
                model.addAttribute("hotel_name", menuDTO.getHotelDTO().getHotel_name());
            }else {
                model.addAttribute("hotel_name", "호텔 정보 없음");
            }
            return "menu/menuRead";

        }catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 메뉴입니다.");
            return "redirect:/menu/menuList";
        }
    }

    @GetMapping("/menuModify")
    public String menuModifyGet(@RequestParam Long menu_num, Model model, Principal principal){

        if (principal == null) {
            return "redirect:/member/login";
        }

        MenuDTO menuDTO = menuService.menuRead(menu_num);

        HotelDTO hotelDTO = hotelService.myHotel(principal.getName());

        if (hotelDTO == null){
            return "redirect:/adMain";
        }
       if (!menuDTO.getHotelDTO().getHotel_num().equals(hotelDTO.getHotel_num())){
           throw new SecurityException("권한이 없습니다.");
       }
       model.addAttribute("menuDTO", menuDTO);

       return "menu/menuModify";
    }

    @PostMapping("/menuModify")
    public String menuModifyPost(MenuDTO menuDTO, RedirectAttributes redirectAttributes,
                                 @RequestParam(value = "multipartFiles", required = false) List<MultipartFile> multipartFiles,
                                 @RequestParam(value = "delnumList", required = false) List<Long> delnumList,
                                 Principal principal) throws Exception {

        HotelDTO hotelDTO = hotelService.myHotel(principal.getName());
        if (hotelDTO == null){
            return "redirect:/adMain";
        }

        if (multipartFiles != null && multipartFiles.stream().allMatch(MultipartFile::isEmpty)){
            multipartFiles = null;
        }

        if (delnumList != null && delnumList.isEmpty()){
            delnumList = null;
        }

        try {
            menuService.menuModify(menuDTO, multipartFiles, hotelDTO, delnumList);
            redirectAttributes.addFlashAttribute("msg", "메뉴가 수정되었습니다. 메뉴 번호 : " + menuDTO.getMenu_num());

        }catch (Exception e) {
            log.error("메뉴 수정 중 오류 발생: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("msg", "메뉴 수정 중 오류가 발생했습니다.");
            return "redirect:/menu/menuModify?menu_num=" + menuDTO.getMenu_num();
        }

        return "redirect:/menu/menuList";
    }

    @PostMapping("/menuDelete")
    public String menuDelete(@RequestParam("id") Long menu_num, RedirectAttributes redirectAttributes) {

        try {
            menuService.menuDelete(menu_num);
            redirectAttributes.addFlashAttribute("successMessage", "삭제가 완료되었습니다.");
        }catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "삭제 중 오류가 발생했습니다.");
        }
        return "redirect:/menu/menuList";
    }

    @DeleteMapping("/deleteImage/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable Long imageId) {
        try {
            imageService.deleteImage(imageId);
            imageRepository.deleteById(imageId);
            return ResponseEntity.ok("이미지가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            log.error("이미지 삭제 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 삭제 실패");
        }
    }

    //한식 메뉴만
    @GetMapping("/koreanMenuList")
    @ResponseBody
    public ResponseEntity<PageResponseDTO<MenuDTO>> koreanMenuList(@RequestParam("hotel_num") Long hotel_num,
                                          PageRequestDTO pageRequestDTO) {

        PageResponseDTO<MenuDTO> menuDTOPageResponseDTO = menuService.koreanMenuList(hotel_num, pageRequestDTO);

        return ResponseEntity.ok(menuDTOPageResponseDTO);
    }

    //중식 메뉴만
    @GetMapping("/chineseMenuList")
    @ResponseBody
    public ResponseEntity<PageResponseDTO<MenuDTO>> chineseMenuList(@RequestParam("hotel_num") Long hotel_num,
                                                                   PageRequestDTO pageRequestDTO) {

        PageResponseDTO<MenuDTO> menuDTOPageResponseDTO = menuService.chineseMenuList(hotel_num, pageRequestDTO);

        return ResponseEntity.ok(menuDTOPageResponseDTO);
    }

    //일식 메뉴만
    @GetMapping("/japaneseMenuList")
    @ResponseBody
    public ResponseEntity<PageResponseDTO<MenuDTO>> japaneseMenuList(@RequestParam("hotel_num") Long hotel_num,
                                                                    PageRequestDTO pageRequestDTO) {

        PageResponseDTO<MenuDTO> menuDTOPageResponseDTO = menuService.japaneseMenuList(hotel_num, pageRequestDTO);

        return ResponseEntity.ok(menuDTOPageResponseDTO);
    }

    //양식 메뉴만
    @GetMapping("/westernMenuList")
    @ResponseBody
    public ResponseEntity<PageResponseDTO<MenuDTO>> westernMenuList(@RequestParam("hotel_num") Long hotel_num,
                                                                    PageRequestDTO pageRequestDTO) {

        PageResponseDTO<MenuDTO> menuDTOPageResponseDTO = menuService.westernMenuList(hotel_num, pageRequestDTO);

        return ResponseEntity.ok(menuDTOPageResponseDTO);
    }

    //분식 메뉴만
    @GetMapping("/snackMenuList")
    @ResponseBody
    public ResponseEntity<PageResponseDTO<MenuDTO>> snackMenuList(@RequestParam("hotel_num") Long hotel_num,
                                                                    PageRequestDTO pageRequestDTO) {

        PageResponseDTO<MenuDTO> menuDTOPageResponseDTO = menuService.snackMenuList(hotel_num, pageRequestDTO);

        return ResponseEntity.ok(menuDTOPageResponseDTO);
    }

    //음료 메뉴만
    @GetMapping("/drinkMenuList")
    @ResponseBody
    public ResponseEntity<PageResponseDTO<MenuDTO>> drinkMenuList(@RequestParam("hotel_num") Long hotel_num,
                                                                    PageRequestDTO pageRequestDTO) {

        PageResponseDTO<MenuDTO> menuDTOPageResponseDTO = menuService.drinkMenuList(hotel_num, pageRequestDTO);

        return ResponseEntity.ok(menuDTOPageResponseDTO);
    }

    //기타 메뉴만
    @GetMapping("/etcMenuList")
    @ResponseBody
    public ResponseEntity<PageResponseDTO<MenuDTO>> etcMenuList(@RequestParam("hotel_num") Long hotel_num,
                                                                    PageRequestDTO pageRequestDTO) {

        PageResponseDTO<MenuDTO> menuDTOPageResponseDTO = menuService.etcMenuList(hotel_num, pageRequestDTO);

        return ResponseEntity.ok(menuDTOPageResponseDTO);
    }
}

