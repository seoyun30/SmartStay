package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.RoomReserveItemDTO;
import com.lookatme.smartstay.repository.ImageRepository;
import com.lookatme.smartstay.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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


//    @PostMapping("/orderReserveRegister")
//    public String orederReserveRegisterPost(@Valid OrderItemDTO orderItemDTO, Model model,
//                                            BindingResult bindingResult, Principal principal) {
//
//        if (bindingResult.hasErrors()) {
//            StringBuffer sb = new StringBuffer();
//
//            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
//            for (FieldError fieldError : fieldErrors) {
//                sb.append(fieldError.getDefaultMessage());
//            }
//
//            return sb.toString();
//        }
//
//        try {
//            orderItemService.createOrder(orderItemDTO);
//        }catch (Exception e) {
//            model.addAttribute("errorMessage", e.getMessage());
//            return "orderreserve/orderReserveRegister";
//        }
//        model.addAttribute("successMessage", "룸서비스 주문 성공");
//
//        return "orderreserve/pay";
//    }

    @PostMapping("/pay")
    public String pay() {

        return "orderreserve/pay";
    }


    @GetMapping("/orderReserveList")
    public String orderReserveList(Principal principal, Model model) {

        //리스트 추가 필요
//        HotelDTO hotelDTO = hotelService.myHotel(principal.getName());
//        model.addAttribute("hotel_name", hotelDTO.getHotel_name());
//
//        String email = principal.getName();
//
//        List<OrderReserve> orders = orderReserveService.getOrdersByMemberEmail(email);
//
//        model.addAttribute("orders", orders);

        return "orderreserve/orderReserveList";
    }

    @GetMapping("/orderReserveRead")
    public String orderReserveRead() {
        return "orderreserve/orderReserveRead";
    }
}
