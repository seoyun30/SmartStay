package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.QnaReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QnaReplyRepository extends JpaRepository<QnaReply, Long> {

    @Query("select q from QnaReply q where q.qna.qna_num = :qna_num")
    QnaReply findByQnaNum(Long qna_num);

    // 특정 QnA 번호와 댓글 번호로 댓글 조회
    @Query("select qr from QnaReply qr where qr.qna.qna_num = :qna_num and qr.qnaReply_num = :qnaReply_num")
    Optional<QnaReply> findByQnaNumAndQnaReplyNum(Long qna_num, Long qnaReply_num);

}
