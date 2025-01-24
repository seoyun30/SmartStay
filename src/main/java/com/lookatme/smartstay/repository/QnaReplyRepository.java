package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Qna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface QnaReplyRepository extends JpaRepository<Qna, Long> {
    //검색기능 추가시 QnaSearch 추가

    //public Page<QnaReply> findByQna_num(Long qna_num, Pageable pageable);
    //public List<QnaReply> findByQna_num(Long qna_num);
    //public List<QnaReply> deleteAllByQna_num(Long qna_num);

}
