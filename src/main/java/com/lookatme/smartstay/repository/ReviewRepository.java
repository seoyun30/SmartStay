package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Brand;
import com.lookatme.smartstay.entity.Hotel;
import com.lookatme.smartstay.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 해당 호텔과 관련된 모든 리뷰 조회
    @Query("select r from Review r where r.hotel.hotel_num =:hotel_num")
    List<Review> findByHotel(@Param("hotel") Long hotel_num);

    // 브랜드와 관련된 모든 호텔들의 리뷰 조회()
    @Query("select r from Review r where r.hotel.brand.brand_num =:brand_num")
    List<Review> findByHotelorBrand(@Param("brand") Long brand_num);

    // 해당 유저와 관련된 모든 리뷰 조회(이메일을 통해 조회)
    @Query("select r from Review r where r.member.email = :email")
    List<Review> findByUser(@Param("email") String email);


    //

}
