package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Brand;
import com.lookatme.smartstay.entity.Cart;
import com.lookatme.smartstay.entity.Qna;
import com.lookatme.smartstay.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface QnaRepository extends JpaRepository<Qna, Long> {
    //QnaSearch 검색기능 추후 추가

    @Query("SELECT q FROM Qna q WHERE q.writer = :writer")
    List<Qna> findByWriter(@Param("writer") String writer);  // 작성자로 QnA 목록 조회

    @Query("SELECT q FROM Qna q WHERE q.writer = :writer")
    Page<Qna> findByWriter(@Param("writer") String writer, Pageable pageable);  // 작성자로 QnA 목록 조회 페이징

    @Query("select q from Qna q")
    Page<Qna> selcetAll(Pageable pageable); //페이징처리 목록 전부

    //제목으로 검색
    // select * from board where title like '%파라미터%'
    @Query("select q from Qna q where q.title like concat('%', :keyword, '%') ")
    public Page<Qna> findByTitleContaining(String keyword, Pageable pageable);

    //내용으로 검색   // 쿼리메소드
    @Query("select q from Qna q where q.content like concat('%', :keyword, '%') ")
    public Page<Qna> findByContentContaining(String keyword, Pageable pageable);

    //작성자 검색
    @Query("select q from Qna q where q.writer like concat('%', :str, '%') ")
    public Page<Qna> selectlikeWriter(String str, Pageable pageable);

    //호텔명 검색
    @Query("select q from Qna q join q.hotel h where h.hotel_name like concat('%', :str, '%')")
    public Page<Qna> selectlikeHotel(String str, Pageable pageable);

    @Query("select q from Qna q where q.title like concat('%', :str, '%') or q.content like concat('%', :str, '%') ")
    public Page<Qna> titleOrCon(String str, Pageable pageable);

    @Query("select q from Qna q where q.writer like concat('%', :str, '%')or q.content like concat('%', :str1, '%') ")
    public Page<Qna> findByContentContainingOrWriterContaining(String str, String str1, Pageable pageable);

    @Query("select q from Qna q where q.writer like concat('%', :str, '%')or q.title like concat('%', :str1, '%') ")
    public Page<Qna> findByTitleContainingOrWriterContaining(String str, String str1, Pageable pageable);

    @Query("select q from Qna q where q.title like concat('%', :str, '%') or q.content like concat('%', :str, '%') or q.writer like concat('%', :str, '%') ")
    public Page<Qna> titleOrConOrWr(String str, Pageable pageable);

    //mylist용
    @Query("select q from Qna q where q.title like concat('%', :keyword, '%')  and q.writer = :email ")
    public Page<Qna> findByTitleAndWriterContaining(String keyword, String email, Pageable pageable);

    @Query("select q from Qna q where q.content like concat('%', :keyword, '%')  and q.writer = :email ")
    public Page<Qna> findByContentAndWriterContaining(String keyword, String email, Pageable pageable);

    @Query("select q from Qna q join q.hotel h where h.hotel_name like concat('%', :str, '%') and q.writer = :email ")
    public Page<Qna> selectlikeHotelAndWriter(String str, String email, Pageable pageable);

    @Query("select q from Qna q where q.title like concat('%', :str, '%') or q.content like concat('%', :str, '%') and q.writer = :email ")
    public Page<Qna> titleOrConAndWriter(String str, String email, Pageable pageable);

    //호텔로 검색 페이징
    @Query("select q from Qna q where q.hotel.hotel_num = :hotel_num")
    Page<Qna> findbyHotel(Long hotel_num, Pageable pageable);

    @Query("select q from Qna q where q.title like concat('%', :keyword, '%')  and q.hotel.hotel_num = :hotel_num ")
    public Page<Qna> findByTitleAndHotel_numContaining(String keyword, Long hotel_num, Pageable pageable);

    @Query("select q from Qna q where q.content like concat('%', :keyword, '%') and q.hotel.hotel_num = :hotel_num ")
    public Page<Qna> findByContentContainingAndHotel_numContaining(String keyword, Long hotel_num, Pageable pageable);

    @Query("select q from Qna q where q.writer like concat('%', :str, '%') and q.hotel.hotel_num = :hotel_num")
    public Page<Qna> selectlikeWriterAndHotel_numContaining(String str, Long hotel_num, Pageable pageable);

    @Query("select q from Qna q where q.title like concat('%', :str, '%') or q.content like concat('%', :str1, '%') and q.hotel.hotel_num = :hotel_num")
    public Page<Qna> titleOrConAndHotel_numContaining(String str, String str1, Long hotel_num, Pageable pageable);

    @Query("select q from Qna q where q.writer like concat('%', :str, '%')or q.title like concat('%', :str1, '%') and q.hotel.hotel_num = :hotel_num ")
    public Page<Qna> findByTitleContainingOrWriterContainingAndHotel_numContaining(String str, String str1, Long hotel_num, Pageable pageable);

    @Query("select q from Qna q where q.title like concat('%', :str, '%') or q.content like concat('%', :str, '%') or q.writer like concat('%', :str, '%')and q.hotel.hotel_num = :hotel_num ")
    public Page<Qna> titleOrConOrWrAnaHotel_numContaining(String str, Long hotel_num, Pageable pageable);

    //브랜드로 검색 페이징
    @Query("select q from Qna q where q.hotel.brand.brand_num = :brand_num")
    Page<Qna> findbyBrand(Long brand_num, Pageable pageable);





   /* @Query("SELECT r FROM Room r WHERE LOWER(r.room_name) LIKE LOWER(CONCAT('%', :roomName, '%'))")
    List<Room> findByRoomNameContainingIgnoreCase(@Param("roomName") String roomName, Sort sort);

    @Query("SELECT r FROM Room r WHERE LOWER(r.room_info) LIKE LOWER(CONCAT('%', :roomInfo, '%'))")
    List<Room> findByRoomInfoContainingIgnoreCase(@Param("roomInfo") String roomInfo, Sort sort);*/

}
