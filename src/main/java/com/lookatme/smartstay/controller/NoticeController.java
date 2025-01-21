package com.lookatme.smartstay.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
public class NoticeController {

    @GetMapping("/List")
    public String noticeList(PageRequestDTO pageRequestDTO){

        return "notice/noticeList";
    }

    @GetMapping("/Register")
    public String noticeRegisterGet(){

        return "notice/noticeRegister";
    }

    @PostMapping("/Register")
    public String noticeRegisterPost(){

        return null;
    }

    @GetMapping("/Read")
    public String noticeRead(){

        return null;
    }

    @GetMapping("/Modify")
    public String noticeModifyGet(){

        return null;
    }

    @PostMapping("/Modify")
    public String noticeModifyPost(){

        return null;
    }

    @PostMapping("/Delete")
    public String noticeDelete(){

        return null;
    }
}
