package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Brand;
import com.lookatme.smartstay.entity.Hotel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Query("select h from Hotel h where h.create_by = :email")
    List<Hotel> findByEmail(String email);

    @Query("select h from Hotel h where h.hotel_name like %:query% or h.address like %:query%")
    List<Hotel> findByHotel_nameOrAddressContaining(@Param("query") String query);

    @Query("select h from Hotel h where h.member.email = :email")
    Optional<Hotel> findHotelByMemberEmail(@Param("email") String email);

    //브랜드는 남겨두고 호텔을 삭제하기 위한 메서드
    @Modifying
    @Transactional
    @Query("update Hotel h set h.brand = null where h.hotel_num = :hotel_num")
    void updateHotelBrandToNull(@Param("hotel_num") Long hotel_num);

    //브랜드에 속한 호텔 목록 조회
    @Query("select h from Hotel h where h.brand= :brand")
    List<Hotel> findByMyBrand(Brand brand);

}
