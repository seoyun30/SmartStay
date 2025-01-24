package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.dto.PageResponseDTO;
import com.lookatme.smartstay.dto.RoomDTO;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.service.MemberService;
import com.lookatme.smartstay.service.RoomService;
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
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/roomRegister")
    public String roomRegisterGet(Model model, Principal principal){

        model.addAttribute("roomDTO", new RoomDTO());
        model.addAttribute("memberDTO", new MemberDTO());

        return "room/roomRegister";
    }

    @PostMapping("/roomRegister")
    public String roomRegisterPost(@Valid RoomDTO roomDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws Exception {

        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors());

            return "room/roomRegister";
        }

        Long room_num = roomService.roomRegister(roomDTO);
        redirectAttributes.addFlashAttribute("msg", "룸이 등록되었습니다. 룸 번호 : " + room_num);

        return "redirect:/room/roomRead?room_num=" + room_num;
    }

    @GetMapping("/roomList")
    public String roomList(PageRequestDTO pageRequestDTO, Model model){

//        PageResponseDTO<RoomDTO> pageResponseDTO = roomService.roomList(pageRequestDTO);
//        log.info(pageResponseDTO);
//
//        model.addAttribute("pageResponseDTO", pageResponseDTO);

        List<RoomDTO> list = roomService.list();
        model.addAttribute("list", list);

        return "room/roomList";
    }

    @GetMapping("/roomRead")
    public String roomRead(Long room_num, Model model, RedirectAttributes redirectAttributes){

        try {
            RoomDTO roomDTO = roomService.roomRead(room_num);

            model.addAttribute("roomDTO", roomDTO);

            return "room/roomRead";

        }catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 룸입니다.");
            return "redirect:/room/roomList";
        }
    }

    @GetMapping("/roomModify")
    public String roomModifyGet(Long room_num, PageRequestDTO pageRequestDTO, Model model){

        RoomDTO roomDTO = roomService.roomRead(room_num);
        if (roomDTO != null){
            model.addAttribute("roomDTO", roomDTO);
            return "room/roomModify";
        }else {
            return "redirect:/room/roomList";
        }
    }

    @PostMapping("/roomModify")
    public String roomModifyPost(@Valid RoomDTO roomDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        log.info(roomDTO);

        if (bindingResult.hasErrors()){
            log.info(bindingResult.getAllErrors());

            return "room/roomModify";
        }

        try {
            roomService.roomModify(roomDTO);
            redirectAttributes.addFlashAttribute("msg", "룸 정보가 수정되었습니다. 룸 번호 : " + roomDTO.getRoom_num());
            return "redirect:/room/roomRead?room_num=" + roomDTO.getRoom_num();
        } catch (EntityNotFoundException e) {
            log.error("Room not found: {}", roomDTO.getRoom_num(), e);
            redirectAttributes.addFlashAttribute("msg", "수정하려는 룸이 존재하지 않습니다.");
            return "redirect:/room/roomList";
        } catch (Exception e) {
            log.error("Error during room modification", e);
            redirectAttributes.addFlashAttribute("msg", "룸 수정 중 오류가 발생했습니다.");
            return "redirect:/room/roomList";
        }
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
}
