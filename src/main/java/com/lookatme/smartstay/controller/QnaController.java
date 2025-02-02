package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.QnaDTO;
import com.lookatme.smartstay.entity.Qna;
import com.lookatme.smartstay.service.ImageService;
import com.lookatme.smartstay.service.QnaServcieImpl;
import com.lookatme.smartstay.service.QnaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/qna")
public class QnaController {

    private final QnaServcieImpl qnaService;
    private final ImageService imageService;

    //등록
    @GetMapping("/qnaRegister")
    public String qnaRegisterGet(Model model) {
        model.addAttribute("qnaDTO", new QnaDTO());
        return "/qna/qnaRegister";
    }

    @PostMapping("/qnaRegister")
    public String qnaRegisterPost(QnaDTO qnaDTO, BindingResult bindingResult) {
        //@valid MultipartFile[] multipartFile 추후 사용

        log.info("컨트롤러로 들어온 값:" + qnaDTO);
        //log.info("컨트롤러로 들어온 값:" + MultipartFile.getOriginalFilename());

        if (bindingResult.hasErrors()) {
            log.info("유효성 검사나 문제가 있다 아래 로그는 모든 문제를 출력");
            log.info(bindingResult.getAllErrors());

            return "/qna/qnaRegister";
        }
        qnaService.register(qnaDTO);
        //파일 추가시 qnaService.register(qnaDTO, multipartFile); 활성

        return "redirect:/qna/qnaList";
    }

    //목록
    @GetMapping("/qnaList")
    public String qnaList(Model model) {
        log.info("pageRequestDTO");
        List<QnaDTO> qnaDTOList = qnaService.list();
        model.addAttribute("qnaDTOList", qnaDTOList);
        return "/qna/qnaList";
    }

    //상세
    @GetMapping("/qnaRead")
    public String qnaRead(Long id, Model model) {
        log.info("컨트롤러 읽기로 들어온 게시글 번호:" + id);
        //log.info("컨트롤러 읽기로 들어온 게시글 번호:" + id);

        if (id != null || id.equals("")) {
            log.info("들어온 id 가 이상함");
            return "redirect:/qna/qnaList";
        }

        try {
            QnaDTO qnaDTO = qnaService.read(id);
            model.addAttribute("qnaDTO", qnaDTO);
        }catch (EntityNotFoundException e){
            log.info("id로 값을 찾지 못함");
            return "redirect:/qna/qnaList";
        }

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
