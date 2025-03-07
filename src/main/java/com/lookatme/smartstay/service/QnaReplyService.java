package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.QnaReplyRequest;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.Qna;
import com.lookatme.smartstay.entity.QnaReply;
import com.lookatme.smartstay.repository.MemberRepository;
import com.lookatme.smartstay.repository.QnaReplyRepository;
import com.lookatme.smartstay.repository.QnaRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class QnaReplyService {

    private final QnaReplyRepository qnaReplyRepository;
    private final MemberRepository memberRepository;
    private final QnaRepository qnaRepository;

    //등록
    public QnaReply register(Long qna_num, QnaReplyRequest request, String email, HttpServletResponse response) throws IOException {
        log.info("register() 호출, qna_num: {}, email: {}", qna_num, email);
        Member member = memberRepository.findByEmail(email);

        // 사용자가 없으면 로그인 페이지로 리다이렉트
        if (member == null) {
            log.info("사용자가 존재하지 않습니다: {}", email);
            response.sendRedirect("/member/login");  // 로그인 페이지 URL로 리다이렉트
            return null;
        }

        // Qna가 존재하는지 체크
        Qna qna = qnaRepository.findById(qna_num).orElseThrow(() ->
                new IllegalArgumentException("댓글 쓰기 실패: 해당 질문이 존재하지 않습니다. " + qna_num));
        log.info("Qna 찾음, qna_num: {}", qna_num);

        // QnaReply 저장
        QnaReply qnaReply = qnaReplyRepository.save(request.toEntity(qna, member));
        log.info("댓글 등록 성공, qnaReply_num: {}", qnaReply.getQnaReply_num());
        return qnaReply;
    }

    //읽기
    @Transactional
    public QnaReply read(Long qna_num) {
        log.info("read() 호출, qna_num: {}", qna_num);
        Qna qna = qnaRepository.findById(qna_num).orElseThrow(() ->
                new IllegalArgumentException("해당 질문이 존재하지 않습니다. qna_num: " + qna_num));
        log.info("Qna 찾음, qna_num: {}", qna_num);
        //  return qna.getQnaReply(); // 단일 QnaReply 반환
        return null;
    }

    //수정
    @Transactional
    public void updateQnaReply(Long qna_num, Long qnaReply_num, QnaReplyRequest dto) {
        log.info("updateQnaReply() 호출, qna_num: {}, qnaReply_num: {}", qna_num, qnaReply_num);
        // qnaNum과 qnaReplyNum으로 댓글 조회
        QnaReply qnaReply = qnaReplyRepository.findByQnaNumAndQnaReplyNum(qna_num, qnaReply_num)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다. :" + qnaReply_num));

        // 댓글 내용 업데이트
        qnaReply.update(dto.getComment());
        log.info("댓글 수정 완료, qnaReply_num: {}", qnaReply_num);
    }

    //삭제
    @Transactional
    public void delete(Long qna_num, Long qnaReply_num) {
        log.info("delete() 호출, qna_num: {}, qnaReply_num: {}", qna_num, qnaReply_num);
        QnaReply qnaReply = qnaReplyRepository.findByQnaNumAndQnaReplyNum(qna_num, qnaReply_num).orElseThrow(() ->
                new IllegalArgumentException("해당 댓글이 존재하지 않습니다. :" + qnaReply_num));

        qnaReplyRepository.delete(qnaReply);
        log.info("댓글 삭제 완료, qnaReply_num: {}", qnaReply_num);
    }
}
