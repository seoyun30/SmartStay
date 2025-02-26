package com.lookatme.smartstay.controller;

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
    public String pay(RoomItemDTO roomItemDTO, OrderItemDTO orderItemDTO, Principal principal, Model model) {

        MemberDTO memberDTO = memberService.readMember(principal.getName());
        model.addAttribute("memberDTO", memberDTO);

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

        if (orderItemDTO != null && orderItemDTO.getService_num() != null || orderItemDTO.getService_nums() != null) {
            log.info("결제 예정 orderItemDTO : " + orderItemDTO);

            if (orderItemDTO.getService_num() == null) {
                List<OrderItemDTO> orderItemDTOList = cartService.findCartOrderOrderItem(orderItemDTO.getService_nums());
                model.addAttribute("orderItemDTOList", orderItemDTOList);
            } else {
                orderItemDTO = orderItemService.findOrderItemDTO(orderItemDTO);
                log.info(orderItemDTO);
                List<OrderItemDTO> orderItemDTOList = Collections.singletonList(orderItemDTO);
                model.addAttribute("orderItemDTOList", orderItemDTOList);
            }
        } else {
            List<OrderItemDTO> orderItemDTOList = new ArrayList<>();
            model.addAttribute("orderItemDTOList", orderItemDTOList);
        }

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
