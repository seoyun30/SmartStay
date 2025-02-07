package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.CartOrderDTO;
import com.lookatme.smartstay.dto.RoomItemDTO;
import com.lookatme.smartstay.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Log4j2
public class PayController {
    private final CartService cartService;

    @GetMapping("/pay")
    public String payPage(List<CartOrderDTO> cartOrderDTOList, Model model) {

//        List<RoomItemDTO> roomItemDTOList = cartService.findCartOrderRoomItem(cartOrderDTOList);
//        model.addAttribute("roomItemDTOList", roomItemDTOList);

        return "pay";
    }

    @PostMapping("/pay")
    public String pay(@RequestParam("cartOrderDTOList") List<CartOrderDTO> cartOrderDTOList, Model model) {
        // cartOrderDTOList를 DTO 객체로 변환
        List<CartOrderDTO> cartOrders = cartOrderDTOList.stream()
                .map(roomitemNum -> CartOrderDTO.builder().roomitem_num(roomitemNum.getRoomitem_num()).build())
                .collect(Collectors.toList());

        // 장바구니 상품 정보 조회
        List<RoomItemDTO> roomItemDTOList = cartService.findCartOrderRoomItem(cartOrders);

        model.addAttribute("roomItemDTOList", roomItemDTOList);

        return "pay"; // 결제 페이지 반환
    }
}
