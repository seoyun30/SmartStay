package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Cart;
import com.lookatme.smartstay.entity.Qna;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;

public interface QnaRepository extends JpaRepository<Qna, Long> {
    //QnaSearch 검색기능 추후 추가

    /*
    //제목으로 검색
    // select * from board where title like '%파라미터%'
    public Page<Qna> findByTitleContaining(String keyword, Pageable pageable);

    //내용으로 검색   // 쿼리메소드

    public Page<Qna> findByContentContaining(String keyword, Pageable pageable);

    //작성자 검색
    @Query("select b from Qna b where b.writer like concat('%', :str, '%') ")
    public Page<Qna> selectlikeWriter(String str, Pageable pageable);

    @Query("select b from Qna b where b.title like concat('%', :str, '%') or b.content like concat('%', :str, '%') ")
    public Page<Qna> titleOrCon(String str, Pageable pageable);

    public Page<Qna> findByContentContainingOrWriterContaining(String str, String str1, Pageable pageable);

    public Page<Qna> findByTitleContainingOrWriterContaining(String str, String str1, Pageable pageable);

    @Query("select b from Qna b where b.title like concat('%', :str, '%') or b.content like concat('%', :str, '%') or b.writer like concat('%', :str, '%') ")
    public Page<Qna> titleOrConOrWr(String str, Pageable pageable);
*/

}
