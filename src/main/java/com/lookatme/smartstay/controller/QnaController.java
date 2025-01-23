package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.entity.Qna;
import com.lookatme.smartstay.service.ImageService;
import com.lookatme.smartstay.service.QnaServcieImpl;
import com.lookatme.smartstay.service.QnaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/qna")
public class QnaController {

    private final QnaServcieImpl qnaService;
    private final ImageService imageService;

    //등록
    @GetMapping("/qnaRegister")
    public String qnaRegisterGet() {
        return "/qna/qnaRegister";
    }

    @PostMapping("/qnaRegister")
    public String qnaRegisterPost(Qna qna) {
        return "/qna/qnaRegister";
    }

    //목록
    @GetMapping("/qnaList")
    public String qnaList() {
        return "/qna/qnaList";
    }

    //상세
    @GetMapping("/qnaRead")
    public String qnaRead(){
        return "/qna/qnaRead";
    }

    //수정
    @GetMapping("/qnaModify")
    public String qnaModifyGet(){
        return "/qna/qnaModify";
    }

    @PostMapping("/qnaModify")
    public String qnaModifyPost(Qna qna) {
        return "/qna/qnaModify";
    }
    //삭제
    @PostMapping("/qnaDelete")
    public String qnaDelete(Long qna_num) {
        log.info(qna_num);
        qnaService.del(qna_num);
        return "redirect:/qna/qnaList";
    }

}
