package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.dto.QnaReplyDTO;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.Qna;
import com.lookatme.smartstay.entity.QnaReply;
import com.lookatme.smartstay.repository.MemberRepository;
import com.lookatme.smartstay.repository.QnaReplyRepository;
import com.lookatme.smartstay.repository.QnaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class QnaReplyService {

    private final QnaReplyRepository qnaReplyRepository;
    private final MemberRepository memberRepository;
    private final QnaRepository qnaRepository;
    private final ModelMapper modelMapper;

    //등록
    public QnaReply register(Long qna_num, QnaReplyDTO qnaReplyDTO, String email) throws IOException {
        log.info("register() 호출, qna_num: {}, email: {}", qna_num, email);
        Member member = memberRepository.findByEmail(email);
        log.info("member: {}", member);

        Qna qna = qnaRepository.findById(qna_num).orElseThrow(() ->
                new IllegalArgumentException("댓글 쓰기 실패: 해당 질문이 존재하지 않습니다. " + qna_num));
        log.info("Qna 찾음, qna_num: {}", qna_num);

        QnaReply qnaReply = modelMapper.map(qnaReplyDTO, QnaReply.class);
        qnaReply.setWriter(member.getEmail());
        qnaReply.setMember(member);
        qnaReply.setQna(qna);
        qnaReply = qnaReplyRepository.save(qnaReply);
        log.info("댓글 등록 성공, qnaReply_num: {}", qnaReply.getQnaReply_num());
        return qnaReply;
    }

    //읽기
    @Transactional
    public QnaReplyDTO read(Long qna_num) throws IOException {
        log.info("read() 호출, qna_num: {}", qna_num);

        QnaReply qnaReply = qnaReplyRepository.findByQnaNum(qna_num);
        log.info("qnaReply: {}", qnaReply);

        QnaReplyDTO qnaReplyDTO = modelMapper.map(qnaReply, QnaReplyDTO.class)
                .setMemberDTO(modelMapper.map(qnaReply.getMember(), MemberDTO.class));
        return qnaReplyDTO;
    }

    //수정
    @Transactional
    public void updateQnaReply(Long qna_num, QnaReplyDTO qnaReplyDTO) {
        log.info("updateQnaReply() 호출, qna_num: {}, qnaReplyDTO: {}", qna_num, qnaReplyDTO);
        QnaReply qnaReply = qnaReplyRepository.findByQnaNumAndQnaReplyNum(qna_num, qnaReplyDTO.getQnaReply_num())
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다. :" + qnaReplyDTO.getQnaReply_num()));

        qnaReply.update(qnaReplyDTO.getComment());
        log.info("댓글 수정 완료, qnaReplyDTO: {}", qnaReplyDTO);
    }

    //삭제
    @Transactional
    public void delete(Long qnaReply_num) {
        log.info("delete() 호출, qnaReply_num: {}", qnaReply_num);
        QnaReply qnaReply = qnaReplyRepository.findById(qnaReply_num).orElseThrow(EntityNotFoundException::new);
        qnaReply.setMember(null);

        qnaReplyRepository.deleteById(qnaReply_num);
        log.info("댓글 삭제 완료, qnaReply_num: {}", qnaReply_num);
    }
}
