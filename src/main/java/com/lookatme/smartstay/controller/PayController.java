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
import java.math.BigDecimal;
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
    public ResponseEntity<?> validatePay(@RequestBody PayDTO payDTO)
            throws IamportResponseException, IOException {

        log.info("결제 검증 요청 payDTO : " + payDTO);

        // 1. amount가 null인 경우 요청 거부
        if (payDTO.getAmount() == null) {
            log.error("amount 값이 null입니다. 결제 요청이 유효하지 않습니다.");
            return ResponseEntity.badRequest().body("결제 금액(amount)이 null일 수 없습니다.");
        }

        // 2. amount가 0인 경우 처리
        if (payDTO.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            log.info("amount 값이 0원입니다. 결제 검증 없이 주문을 진행합니다.");
            return ResponseEntity.ok("0원 결제가 정상적으로 처리되었습니다.");
        }

        // 3. amount가 양수인 경우 결제 검증 로직 수행
        try {
            Payment payment = payService.validatePay(payDTO);

            if (payment == null) {
                log.error("결제 정보가 존재하지 않습니다. imp_uid: {}", payDTO.getImp_uid());
                return ResponseEntity.badRequest().body("결제 정보가 존재하지 않습니다.");
            }

            if (!payment.getAmount().equals(payDTO.getAmount())) {
                log.error("결제 금액 불일치: 요청 금액 = {}, 실제 결제 금액 = {}",
                        payDTO.getAmount(), payment.getAmount());
                return ResponseEntity.badRequest().body("결제 금액이 일치하지 않습니다.");
            }

            if (!"paid".equals(payment.getStatus())) {
                log.error("결제가 완료되지 않았습니다. 상태: {}", payment.getStatus());
                return ResponseEntity.badRequest().body("결제가 완료되지 않았습니다.");
            }

            log.info("결제 검증 성공: {}", payment);
            return ResponseEntity.ok(payment);

        } catch (Exception e) {
            log.error("결제 검증 중 오류 발생", e);
            return ResponseEntity.status(500).body("결제 검증 중 오류 발생");
        }
    }

    @PostMapping("/pay/order")
    @ResponseBody
    public ResponseEntity<String> payOrder(@RequestBody PayDTO payDTO){
        log.info("주문 정보 payDTO : " + payDTO);

        // 1. amount가 null인 경우 요청 거부
        if (payDTO.getAmount() == null) {
            log.error("amount 값이 null입니다. 결제 요청이 유효하지 않습니다.");
            return ResponseEntity.badRequest().body("결제 금액(amount)이 null일 수 없습니다.");
        }

        // 2. amount가 0원인 경우 처리
        if (payDTO.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            log.info("0원 결제 처리: 결제 검증 없이 주문 진행");
            payService.savePayInfo(payDTO); // 예약 정보 저장
            return ResponseEntity.ok("0원 예약이 정상적으로 완료되었습니다.");
        }

        // 3. amount가 양수인 경우 기존 로직 수행
        try {
            Payment payment = payService.validatePay(payDTO);
            if (payment != null && payment.getAmount().equals(payDTO.getAmount())) {
                log.info("결제 검증 완료: {}", payment);
                payService.savePayInfo(payDTO); // 예약 정보 저장
                return ResponseEntity.ok("예약 주문 완료");
            } else {
                log.error("결제 검증 실패: 금액 불일치");
                return ResponseEntity.badRequest().body("결제 검증 실패");
            }
        } catch (Exception e) {
            log.error("결제 검증 중 오류 발생", e);
            return ResponseEntity.status(500).body("결제 검증 중 오류 발생");
        }
    }

}
