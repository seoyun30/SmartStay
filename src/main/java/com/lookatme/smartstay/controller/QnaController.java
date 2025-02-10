package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.MemberDTO;
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
import org.springframework.security.core.context.SecurityContextHolder;
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

        qnaDTOList.forEach(qnaDTO -> {log.info("qnaDTO: " + qnaDTO);});
        return "/qna/qnaList";
    }

    //상세
    @GetMapping("/qnaRead")
    public String qnaRead(Long id, Model model) {
        log.info("컨트롤러 읽기로 들어온 게시글 번호:" + id);
        //log.info("컨트롤러 읽기로 들어온 게시글 번호:" + id);

        if (id == null || id.equals("")) {
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
    public String qnaModifyGet(Long qna_num, Model model) { //PageRequestDTO pageRequestDTO

        if(qna_num == null || qna_num.equals("")) {
            log.info("들어온 qna_num이 이상함");
            return "redirect:/qna/qnaList"; //+pageRequestDTO.getLink();
        }
        //try~chach문으로 이미지 넣을것

        // QnaDTO를 가져와서 model에 추가
        try {
            QnaDTO qnaDTO = qnaService.read(qna_num);
            model.addAttribute("qnaDTO", qnaDTO);
        } catch (EntityNotFoundException e) {
            log.info("해당 id로 게시글을 찾을 수 없음");
            return "redirect:/qna/qnaList";
        }

        return "/qna/qnaModify";
    }

    @PostMapping("/qnaModify")
    public String qnaModifyPost(QnaDTO qnaDTO, BindingResult bindingResult, Long[] delino, Model model) {
        qnaService.modify(qnaDTO);

        log.info("업데이트포스"+qnaDTO);
        //log.info("업데이트포스"+pageRequestDTO);
        if (bindingResult.hasErrors()) {
            log.info("유효성검사 확인!!");
            log.info(bindingResult.getAllErrors()); //유효성 내용 콘솔창에 출력
            return "/qna/qnaList";
        }

        return "redirect:/qna/qnaList"; //+pageRequestDTO.getLink()
    }

    //삭제
    @PostMapping("/qnaDelete")
    public String qnaDelete(Long qna_num) {
        log.info(qna_num);
        qnaService.del(qna_num);
        return "redirect:/qna/qnaList";
    }

}
