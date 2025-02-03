package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.HotelDTO;
import com.lookatme.smartstay.dto.RoomDTO;
import com.lookatme.smartstay.dto.RoomItemDTO;
import com.lookatme.smartstay.dto.RoomReserveDTO;
import com.lookatme.smartstay.entity.Room;
import com.lookatme.smartstay.repository.HotelRepository;
import com.lookatme.smartstay.repository.RoomRepository;
import com.lookatme.smartstay.service.RoomReserveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.security.Principal;
import java.util.List;

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
    public String roomReserveRegisterPost(@Valid RoomItemDTO roomItemDTO,
                                                  BindingResult bindingResult, Principal principal) {

        if (bindingResult.hasErrors()) {
            StringBuffer sb = new StringBuffer();

            List<FieldError> fieldErrors = bindingResult.getFieldErrors(); //각 필드 에러
            for (FieldError fieldError : fieldErrors) {
                log.info("필드 : "+ fieldError.getField() + " 메시지 : " + fieldError.getDefaultMessage());
                sb.append(fieldError.getDefaultMessage());
            }

            log.info(sb.toString());

            return sb.toString();
        }

        //받아온 값 체크
        log.info(roomItemDTO);

        roomReserveService.order(roomItemDTO, principal.getName());

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
