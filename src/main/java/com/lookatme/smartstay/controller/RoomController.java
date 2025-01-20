package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.dto.RoomDTO;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/room")

public class RoomController {

    private final RoomService roomService;


    @GetMapping("roomRegister")
    public String roomRegisterGet(RoomDTO roomDTO){

        return "room/roomRegister";
    }

    @PostMapping("roomRegister")
    public String roomRegisterPost(@Valid RoomDTO roomDTO, MemberDTO memberDTO,
                                   BindingResult bindingResult, List<MultipartFile> multipartFileList){

        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors());

            return "room/roomRegister";
        }

        roomService.roomRegister(roomDTO, multipartFileList);

        return "redirect:/room/roomList";


    }

}
