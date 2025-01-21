package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.dto.PageResponseDTO;
import com.lookatme.smartstay.dto.RoomDTO;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.service.RoomService;
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
    public String roomRegisterPost(@Valid RoomDTO roomDTO,
                                   BindingResult bindingResult) throws Exception {

        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors());

            return "room/roomRegister";
        }


        return "redirect:/room/roomList";
    }

    @GetMapping("roomList")
    public String roomList(PageRequestDTO pageRequestDTO, Model model){

        PageResponseDTO<RoomDTO> pageResponseDTO =
                roomService.roomList(pageRequestDTO);

        model.addAttribute("pageResponseDTO", pageResponseDTO);

        return "room/roomList";
    }

}
