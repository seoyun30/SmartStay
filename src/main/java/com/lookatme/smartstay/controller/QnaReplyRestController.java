package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.QnaDTO;
import com.lookatme.smartstay.dto.QnaReplyRequest;
import com.lookatme.smartstay.entity.QnaReply;
import com.lookatme.smartstay.service.QnaReplyService;
import com.lookatme.smartstay.service.QnaService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/qnareply")
public class QnaReplyRestController {

    private final QnaReplyService qnaReplyService;
    private final QnaService qnaService;

    @PostMapping("/register")
    public ResponseEntity<QnaReply> register(@RequestParam Long qna_num,
                                             @RequestBody QnaReplyRequest request,
                                             Principal principal,
                                             HttpServletResponse response) throws IOException {
        log.info("register() 호출, qnaReply_num: {}, principal: {}", qna_num, principal.getName());
        // principal.getName()을 통해 현재 로그인한 사용자의 이메일을 가져옵니다.
        String email = principal.getName();
        // qnaReplyService.register에 필요한 파라미터를 전달
        QnaReply qnaReply = qnaReplyService.register(qna_num, request, email, response);
        // 생성된 QnaReply 객체를 응답 본문으로 반환
        log.info("댓글 등록 완료, qnaReply_num: {}", qnaReply.getQnaReply_num());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(qnaReply);
    }

    //댓글 읽어오기
    @GetMapping("/qnaRead")
    public String qnaRead(@RequestParam Long qna_num, Model model) {
        log.info("qnaRead() 호출, qna_num: {}", qna_num);

        QnaDTO qnaDTO = qnaService.read(qna_num);
        if (qnaDTO == null) {
            log.error("QnA 데이터를 찾을 수 없습니다. qna_num: {}", qna_num);
            return "error/404"; // 에러 페이지로 이동
        }

        model.addAttribute("qnaDTO", qnaDTO);
        return "qna/qnaRead"; // 뷰 파일 반환
    }

    //댓글 업데이트
    @PutMapping({"/modify"})
    public ResponseEntity<Long> update(@PathVariable Long qna_num, @PathVariable Long qnaReply_num, @RequestBody QnaReplyRequest dto) {
        log.info("update() 호출, qna_num: {}, qnaReply_num: {}", qna_num, qnaReply_num);
        qnaReplyService.updateQnaReply(qna_num, qnaReply_num, dto);
        log.info("댓글 수정 완료, qnaReply_num: {}", qnaReply_num);
        return ResponseEntity.ok(qnaReply_num);
    }

    //댓글 삭제
    @DeleteMapping("/del/{qna_num}")
    public ResponseEntity<Long> delete(@PathVariable Long qna_num, @PathVariable Long qnaReply_num) {
        log.info("delete() 호출, qna_num: {}, qnaReply_num: {}", qna_num, qnaReply_num);
        qnaReplyService.delete(qna_num, qnaReply_num);
        log.info("댓글 삭제 완료, qnaReply_num: {}", qnaReply_num);
        return ResponseEntity.ok(qnaReply_num);
    }
}
