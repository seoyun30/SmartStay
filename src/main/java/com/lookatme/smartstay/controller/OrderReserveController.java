package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.HotelDTO;
import com.lookatme.smartstay.dto.OrderItemDTO;
import com.lookatme.smartstay.entity.Care;
import com.lookatme.smartstay.entity.Menu;
import com.lookatme.smartstay.entity.OrderReserve;
import com.lookatme.smartstay.service.HotelService;
import com.lookatme.smartstay.service.OrderReserveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/orderreserve")
public class OrderReserveController {
    private final OrderReserveService orderReserveService;
    private final HotelService hotelService;

    @GetMapping("/orderReserveRegister")
    public String orderReserveRegisterGet(Model model) {

        List<Menu> menus = orderReserveService.getAllMenus();

        List<Care> cares = orderReserveService.getAllCares();

        model.addAttribute("menus", menus);
        model.addAttribute("cares", cares);

        return "orderreserve/orderReserveRegister";
    }


    @PostMapping("/orderReserveRegister")
    public String orederReserveRegisterPost(@Valid OrderItemDTO orderItemDTO,
                                            BindingResult bindingResult, Principal principal) {

        if (bindingResult.hasErrors()) {
            StringBuffer sb = new StringBuffer();

            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return sb.toString();
        }

        return "/pay";
    }


    @GetMapping("/orderReserveList")
    public String orderReserveList(Principal principal, Model model) {

        HotelDTO hotelDTO = hotelService.myHotel(principal.getName());
        model.addAttribute("hotel_name", hotelDTO.getHotel_name());

        String email = principal.getName();

        List<OrderReserve> orders = orderReserveService.getOrdersByMemberEmail(email);

        model.addAttribute("orders", orders);

        return "orderreserve/orderReserveList";
    }

    @GetMapping("/orderReserveRead")
    public String orderReserveRead() {
        return "orderreserve/orderReserveRead";
    }
}
