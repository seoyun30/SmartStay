package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.HotelDTO;
import com.lookatme.smartstay.dto.RoomDTO;
import com.lookatme.smartstay.dto.RoomItemDTO;
import com.lookatme.smartstay.dto.RoomReserveDTO;
import com.lookatme.smartstay.entity.Room;
import com.lookatme.smartstay.repository.HotelRepository;
import com.lookatme.smartstay.repository.RoomRepository;
import com.lookatme.smartstay.service.RoomReserveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/roomreserve")
public class RoomReserveController {
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final RoomReserveService roomReserveService;

    @GetMapping("/roomReserveRegister")
    public String roomReserveRegisterGet(Model model) {

        Room room = roomRepository.findById(1L).orElseThrow();
        log.info("룸 정보: " + room);

        HotelDTO hotelDTO = modelMapper.map(room.getHotel(), HotelDTO.class);
        RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);
        roomDTO.setHotelDTO(hotelDTO);
        log.info("룸 정보: " + roomDTO);

        model.addAttribute("roomDTO", roomDTO);

        return "roomreserve/roomReserveRegister";
    }

    @PostMapping("/roomReserveRegister")
    public String roomReserveRegisterPost(RoomItemDTO roomItemDTO) {

        log.info(roomItemDTO);

        return "/pay";
    }

    @GetMapping("/roomReserveList")
    public String roomReserveList() {
        return "roomreserve/roomReserveList";
    }

    @GetMapping("/roomReserveRead")
    public String roomReserveRead() {
        return "roomreserve/roomReserveRead";
    }



}
