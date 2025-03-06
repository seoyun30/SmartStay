package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {


    //리뷰 리스트
    // 해당 호텔의 모든 리뷰조회
    @Query("select r from Review r where r.hotel.hotel_num =:hotel_num")
    List<Review> findByHotel(Long hotel_num);

    // 브랜드의 모든 리뷰조회
    @Query("select r from Review r where r.hotel.brand.brand_num =:brand_num")
    List<Review> findByHotelorBrand(@Param("brand") Long brand_num);

    // 해당 유저의 모든 리뷰조회(이메일을 통해 조회)
    @Query("select r from Review r where r.member.email = :email")
    List<Review> findByUser(@Param("email") String email);


//    /**카테고리 name 으로 review 찾기**/
//    Page<Review> findAllByOrderByCountDesc(Pageable pageable);  //조회수
//
//    Page<Review> findAllByOrderByViewsDesc(Pageable pageable);  // 조회순
//
//    Page<Review> findAllByOrderByDateTimeDesc(Pageable pageable); // 시간순
//
//    Page<Review> findAllByOrderByReviewLikeDesc(Pageable pageable);  // 별점 순

//    // Acs : 오름차, Desc : 내림차
//    List<Review> findByScoreOrderByScoreDesc(String score);
//    List<Review> findByScoreOrderByScoreAsc(String score);

//    List<Review> findByReg_dateContaining(String reg_date);
//
//    List<Review> findByScoreContaining(String score);


    //
    @Query("SELECT r FROM Review r WHERE r.hotel.hotel_num = :hotel_num ORDER BY r.reg_date DESC")
    List<Review> findTopNByHotelNum(@Param("hotel_num") Long hotel_num, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.room.room_num = :room_num ORDER BY r.reg_date DESC")
    List<Review> findTopNByRoomNum(@Param("room_num") Long room_num, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.hotel.hotel_num = :hotel_num")
    int countByHotelNum(@Param("hotel_num") Long hotel_num);



}
