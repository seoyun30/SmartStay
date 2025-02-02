package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.HotelDTO;
import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.dto.PageResponseDTO;
import com.lookatme.smartstay.dto.RoomDTO;
import com.lookatme.smartstay.entity.Hotel;
import com.lookatme.smartstay.repository.HotelRepository;
import com.lookatme.smartstay.repository.ImageRepository;
import com.lookatme.smartstay.repository.RoomRepository;
import com.lookatme.smartstay.service.ImageService;
import com.lookatme.smartstay.service.MemberService;
import com.lookatme.smartstay.service.RoomService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
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

    @GetMapping("/roomRegister")
    public String roomRegisterGet(Model model, Principal principal) {

        String created_by = principal.getName();
        log.info("로그인한 사용자: {}", created_by);

        Hotel hotel = hotelRepository.findByCreate_by(created_by)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 호텔 정보를 찾을 수 없습니다."));
        log.info("조회된 호텔 정보: {}", hotel);

        RoomDTO roomDTO = new RoomDTO();
        HotelDTO hotelDTO = new HotelDTO();

        hotelDTO.setHotel_num(hotel.getHotel_num());
        hotelDTO.setHotel_name(hotel.getHotel_name());
        roomDTO.setHotelDTO(hotelDTO);

        model.addAttribute("roomDTO", roomDTO);

        return "room/roomRegister";
    }

    @PostMapping("/roomRegister")
    public String roomRegisterPost(@Valid RoomDTO roomDTO, BindingResult bindingResult, Principal principal,
                                   @RequestParam(value = "multipartFiles", required = false)  List<MultipartFile> multipartFiles,
                                   @RequestParam(value = "mainImageIndex", required = false, defaultValue = "0") Long mainImageIndex,
                                   RedirectAttributes redirectAttributes, Model model) throws Exception {

        log.info("컨트롤러에서 받은 값 : "  + roomDTO);

        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("result", "룸 등록 실패");
            return "redirect:/room/roomRegister";
        }
        String created_by = principal.getName();
        Hotel hotel = hotelRepository.findByCreate_by(created_by)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 호텔 정보를 찾을 수 없습니다."));

        try {
            roomService.roomRegister(roomDTO, hotel.getHotel_num(), multipartFiles, mainImageIndex);
            redirectAttributes.addFlashAttribute("msg", "룸이 등록되었습니다.");
            return "redirect:/room/roomList";

        } catch (IllegalArgumentException e) {
            log.error("룸 등록 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/room/roomRegister";
        }
    }

    @GetMapping("/roomList")
    public String roomList(PageRequestDTO pageRequestDTO, Model model, Principal principal){

        String created_by = principal.getName();

        Hotel hotel = hotelRepository.findByCreate_by(created_by)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 호텔 정보를 찾을 수 없습니다."));

        PageResponseDTO<RoomDTO> pageResponseDTO = roomService.getRoomsByHotel(hotel, pageRequestDTO);
        model.addAttribute("hotel_name", hotel.getHotel_name());
        log.info("PageResponseDTO: " + pageResponseDTO);
        model.addAttribute("pageResponseDTO", pageResponseDTO);

        return "room/roomList";
    }

    @GetMapping("/roomRead")
    public String roomRead(@RequestParam(required = false) Long room_num, Model model, RedirectAttributes redirectAttributes){

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

        RoomDTO roomDTO = roomService.roomRead(room_num);

        String email = principal.getName();
        Hotel hotel = hotelRepository.findByCreate_by(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 호텔 정보를 찾을 수 없습니다."));

        if (!roomDTO.getHotelDTO().getHotel_num().equals(hotel.getHotel_num())) {
            throw new SecurityException("권한이 없습니다.");
        }
        model.addAttribute("roomDTO", roomDTO);

        return "room/roomModify";
    }

    @PostMapping("/roomModify")
    public String roomModifyPost(@Valid RoomDTO roomDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes,
                                 @RequestParam(value = "multipartFiles", required = false) List<MultipartFile> multipartFiles,
                                 @RequestParam(value = "del_num", required = false) Long[] del_num,
                                 @RequestParam(value = "main_num", required = false) Long main_num, Principal principal) throws Exception {

        log.info("룸 수정 요청: {}", roomDTO);

        if (bindingResult.hasErrors()){
            log.info("유효성검사 오류");
            log.info(bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("result", "룸 정보를 다시 확인해주세요." );
            return "redirect:/room/roomModify?room_num=" + roomDTO.getRoom_num();
        }

        String email = principal.getName();
        Hotel hotel = hotelRepository.findByCreate_by(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 호텔 정보를 찾을 수 없습니다."));

        roomService.roomModify(roomDTO, multipartFiles, del_num, main_num, hotel);

        redirectAttributes.addFlashAttribute("msg", "룸 정보가 수정되었습니다. 룸 번호 : " + roomDTO.getRoom_num());

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
            imageRepository.deleteById(imageId);
            return ResponseEntity.ok("이미지가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            log.error("이미지 삭제 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 삭제 실패");
        }
    }

}
