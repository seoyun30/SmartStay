package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.CartOrderDTO;
import com.lookatme.smartstay.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class PayController {
    private final CartService cartService;

//    @GetMapping("/pay")
//    public String payPage(CartOrderDTO cartOrderDTO, Model model) {
//
//        log.info("get 방식 : " + cartOrderDTO);
//        List<CartOrderDTO> cartOrderDTOList = cartOrderDTO.getCartOrderDTOList();
//        List<RoomItemDTO> roomItemDTOList = cartService.findCartOrderRoomItem(cartOrderDTOList);
//        model.addAttribute("roomItemDTOList", roomItemDTOList);
//
//        return "pay";
//    }

    @PostMapping("/pay")
    public ResponseEntity pay(@RequestBody CartOrderDTO cartOrderDTO, Principal principal) {

        log.info("Paying " + cartOrderDTO);
        log.info("진입");

        List<CartOrderDTO> cartOrderDTOList = cartOrderDTO.getCartOrderDTOList();

        if (cartOrderDTOList == null || cartOrderDTOList.size() == 0) {
            return new ResponseEntity<String>("결제할 상품을 선택해주세요", HttpStatus.FORBIDDEN);
        }

        for (CartOrderDTO cartOrder : cartOrderDTOList) {

            if (!cartService.validateCartRoomItem(cartOrder.getRoomitem_num(), principal.getName())) {
                return new ResponseEntity<String>("결제 권한이 없습니다.", HttpStatus.FORBIDDEN);

            }
        }

        return new ResponseEntity<String>( "결제 페이지 이동", HttpStatus.OK);
    }
}
