package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Qna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QnaRepository extends JpaRepository<Qna, Long> {
    @Query("SELECT q FROM Qna q WHERE q.writer = :writer")
    Page<Qna> findByWriter(@Param("writer") String writer, Pageable pageable);  // 작성자로 QnA 목록 조회 페이징

    @Query("select q from Qna q")
    Page<Qna> selcetAll(Pageable pageable); //페이징처리 목록 전부

    //제목으로 검색
    // select * from board where title like '%파라미터%'
    @Query("select q from Qna q where q.title like concat('%', :keyword, '%') ")
    Page<Qna> findByTitleContaining(String keyword, Pageable pageable);

    //내용으로 검색   // 쿼리메소드
    @Query("select q from Qna q where q.content like concat('%', :keyword, '%') ")
    Page<Qna> findByContentContaining(String keyword, Pageable pageable);

    //작성자 검색
    @Query("select q from Qna q where q.writer like concat('%', :str, '%') ")
    Page<Qna> selectlikeWriter(String str, Pageable pageable);

    //호텔명 검색
    @Query("select q from Qna q join q.hotel h where h.hotel_name like concat('%', :str, '%')")
    Page<Qna> selectlikeHotel(String str, Pageable pageable);

    @Query("select q from Qna q where q.title like concat('%', :str, '%') or q.content like concat('%', :str, '%') ")
    Page<Qna> titleOrCon(String str, Pageable pageable);

    @Query("select q from Qna q where q.writer like concat('%', :str, '%')or q.content like concat('%', :str1, '%') ")
    Page<Qna> findByContentContainingOrWriterContaining(String str, String str1, Pageable pageable);

    @Query("select q from Qna q where q.writer like concat('%', :str, '%')or q.title like concat('%', :str1, '%') ")
    Page<Qna> findByTitleContainingOrWriterContaining(String str, String str1, Pageable pageable);

    @Query("select q from Qna q where q.title like concat('%', :str, '%') or q.content like concat('%', :str, '%') or q.writer like concat('%', :str, '%') ")
    Page<Qna> titleOrConOrWr(String str, Pageable pageable);

    //유저용
    @Query("select q from Qna q where q.title like concat('%', :keyword, '%')  and q.writer = :email ")
    Page<Qna> findByTitleAndWriterContaining(String keyword, String email, Pageable pageable);

    @Query("select q from Qna q where q.content like concat('%', :keyword, '%')  and q.writer = :email ")
    Page<Qna> findByContentAndWriterContaining(String keyword, String email, Pageable pageable);

    @Query("select q from Qna q join q.hotel h where h.hotel_name like concat('%', :str, '%') and q.writer = :email ")
    Page<Qna> selectlikeHotelAndWriter(String str, String email, Pageable pageable);

    @Query("select q from Qna q where q.title like concat('%', :str, '%') or q.content like concat('%', :str, '%') and q.writer = :email ")
    Page<Qna> titleOrConAndWriter(String str, String email, Pageable pageable);

    //호텔용
    @Query("select q from Qna q where q.hotel.hotel_num = :hotel_num")
    Page<Qna> findbyHotel(Long hotel_num, Pageable pageable);

    @Query("select q from Qna q where q.title like concat('%', :keyword, '%')  and q.hotel.hotel_num = :hotel_num ")
    Page<Qna> findByTitleAndHotel_numContaining(String keyword, Long hotel_num, Pageable pageable);

    @Query("select q from Qna q where q.content like concat('%', :keyword, '%') and q.hotel.hotel_num = :hotel_num ")
    Page<Qna> findByContentContainingAndHotel_numContaining(String keyword, Long hotel_num, Pageable pageable);

    @Query("select q from Qna q where q.writer like concat('%', :str, '%') and q.hotel.hotel_num = :hotel_num")
    Page<Qna> selectlikeWriterAndHotel_numContaining(String str, Long hotel_num, Pageable pageable);

    @Query("select q from Qna q where q.title like concat('%', :str, '%') or q.content like concat('%', :str1, '%') and q.hotel.hotel_num = :hotel_num")
    Page<Qna> titleOrConAndHotel_numContaining(String str, String str1, Long hotel_num, Pageable pageable);

    @Query("select q from Qna q where q.writer like concat('%', :str, '%')or q.title like concat('%', :str1, '%') and q.hotel.hotel_num = :hotel_num ")
    Page<Qna> findByTitleContainingOrWriterContainingAndHotel_numContaining(String str, String str1, Long hotel_num, Pageable pageable);

    @Query("select q from Qna q where (q.title like concat('%', :str, '%') or q.content like concat('%', :str, '%') or q.writer like concat('%', :str, '%')) and q.hotel.hotel_num = :hotel_num ")
    Page<Qna> titleOrConOrWrAnaHotel_numContaining(String str, Long hotel_num, Pageable pageable);

    //브랜드용
    @Query("select q from Qna q where q.hotel.brand.brand_num = :brand_num")
    Page<Qna> findbyBrand(Long brand_num, Pageable pageable);

    @Query("select q from Qna q where q.title like concat('%', :keyword, '%')  and q.hotel.brand.brand_num = :brand_num ")
    Page<Qna> findByTitleAndBrand_numContaining(String keyword, Long brand_num, Pageable pageable);

    @Query("select q from Qna q where q.content like concat('%', :keyword, '%') and q.hotel.brand.brand_num = :brand_num ")
    Page<Qna> findByContentContainingAndBrand_numContaining(String keyword, Long brand_num, Pageable pageable);

    @Query("select q from Qna q where q.writer like concat('%', :str, '%') and q.hotel.brand.brand_num = :brand_num")
    Page<Qna> selectlikeWriterAndBrand_numContaining(String str, Long brand_num, Pageable pageable);

    @Query("select q from Qna q join q.hotel h where h.hotel_name like concat('%', :str, '%') and q.hotel.brand.brand_num = :brand_num")
    Page<Qna> selectlikeHotelAndBrand_numContaining(String str, Long brand_num, Pageable pageable);

    @Query("select q from Qna q where q.title like concat('%', :str, '%') or q.content like concat('%', :str1, '%') and q.hotel.brand.brand_num = :brand_num")
    Page<Qna> titleOrConAndBrand_numContaining(String str, String str1, Long brand_num, Pageable pageable);

    @Query("select q from Qna q where q.writer like concat('%', :str, '%')or q.title like concat('%', :str1, '%') and q.hotel.brand.brand_num = :brand_num ")
    Page<Qna> findByTitleContainingOrWriterContainingAndBrand_numContaining(String str, String str1, Long brand_num, Pageable pageable);

    @Query("select q from Qna q where (q.title like concat('%', :str, '%') or q.content like concat('%', :str, '%') or q.writer like concat('%', :str, '%') or q.hotel.hotel_name like concat('%', :str, '%')) "
           + "and q.hotel.brand.brand_num = :brand_num ")
    Page<Qna> titleOrConOrWrAnaBrand_numContaining(String str, Long brand_num, Pageable pageable);

}
