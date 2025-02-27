package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.RoomDTO;
import com.lookatme.smartstay.dto.RoomReserveItemDTO;
import com.lookatme.smartstay.service.RoomReserveService;
import com.lookatme.smartstay.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/roomreserve")
public class RoomReserveController {
    private final RoomService roomService;
    private final RoomReserveService roomReserveService;

    @GetMapping("/roomReserveRegister")
    public String roomReserveRegisterGet(Long room_num, Model model) {

        RoomDTO roomDTO = roomService.roomRead(room_num);
        log.info("룸 정보: " + roomDTO);

        model.addAttribute("roomDTO", roomDTO);

        return "roomreserve/roomReserveRegister";
    }

    //관리자 룸예약 목록
    @GetMapping("/roomReserveList")
    public String roomReserveList(Principal principal, Model model) {

        List<RoomDTO> roomDTOList = roomService.findmyRoom(principal.getName());
        model.addAttribute("roomDTOList", roomDTOList);

        return "roomreserve/roomReserveList";
    }

    //관리자 룸예약 정보 불러오기
    @GetMapping("/findRoomReserve")
    @ResponseBody
    public ResponseEntity<List<RoomReserveItemDTO>> findRoomReserve(@RequestParam("room_num") Long room_num) {
        List<RoomReserveItemDTO> roomReserveItemDTOList = roomReserveService.findRoomReserve(room_num);
        return ResponseEntity.ok(roomReserveItemDTOList);
    }

    @GetMapping("/roomReserveRead")
    public String roomReserveRead() {
        return "roomreserve/roomReserveRead";
    }



}
