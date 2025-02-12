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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class PayController {
    private final MemberService memberService;
    private final CartService cartService;
    private final RoomItemService roomItemService;
    private final PayService payService;

    @GetMapping("/pay")
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

    @PostMapping("/pay/prepare")
    public void preparePay(@RequestBody PrePayDTO prePayDTO)
            throws IamportResponseException, IOException {
        payService.postPrepare(prePayDTO);
    }

    @PostMapping("/pay/validate")
    public Payment validatePay(@RequestBody PayDTO payDTO)
            throws IamportResponseException, IOException {
        return payService.validatePay(payDTO);
    }

    @PostMapping("/pay/order")
    @ResponseBody
    public void payOrder(@RequestBody PayDTO payDTO){
        payService.savePayInfo(payDTO);
    }

}
