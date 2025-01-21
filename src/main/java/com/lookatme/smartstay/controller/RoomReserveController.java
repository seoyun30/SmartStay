package com.lookatme.smartstay.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/roomreserve")
public class RoomReserveController {

    @GetMapping("/roomReserveRegister")
    public String roomReserveRegisterGet() {
        return "roomreserve/roomReserveRegister";
    }

    @GetMapping("/roomReserveList")
    public String roomReserveList() {
        return "roomreserve/roomReserveList";
    }

    @GetMapping("/roomReserveRead")
    public String roomReserveRead() {
        return "roomreserve/roomReserveRead";
    }



}
