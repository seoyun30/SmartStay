package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.dto.PayDTO;
import com.lookatme.smartstay.dto.PrePayDTO;
import com.lookatme.smartstay.dto.RoomItemDTO;
import com.lookatme.smartstay.service.CartService;
import com.lookatme.smartstay.service.MemberService;
import com.lookatme.smartstay.service.PayService;
import com.lookatme.smartstay.service.RoomItemService;
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
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/pay")
public class PayController {
    private final MemberService memberService;
    private final CartService cartService;
    private final RoomItemService roomItemService;
    private final PayService payService;

    @GetMapping("/")
    public String pay(RoomItemDTO roomItemDTO, Principal principal, Model model) {

        //차후 룸서비스 추가 필요
        log.info("get 방식 : " + roomItemDTO);

        MemberDTO memberDTO = memberService.readMember(principal.getName());
        model.addAttribute("memberDTO", memberDTO);

        if (roomItemDTO.getRoom_num() == null) {
            List<RoomItemDTO> roomItemDTOList = cartService.findCartOrderRoomItem(roomItemDTO.getRoomitem_nums());
            model.addAttribute("roomItemDTOList", roomItemDTOList);
        } else {
            roomItemDTO = roomItemService.findRoomDTO(roomItemDTO);
            log.info(roomItemDTO);
            List<RoomItemDTO> roomItemDTOList = Collections.singletonList(roomItemDTO);
            model.addAttribute("roomItemDTOList", roomItemDTOList);
        }

        return "pay";
    }

    @PostMapping("/prepare")
    @ResponseBody
    public ResponseEntity<String> preparePay(@RequestBody PrePayDTO prePayDTO)
            throws IamportResponseException, IOException {
        log.info("prePayDTO : " + prePayDTO);
        payService.postPrepare(prePayDTO);
        return ResponseEntity.ok("결제 사전 준비 완료");
    }

    @PostMapping("/validate")
    @ResponseBody
    public ResponseEntity<Payment> validatePay(@RequestBody PayDTO payDTO)
            throws IamportResponseException, IOException {
        log.info("결제 검증 payDTO : " + payDTO);
        Payment payment = payService.validatePay(payDTO);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/order")
    @ResponseBody
    public ResponseEntity<String> payOrder(@RequestBody PayDTO payDTO){
        log.info("주문 정보 payDTO : " + payDTO);
        payService.savePayInfo(payDTO);
        return ResponseEntity.ok("예약 주문 완료");
    }

}
