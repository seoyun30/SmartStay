package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.QnaReplyDTO;
import com.lookatme.smartstay.dto.QnaReplyRequest;
import com.lookatme.smartstay.entity.QnaReply;
import com.lookatme.smartstay.service.QnaReplyService;
import com.lookatme.smartstay.service.QnaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/qnareply")
public class QnaReplyRestController {

    private final QnaReplyService qnaReplyService;
    private final QnaService qnaService;

    @PostMapping("/register")
    public ResponseEntity<QnaReply> register(@PathVariable Long qnaReply_num,
                                             @RequestBody QnaReplyRequest request,
                                             Principal principal,
                                             HttpServletResponse response) throws IOException {
        log.info("register() 호출, qnaReply_num: {}, principal: {}", qnaReply_num, principal.getName());
        // principal.getName()을 통해 현재 로그인한 사용자의 이메일을 가져옵니다.
        String email = principal.getName();
        // qnaReplyService.register에 필요한 파라미터를 전달
        QnaReply qnaReply = qnaReplyService.register(qnaReply_num, request, email, response);
        // 생성된 QnaReply 객체를 응답 본문으로 반환
        log.info("댓글 등록 완료, qnaReply_num: {}", qnaReply.getQnaReply_num());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(qnaReply);
    }

    //댓글 읽어오기
    @GetMapping("/read/{qna_num}")
    public QnaReply read(@PathVariable long qna_num) {
        log.info("read() 호출, qna_num: {}", qna_num);
        QnaReply qnaReply = qnaReplyService.read(qna_num);
        log.info("댓글 읽기 완료, qna_num: {}, qnaReply_num: {}", qna_num, qnaReply.getQnaReply_num());
        return qnaReply;
    }

    //댓글 업데이트
    @PutMapping({"/modify"})
    public ResponseEntity<Long> update(@PathVariable long qna_num, @PathVariable Long qnaReply_num, @RequestBody QnaReplyRequest dto) {
        log.info("update() 호출, qna_num: {}, qnaReply_num: {}", qna_num, qnaReply_num);
        qnaReplyService.updateQnaReply(qna_num, qnaReply_num, dto);
        log.info("댓글 수정 완료, qnaReply_num: {}", qnaReply_num);
        return ResponseEntity.ok(qnaReply_num);
    }

    //댓글 삭제
    @DeleteMapping("/del/{qna_num}")
    public ResponseEntity<Long> delete(@PathVariable long qna_num, @PathVariable Long qnaReply_num) {
        log.info("delete() 호출, qna_num: {}, qnaReply_num: {}", qna_num, qnaReply_num);
        qnaReplyService.delete(qna_num, qnaReply_num);
        log.info("댓글 삭제 완료, qnaReply_num: {}", qnaReply_num);
        return ResponseEntity.ok(qnaReply_num);
    }
}
