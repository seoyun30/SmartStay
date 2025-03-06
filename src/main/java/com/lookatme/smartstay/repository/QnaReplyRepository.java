package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.QnaReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QnaReplyRepository extends JpaRepository<QnaReply, Long> {
    //검색기능 추가시 QnaSearch 추가

    //public Page<QnaReply> findByQna_num(Long qna_num, Pageable pageable);
    /*@Query("select q from QnaReply q where q.qna.qna_num = :qna_num")
    public List<QnaReply> findByQna_num(Long qna_num);
    @Query("select q from QnaReply q where q.qna.qna_num = :qna_num")
    public List<QnaReply> deleteAllByQna_num(Long qna_num); //수정 필요*/

//    @Query("select q from QnaReply q where q.qna.qna_num = :qna_num")
//    public QnaReply findByQna_num(Long qna_num);
//
//    @Query("delete from QnaReply q where q.qna.qna_num = :qna_num")
//    public void deleteByQna_num(Long qna_num);

//    @Query("select qr from QnaReply qr where qr.qna.qna_num = :qna_num and qr.qnaReply_num = :qnaReply_num")
//    public Optional<QnaReply> findByQnaNumAndQnaReplyNum(Long qna_num, Long qnaReply_num);

    // 특정 QnA 번호와 댓글 번호로 댓글 조회
    @Query("select qr from QnaReply qr where qr.qna.qna_num = :qna_num and qr.qnaReply_num = :qnaReply_num")
    Optional<QnaReply> findByQnaNumAndQnaReplyNum(Long qna_num, Long qnaReply_num);

    // 특정 QnA 번호의 모든 댓글 삭제
    @Query("delete from QnaReply qr where qr.qna.qna_num = :qna_num")
    void deleteByQnaNum(Long qna_num);

}
