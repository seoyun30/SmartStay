package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.QnaReplyDTO;
import com.lookatme.smartstay.service.QnaReplyService;
import com.lookatme.smartstay.service.QnaReplyServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/qnareply")
public class QnaReplyRestController {

    private final QnaReplyServiceImpl qnaReplyServiceImpl;
    private final QnaReplyService qnaReplyService;

    @PostMapping("/register")
    public ResponseEntity qnareplyregister(@Valid QnaReplyDTO qnaReplyDTO, BindingResult bindingResult) {
        log.info("댓글 들어온 값:" + qnaReplyDTO);

        if (bindingResult.hasErrors()) {
            log.info("에러가 있습니다");
            log.info(bindingResult.getAllErrors());

            //특정 필드값의 오류만 확인하면됨
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            log.info("-------------------");
            fieldErrors.forEach(a -> log.info(a.getDefaultMessage()));

            return new ResponseEntity<String>(fieldErrors.get(0).getDefaultMessage(), HttpStatus.OK);
        }
        if (qnaReplyDTO.getQnaReply_num() == null || qnaReplyDTO.getQnaReply_num().equals("")) {

            return new ResponseEntity<String>("댓글값들이 안들어옴", HttpStatus.BAD_REQUEST);
        }

        //댓글 저장
        try {
            log.info("저장시작");
            qnaReplyService.register(qnaReplyDTO);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<String>("입력하신 글에는 댓글이 없습니다.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<String>("저당되었습니다.", HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity list(Long qna_num) {
        log.info("댓글" + qna_num);

        if (qna_num == null || qna_num.equals("")) {
            return new ResponseEntity<String>("요청값을 확인해주세요", HttpStatus.BAD_REQUEST);
        }

        //댓글불러옴??

        return new ResponseEntity(qnaReplyService.list(qna_num), HttpStatus.OK);
    }

    @GetMapping("/read/{qna_num}")
    public ResponseEntity read(@PathVariable("qna_num") Long qna_num) {
        QnaReplyDTO qnaReplyDTO = qnaReplyService.read(qna_num);

        return new ResponseEntity<QnaReplyDTO>(qnaReplyDTO, HttpStatus.OK);
    }

    @PostMapping("/modify")
    public ResponseEntity modify(QnaReplyDTO qnaReplyDTO) {
        log.info(qnaReplyDTO);

        try {
            qnaReplyService.modify(qnaReplyDTO);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<String>("댓글 못 찾음", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("수정본",HttpStatus.OK);
    }

    @PostMapping("/del/{qna_num}")
    public ResponseEntity del(@PathVariable("qna_num") Long qna_num) {
        log.info("컨트롤러 qna_num"+qna_num);

        try {
            qnaReplyService.del(qna_num);
        }catch (EntityNotFoundException e) {
            return new ResponseEntity<String>("댓글 없음", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<String>("삭제됨",HttpStatus.OK);
    }


}
