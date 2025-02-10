package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.RoomItemDTO;
import com.lookatme.smartstay.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class PayController {
    private final CartService cartService;

    @GetMapping("/pay")
    public String payPage(Long[] roomitem_nums, Model model) {

        //차후 룸서비스 추가 필요
        log.info("get 방식 : " + roomitem_nums);
        List<RoomItemDTO> roomItemDTOList = cartService.findCartOrderRoomItem(roomitem_nums);
        model.addAttribute("roomItemDTOList", roomItemDTOList);

        return "pay";
    }

}
