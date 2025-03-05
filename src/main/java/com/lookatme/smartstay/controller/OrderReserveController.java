package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.repository.ImageRepository;
import com.lookatme.smartstay.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                                          Model model, RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/member/login";
        }

        RoomReserveItemDTO roomReserveItemDTO = roomReserveService.findRoomReserveItem(roomreserveitem_num, principal.getName());

        if (!roomReserveItemDTO.getRoomReserveDTO().getCheck_state().name().equals("IN")){
            redirectAttributes.addFlashAttribute("msg", "룸서비스를 주문할 수 없는 상태입니다.");
            return "redirect:/member/myRoomReserveRead?roomreserveitem_num=" + roomreserveitem_num;
        }

        model.addAttribute("roomReserveItemDTO", roomReserveItemDTO);

        return "orderreserve/orderReserveRegister";
    }

    @GetMapping("/orderReserveList")
    public String orderReserveList(PageRequestDTO pageRequestDTO, Principal principal, Model model) {

        if (principal == null) {
            return "redirect:/member/login";
        }

        HotelDTO hotelDTO = hotelService.myHotel(principal.getName());
        model.addAttribute("hotelDTO", hotelDTO);

        PageResponseDTO<OrderReserveItemDTO> pageResponseDTO = orderReserveService.findOrderReservePage(principal.getName(), pageRequestDTO);
        model.addAttribute("pageResponseDTO", pageResponseDTO);

        return "orderreserve/orderReserveList";
    }

    @GetMapping("/orderReserveRead")
    public String orderReserveRead(Long serviceitem_num, Model model) {
        OrderReserveItemDTO orderReserveItemDTO = orderReserveService.findOrderReserveItemAd(serviceitem_num);
        model.addAttribute("orderReserveItemDTO", orderReserveItemDTO);
        return "orderreserve/orderReserveRead";
    }

    //주문 상태 변경
    @PostMapping("/state")
    @ResponseBody
    public ResponseEntity<?> stateChange(OrderReserveDTO orderReserveDTO) {

        OrderReserveDTO result = orderReserveService.stateChange(orderReserveDTO);
        return ResponseEntity.ok(result);
    }

}
