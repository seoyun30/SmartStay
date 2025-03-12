package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.constant.CheckState;
import com.lookatme.smartstay.entity.Hotel;
import com.lookatme.smartstay.entity.Review;
import com.lookatme.smartstay.entity.RoomReserveItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    //리뷰 리스트
    // 해당 호텔의 모든 리뷰조회
    @Query("select r from Review r where r.hotel.hotel_num =:hotel_num")
    List<Review> findByHotel(Long hotel_num);

    //페이지 형식으로 호텔 리스트
    @Query("select r from  Review r where r.hotel.hotel_num =:hotel_num")
    Page<Review> findByAdHotel(Long hotel_num, Pageable pageable);

    // 해당 유저의 모든 리뷰조회(이메일을 통해 조회)
    @Query("select r from Review r where r.member.email = :email")
    List<Review> findByUser(@Param("email") String email);

    @Query("select r from Review r where r.member.email = :email and r.roomReserve.reserve_num = :reserve_num")
    Review findByEmailAndReserveNum(String email, Long reserve_num);

    // 검색 기능
    @Query("SELECT r FROM Review r WHERE r.room.hotel.hotel_num = :hotel_num "
            + "AND (:hotel_name IS NULL OR r.room.hotel.hotel_name LIKE %:hotel_name%) "
            + "AND (:room_name IS NULL OR r.room.room_name LIKE %:room_name%) "
            + "AND (:rev_name IS NULL OR r.create_by LIKE %:rev_name%)")
    Page<Review> findReviewBySearch(
            @Param("hotel_num") Long hotel_num,
            @Param("hotel_name") String hotel_name,
            @Param("room_name") String room_name,
            @Param("rev_name") String rev_name,
            Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.room.hotel.hotel_num = :hotel_num "
            + "AND (:hotel_name IS NULL OR r.room.hotel.hotel_name LIKE %:hotel_name%) "
            + "AND ((:room_name IS NULL OR r.room.room_name LIKE %:room_name%) "
            + "OR (:rev_name IS NULL OR r.create_by LIKE %:rev_name%))")
    Page<Review> findReviewBySearchAll(
            @Param("hotel_num") Long hotel_num,
            @Param("hotel_name") String hotel_name,
            @Param("room_name") String room_name,
            @Param("rev_name") String rev_name,
            Pageable pageable);

    /**리뷰를 시간순, 별점 순, ,.. 으로 조회하기 **/
//    @Query("SELECT r FROM Review r ORDER BY r.reg_date DESC ")
//    Page<Review> findByOrderByReg_dateDesc(Pageable pageable);

//    Page<Review> findAllByOrderByCountDesc(Pageable pageable);  //조회수 // 아직
//
//    Page<Review> findAllByOrderByViewsDesc(Pageable pageable);  // 조회순  // 아직
//
//    Page<Review> findAllByOrderByDateTimeDesc(Pageable pageable); // 시간순
//
//    Page<Review> findAllByOrderByReviewLikeDesc(Pageable pageable);  // 별점 순

//    // Acs : 오름차, Desc : 내림차
    @Query("SELECT r FROM Review r")
    Page<Review> findAllReviews(Pageable pageable);

    //
    @Query("SELECT r FROM Review r WHERE r.hotel.hotel_num = :hotel_num ORDER BY r.reg_date DESC")
    List<Review> findTopNByHotelNum(@Param("hotel_num") Long hotel_num, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.room.room_num = :room_num ORDER BY r.reg_date DESC")
    List<Review> findTopNByRoomNum(@Param("room_num") Long room_num, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.hotel.hotel_num = :hotel_num")
    int countByHotelNum(@Param("hotel_num") Long hotel_num);



}
