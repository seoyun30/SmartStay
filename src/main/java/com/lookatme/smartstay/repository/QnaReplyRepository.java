package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Qna;
import com.lookatme.smartstay.entity.QnaReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface QnaReplyRepository extends JpaRepository<QnaReply, Long> {
    //검색기능 추가시 QnaSearch 추가

    //public Page<QnaReply> findByQna_num(Long qna_num, Pageable pageable);
    @Query("select q from QnaReply q where q.qna.qna_num = :qna_num")
    public List<QnaReply> findByQna_num(Long qna_num);
    @Query("select q from QnaReply q where q.qna.qna_num = :qna_num")
    public List<QnaReply> deleteAllByQna_num(Long qna_num); //수정 필요



}
