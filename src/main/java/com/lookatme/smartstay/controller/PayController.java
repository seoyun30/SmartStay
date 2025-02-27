package com.lookatme.smartstay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.service.*;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class PayController {
    private final MemberService memberService;
    private final CartService cartService;
    private final RoomItemService roomItemService;
    private final OrderItemService orderItemService;
    private final PayService payService;

    @GetMapping("/pay")
    public String pay(RoomItemDTO roomItemDTO, @RequestParam(value = "orderItemDTO", required = false) String orderItemDTOStr,
                      @RequestParam(value = "service_nums", required = false) Long[] serviceNums,
                      Principal principal, Model model) {

        MemberDTO memberDTO = memberService.readMember(principal.getName());
        model.addAttribute("memberDTO", memberDTO);
        OrderItemDTO orderItemDTO = null;
        if (orderItemDTOStr != null && !orderItemDTOStr.isEmpty()) {
            try {
                // URL 인코딩된 JSON을 디코딩 후 객체 변환
                String decodedJson = URLDecoder.decode(orderItemDTOStr, StandardCharsets.UTF_8);
                ObjectMapper objectMapper = new ObjectMapper();
                orderItemDTO = objectMapper.readValue(decodedJson, OrderItemDTO.class);
                log.info("디코딩된 orderItemDTO: " + orderItemDTO);
            } catch (Exception e) {
                log.error("orderItemDTO 디코딩 실패", e);
            }
        }

        if (roomItemDTO != null && roomItemDTO.getRoom_num() != null || roomItemDTO.getRoomitem_nums() != null) {
            log.info("결제 예정 roomItemDTO : " + roomItemDTO);

            if (roomItemDTO.getRoom_num() == null) {
                List<RoomItemDTO> roomItemDTOList = cartService.findCartOrderRoomItem(roomItemDTO.getRoomitem_nums());
                model.addAttribute("roomItemDTOList", roomItemDTOList);
            } else {
                roomItemDTO = roomItemService.findRoomDTO(roomItemDTO);
                log.info(roomItemDTO);
                List<RoomItemDTO> roomItemDTOList = Collections.singletonList(roomItemDTO);
                model.addAttribute("roomItemDTOList", roomItemDTOList);
            }
        } else {
            List<RoomItemDTO> roomItemDTOList = new ArrayList<>();
            model.addAttribute("roomItemDTOList", roomItemDTOList);
        }


        List<OrderItemDTO> orderItemDTOList = new ArrayList<>();
        if (serviceNums != null) {
            orderItemDTOList = cartService.findCartOrderOrderItem(serviceNums);
        }
        if (orderItemDTO != null) {
            orderItemDTO = orderItemService.findOrderItemDTO(orderItemDTO);
            orderItemDTOList = Collections.singletonList(orderItemDTO);
        }
        orderItemDTOList.forEach(orderItemDTO1 -> log.info(orderItemDTO1));
        model.addAttribute("orderItemDTOList", orderItemDTOList);

        return "pay";
    }

    @PostMapping("/pay/prepare")
    @ResponseBody
    public ResponseEntity<String> preparePay(@RequestBody PrePayDTO prePayDTO)
            throws IamportResponseException, IOException {
        log.info("prePayDTO : " + prePayDTO);
        payService.postPrepare(prePayDTO);
        return ResponseEntity.ok("결제 사전 준비 완료");
    }

    @PostMapping("/pay/validate")
    @ResponseBody
    public ResponseEntity<Payment> validatePay(@RequestBody PayDTO payDTO)
            throws IamportResponseException, IOException {
        log.info("결제 검증 payDTO : " + payDTO);
        Payment payment = payService.validatePay(payDTO);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/pay/order")
    @ResponseBody
    public ResponseEntity<String> payOrder(@RequestBody PayDTO payDTO){
        log.info("주문 정보 payDTO : " + payDTO);
        payService.savePayInfo(payDTO); //여기에서 룸서비스 예약 추가 필요
        return ResponseEntity.ok("예약 주문 완료");
    }

}
