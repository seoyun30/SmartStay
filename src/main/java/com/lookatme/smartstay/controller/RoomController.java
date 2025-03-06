package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.HotelDTO;
import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.dto.PageResponseDTO;
import com.lookatme.smartstay.dto.RoomDTO;
import com.lookatme.smartstay.repository.HotelRepository;
import com.lookatme.smartstay.repository.ImageRepository;
import com.lookatme.smartstay.repository.RoomRepository;
import com.lookatme.smartstay.service.HotelService;
import com.lookatme.smartstay.service.ImageService;
import com.lookatme.smartstay.service.MemberService;
import com.lookatme.smartstay.service.RoomService;
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
import java.time.LocalTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/room")

public class RoomController {

    private final RoomService roomService;
    private final MemberService memberService;
    private final ImageService imageService;
    private final ImageRepository imageRepository;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final HotelService hotelService;

    @GetMapping("/roomRegister")
    public String roomRegisterGet(Model model, Principal principal) {

        if (principal == null) {
            return "redirect:/member/login";
        }

       HotelDTO hotelDTO = hotelService.myHotel(principal.getName());
       if (hotelDTO == null) {
         return "redirect:/adMain";
       }
       RoomDTO roomDTO = new RoomDTO();
       roomDTO.setHotelDTO(hotelDTO);
       roomDTO.setRoom_bed(2L);
       roomDTO.setIn_time(LocalTime.of(15, 0));
       roomDTO.setOut_time(LocalTime.of(11, 0));

       System.out.println("체크인 시간: " + roomDTO.getIn_time());
       System.out.println("체크아웃 시간: " + roomDTO.getOut_time());

       model.addAttribute("roomDTO", roomDTO);
       model.addAttribute("inTimeString", roomDTO.getIn_time().toString());
       model.addAttribute("outTimeString", roomDTO.getOut_time().toString());

       return "room/roomRegister";
    }

    @PostMapping("/roomRegister")
    public String roomRegisterPost(RoomDTO roomDTO, RedirectAttributes redirectAttributes,
                                   @RequestParam(value = "multipartFiles", required = false)  List<MultipartFile> multipartFiles,
                                   @RequestParam(value = "mainImageIndex", required = false, defaultValue = "0") Long mainImageIndex,
                                   Principal principal) throws Exception {

        log.info("컨트롤러에서 받은 값 : "  + roomDTO);


        HotelDTO hotelDTO = hotelService.myHotel(principal.getName());
        if (hotelDTO == null) {
            return "redirect:/adMain";
        }
        if (multipartFiles != null && multipartFiles.stream().allMatch(MultipartFile::isEmpty)) {
            multipartFiles = null;
        }
        try {
            roomService.roomRegister(roomDTO, hotelDTO.getHotel_num(), multipartFiles, mainImageIndex);
            redirectAttributes.addFlashAttribute("msg", "룸이 등록되었습니다.");
            return "redirect:/room/roomList";

        } catch (IllegalArgumentException e) {
            log.error("룸 등록 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/room/roomRegister";
        }
    }

    @GetMapping("/roomList")
    public String roomList(PageRequestDTO pageRequestDTO, Model model, Principal principal,
                           @RequestParam(defaultValue = "room_num") String sortField,
                           @RequestParam(defaultValue = "asc") String sortDir,
                           @RequestParam(required = false) String searchType,
                           @RequestParam(required = false) String searchKeyword) {

        if (principal == null) {
            return "redirect:/member/login";
        }

        HotelDTO hotelDTO = hotelService.myHotel(principal.getName());
        if (hotelDTO == null) {
            return "redirect:/adMain";
        }
        model.addAttribute("hotel_name", hotelDTO.getHotel_name());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);

        if (searchType != null && !searchType.isEmpty() && searchKeyword != null && !searchKeyword.isEmpty()) {
            List<RoomDTO> results = roomService.searchList(hotelDTO, searchType, searchKeyword, sortField, sortDir);
            model.addAttribute("results", results);
            model.addAttribute("searchType", searchType);
            model.addAttribute("searchKeyword", searchKeyword);
            model.addAttribute("isSearch", true);
        } else {
            PageResponseDTO<RoomDTO> pageResponseDTO = roomService.getRoomsByHotel(hotelDTO, pageRequestDTO, sortField, sortDir);
            model.addAttribute("pageResponseDTO", pageResponseDTO);
            model.addAttribute("isSearch", false);
        }
        return "room/roomList";
    }


