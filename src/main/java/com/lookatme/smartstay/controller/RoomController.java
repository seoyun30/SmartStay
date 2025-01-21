package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.dto.PageResponseDTO;
import com.lookatme.smartstay.dto.RoomDTO;
import com.lookatme.smartstay.entity.Member;
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


    @GetMapping("roomRegister")
    public String roomRegisterGet(Model model){

        model.addAttribute("roomDTO", new RoomDTO());
        return "room/roomRegister";
    }

    @PostMapping("roomRegister")
    public String roomRegisterPost(@Valid RoomDTO roomDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws Exception {

        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors());

            return "room/roomRegister";
        }

        Long room_num = roomService.roomRegister(roomDTO);
        redirectAttributes.addFlashAttribute("msg", "룸이 등록되었습니다. 룸 번호 : " + room_num);

        return "redirect:/room/roomList";
    }

    @GetMapping("roomList")
    public String roomList(PageRequestDTO pageRequestDTO, Model model){

        PageResponseDTO<RoomDTO> pageResponseDTO = roomService.roomList(pageRequestDTO);

        model.addAttribute("pageResponseDTO", pageResponseDTO);

        return "room/roomList";
    }

    @GetMapping("roomRead")
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

    @GetMapping("room/roomModify")
    public String roomModifyGet(Long room_num, PageRequestDTO pageRequestDTO, Model model){

        RoomDTO roomDTO = roomService.roomRead(room_num);
        if (roomDTO != null){
            model.addAttribute("roomDTO", roomDTO);
            return "room/roomModify";
        }else {
            return "redirect:/room/roomList";
        }
    }

    @PostMapping("room/roomModify")
    public String roomModify(@Valid RoomDTO roomDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()){
            log.info(bindingResult.getAllErrors());

            return "room/roomModify";
        }

        roomService.roomModify(roomDTO);

        return null;
    }
}
