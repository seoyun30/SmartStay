package com.lookatme.smartstay.controller;


import com.lookatme.smartstay.dto.QnaReplyDTO;
import com.lookatme.smartstay.entity.QnaReply;
import com.lookatme.smartstay.service.QnaReplyService;
import com.lookatme.smartstay.service.QnaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/qnaReply")
public class QnaReplyRestController {

    private final QnaReplyService qnaReplyService;
    private final QnaService qnaService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam Long qna_num,
                                             @RequestBody QnaReplyDTO qnaReplyDTO,
                                             Principal principal) throws IOException {
        log.info("Register QnaReply qna num: " + qna_num);
        log.info("Register QnaReply qnaReplyDTO: " + qnaReplyDTO);
        String email = principal.getName();
        QnaReply qnaReply = qnaReplyService.register(qna_num, qnaReplyDTO, email);

        log.info("댓글 등록 완료, qnaReply_num: {}", qnaReply.getQnaReply_num());
        return ResponseEntity.ok("댓글 등록 완료");
    }

    //댓글 읽어오기
    @GetMapping("/qnaRead")
    public ResponseEntity<?> qnaRead(@RequestParam Long qna_num) throws IOException {
        log.info("qnaRead() 호출, qna_num: {}", qna_num);

        QnaReplyDTO qnaReplyDTO = qnaReplyService.read(qna_num);
        if (qnaReplyDTO == null) {
            log.error("QnA 데이터를 찾을 수 없습니다. qna_num: {}", qna_num);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(qnaReplyDTO);

    }

    //댓글 업데이트
    @PutMapping({"/modify"})
    public ResponseEntity<Long> update(@RequestParam Long qna_num, @RequestBody QnaReplyDTO qnaReplyDTO) {
        log.info("update() 호출, qna_num: {}, qnaReplyDTO: {}", qna_num, qnaReplyDTO);
        qnaReplyService.updateQnaReply(qna_num, qnaReplyDTO);
        log.info("댓글 수정 완료, qnaReplyDTO: {}", qnaReplyDTO);
        return ResponseEntity.ok(qnaReplyDTO.getQnaReply_num());
    }

    //댓글 삭제
    @DeleteMapping("/del/{qnaReply_num}")
    public ResponseEntity<Long> delete(@PathVariable Long qnaReply_num) {
        log.info("delete() 호출, qnaReply_num: {}", qnaReply_num);
        qnaReplyService.delete(qnaReply_num);
        log.info("댓글 삭제 완료, qnaReply_num: {}", qnaReply_num);
        return ResponseEntity.ok(qnaReply_num);
    }
}