    @GetMapping("/roomRead")
    public String roomRead(@RequestParam(required = false) Long room_num, Principal principal,
                           Model model, RedirectAttributes redirectAttributes){

        if (principal == null) {
            return "redirect:/member/login";
        }

        if (room_num == null) {
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 룸입니다.");
            return "redirect:/room/roomList";
        }

        try {
            RoomDTO roomDTO = roomService.roomRead(room_num);
            model.addAttribute("roomDTO", roomDTO);

            if (roomDTO.getHotelDTO() != null) {
                model.addAttribute("hotel_name", roomDTO.getHotelDTO().getHotel_name());
            }else {
                model.addAttribute("hotel_name", "호텔 정보 없음");
            }
            return "room/roomRead";
        }catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("msg", "해당 룸 정보를 찾을 수 없습니다.");
            return "redirect:/room/roomList";
        }catch (Exception e) {
            redirectAttributes.addFlashAttribute("msg", "알 수 없는 오류 발생");
            return "redirect:/room/roomList";
        }
    }

    @GetMapping("/roomModify")
    public String roomModifyGet(@RequestParam Long room_num, Model model, Principal principal){

        if (principal == null) {
            return "redirect:/member/login";
        }

        RoomDTO roomDTO = roomService.roomRead(room_num);

        HotelDTO hotelDTO = hotelService.myHotel(principal.getName());
        if (hotelDTO == null) {
            return "redirect:/adMain";
        }
        if (!roomDTO.getHotelDTO().getHotel_num().equals(hotelDTO.getHotel_num())) {
            throw new SecurityException("권한이 없습니다.");
        }

        LocalTime in_time = roomDTO.getIn_time();
        LocalTime out_time = roomDTO.getOut_time();

        if (in_time == null) {
            in_time = LocalTime.of(15, 0);
        }
        if (out_time == null) {
            out_time = LocalTime.of(11, 0);
        }

        model.addAttribute("roomDTO", roomDTO);
        model.addAttribute("inTimeString", in_time.toString());
        model.addAttribute("outTimeString", out_time.toString());

        return "room/roomModify";
    }

    @PostMapping("/roomModify")
    public String roomModifyPost(RoomDTO roomDTO, RedirectAttributes redirectAttributes,
                                 @RequestParam(value = "multipartFiles", required = false) List<MultipartFile> multipartFiles,
                                 @RequestParam(value = "delnumList", required = false) List<Long> delnumList,
                                 Principal principal) throws Exception {

        log.info("룸 수정 요청: {}", roomDTO);

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
            roomService.roomModify(roomDTO, multipartFiles, hotelDTO, delnumList);
            redirectAttributes.addFlashAttribute("msg", "룸 정보가 수정되었습니다. 룸 번호: " + roomDTO.getRoom_num());
        } catch (Exception e) {
            log.error("룸 수정 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "룸 수정 중 오류가 발생했습니다.");
            return "redirect:/room/roomModify?room_num=" + roomDTO.getRoom_num();
        }

        return "redirect:/room/roomList";
    }


    @PostMapping("/roomDelete")
    public String roomDelete(@RequestParam("id") Long room_num, RedirectAttributes redirectAttributes) {

        try {
            roomService.roomDelete(room_num);
            redirectAttributes.addFlashAttribute("successMessage", "삭제가 완료되었습니다.");
        }catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "삭제 중 오류가 발생했습니다.");
        }
        return "redirect:/room/roomList";
    }

    @DeleteMapping("/deleteImage/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable Long imageId) {
        try {
            imageService.deleteImage(imageId);
            return ResponseEntity.ok("이미지가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            log.error("이미지 삭제 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 삭제 실패");
        }
    }

}
