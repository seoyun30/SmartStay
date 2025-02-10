package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.RoomItemDTO;
import com.lookatme.smartstay.service.CartService;
import com.lookatme.smartstay.service.RoomItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class PayController {
    private final CartService cartService;
    private final RoomItemService roomItemService;

    @GetMapping("/pay")
    public String pay(RoomItemDTO roomItemDTO,  Model model) {

        //차후 룸서비스 추가 필요
        log.info("get 방식 : " + roomItemDTO);

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

}
