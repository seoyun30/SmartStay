package com.lookatme.smartstay.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/orderreserve")
public class OrderReserveController {

    @GetMapping("/orderReserveList")
    public String orderReserveList() {
        return "roomreserve/orderReserveList";
    }

    @GetMapping("/orderReserveRead")
    public String orderReserveRead() {
        return "orderreserve/orderReserveRead";
    }
}
