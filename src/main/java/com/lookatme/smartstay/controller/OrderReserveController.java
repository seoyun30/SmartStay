package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.HotelDTO;
import com.lookatme.smartstay.dto.ImageDTO;
import com.lookatme.smartstay.dto.OrderItemDTO;
import com.lookatme.smartstay.entity.Care;
import com.lookatme.smartstay.entity.Image;
import com.lookatme.smartstay.entity.Menu;
import com.lookatme.smartstay.entity.OrderReserve;
import com.lookatme.smartstay.repository.ImageRepository;
import com.lookatme.smartstay.service.HotelService;
import com.lookatme.smartstay.service.OrderItemService;
import com.lookatme.smartstay.service.OrderReserveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/orderreserve")
public class OrderReserveController {
    private final OrderReserveService orderReserveService;
    private final HotelService hotelService;
    private final OrderItemService orderItemService;
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;

    @GetMapping("/orderReserveRegister")
    public String orderReserveRegisterGet(Model model) {

        List<Menu> menus = orderReserveService.getAllMenus();
        List<Care> cares = orderReserveService.getAllCares();

        Map<Long, List<ImageDTO>> menuImagesMap = new HashMap<>();
        for (Menu menu : menus) {
            List<Image> menuImages = imageRepository.findByTarget("menu", menu.getMenu_num());
            List<ImageDTO> menuImagesDTOs = menuImages.stream()
                    .map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList());
            menuImagesMap.put(menu.getMenu_num(), menuImagesDTOs);
        }

        Map<Long, List<ImageDTO>> careImagesMap = new HashMap<>();
        for (Care care : cares) {
            List<Image> careImages = imageRepository.findByTarget("care", care.getCare_num());
            List<ImageDTO> careImageDTOs = careImages.stream()
                    .map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList());
            careImagesMap.put(care.getCare_num(), careImageDTOs);
        }

        model.addAttribute("menus", menus);
        model.addAttribute("cares", cares);
        model.addAttribute("menuImagesMap", menuImagesMap);
        model.addAttribute("careImagesMap", careImagesMap);

        return "orderreserve/orderReserveRegister";
    }


    @PostMapping("/orderReserveRegister")
    public String orederReserveRegisterPost(@Valid OrderItemDTO orderItemDTO, Model model,
                                            BindingResult bindingResult, Principal principal) {

        if (bindingResult.hasErrors()) {
            StringBuffer sb = new StringBuffer();

            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return sb.toString();
        }

        try {
            orderItemService.createOrder(orderItemDTO);
        }catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "orderreserve/orderReserveRegister";
        }
        model.addAttribute("successMessage", "룸서비스 주문 성공");

        return "orderreserve/pay";
    }

    @PostMapping("/pay")
    public String pay() {

        return "orderreserve/pay";
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
