package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.service.RoomReserveService;
import com.lookatme.smartstay.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/roomreserve")
public class RoomReserveController {
    private final RoomService roomService;
    private final RoomReserveService roomReserveService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/roomReserveRegister")
    public String roomReserveRegisterGet(Long room_num, Model model) {

        RoomDTO roomDTO = roomService.roomRead(room_num);
        log.info("룸 정보: " + roomDTO);

        model.addAttribute("roomDTO", roomDTO);

        return "roomreserve/roomReserveRegister";
    }

    //관리자 룸예약 목록
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/roomReserveList")
    public String roomReserveList(Principal principal, PageRequestDTO pageRequestDTO, Model model) {

        List<RoomDTO> roomDTOList = roomService.findmyRoom(principal.getName());
        model.addAttribute("roomDTOList", roomDTOList);

        PageResponseDTO<RoomReserveItemDTO> pageResponseDTO = roomReserveService.findRoomReservePage(principal.getName(), pageRequestDTO);
        model.addAttribute("pageResponseDTO", pageResponseDTO);

        return "roomreserve/roomReserveList";
    }

    //관리자 룸예약 정보 불러오기
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/findRoomReserve")
    @ResponseBody
    public ResponseEntity<List<RoomReserveItemDTO>> findRoomReserve(@RequestParam("room_num") Long room_num) {
        List<RoomReserveItemDTO> roomReserveItemDTOList = roomReserveService.findRoomReserve(room_num);
        return ResponseEntity.ok(roomReserveItemDTOList);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/roomReserveRead")
    public String roomReserveRead(Long roomreserveitem_num, Model model) {

        RoomReserveItemDTO roomReserveItemDTO = roomReserveService.findRoomReserveItemAd(roomreserveitem_num);
        model.addAttribute("roomReserveItemDTO", roomReserveItemDTO);

        return "roomreserve/roomReserveRead";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/state")
    @ResponseBody
    public ResponseEntity<?> stateChange(RoomReserveDTO roomReserveDTO) {

        RoomReserveDTO result = roomReserveService.stateChange(roomReserveDTO);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/checkReserve/{room_num}")
    public ResponseEntity<?> getReserveDatesForm(@PathVariable Long room_num) {
        if (room_num == null) {
            return ResponseEntity.badRequest().body(" 방 ID가 필요합니다.");
        }

        List<Map<String, String>> reserveDates = roomReserveService.getReserveDatesByRoom(room_num);

        if (reserveDates.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList()); // 빈 배열 반환
        }

        return ResponseEntity.ok(reserveDates);
    }



}
