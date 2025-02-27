package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.repository.ImageRepository;
import com.lookatme.smartstay.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/orderreserve")
public class OrderReserveController {
    private final RoomReserveService roomReserveService;
    private final OrderReserveService orderReserveService;
    private final MenuService menuService;
    private final CareService careService;
    private final HotelService hotelService;
    private final OrderItemService orderItemService;
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;

    @GetMapping("/orderReserveRegister")
    public String orderReserveRegisterGet(Principal principal, Long roomreserveitem_num,
                                          Model model) {

        RoomReserveItemDTO roomReserveItemDTO = roomReserveService.findRoomReserveItem(roomreserveitem_num, principal.getName());

        model.addAttribute("roomReserveItemDTO", roomReserveItemDTO);

        return "orderreserve/orderReserveRegister";
    }

    @GetMapping("/orderReserveList")
    public String orderReserveList(PageRequestDTO pageRequestDTO, Principal principal, Model model) {

        HotelDTO hotelDTO = hotelService.myHotel(principal.getName());
        model.addAttribute("hotelDTO", hotelDTO);

        PageResponseDTO<OrderReserveItemDTO> pageResponseDTO = orderReserveService.findOrderReservePage(principal.getName(), pageRequestDTO);
        model.addAttribute("pageResponseDTO", pageResponseDTO);

        return "orderreserve/orderReserveList";
    }

    @GetMapping("/orderReserveRead")
    public String orderReserveRead() {
        return "orderreserve/orderReserveRead";
    }
}
