package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Care;
import com.lookatme.smartstay.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CareRepository extends JpaRepository<Care, Long> {

    @Query("select c from Care c where c.hotel = :hotel")
    Page<Care> findByHotel(@Param("hotel")Hotel hotel, Pageable pageable);

    @Query("SELECT c FROM Care c WHERE c.hotel.hotel_num = :hotelNum AND LOWER(c.care_name) LIKE LOWER(CONCAT('%', :careName, '%'))")
    Page<Care> findByCare_nameContainingIgnoreCase(@Param("hotelNum") Long hotelNum, @Param("careName") String careName, Pageable pageable);

    @Query("SELECT c FROM Care c WHERE c.hotel.hotel_num = :hotelNum AND LOWER(c.care_detail) LIKE LOWER(CONCAT('%', :careDetail, '%'))")
    Page<Care> findByCare_detailContainingIgnoreCase(@Param("hotelNum") Long hotelNum, @Param("careDetail") String careDetail, Pageable pageable);

    @Query("SELECT c FROM Care c " +
            "WHERE c.hotel.hotel_num = :hotelNum " +
            "AND LOWER(c.care_name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.care_detail) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Care> findByCare_nameContainingIgnoreCaseOrCare_detailContainingIgnoreCase(@Param("hotelNum") Long hotelNum, @Param("keyword") String keyword, Pageable pageable);

}