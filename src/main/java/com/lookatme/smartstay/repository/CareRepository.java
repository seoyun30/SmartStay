package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Care;
import com.lookatme.smartstay.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CareRepository extends JpaRepository<Care, Long> {

    @Query("select c from Care c where c.hotel = :hotel")
    Page<Care> findByHotel(@Param("hotel")Hotel hotel, Pageable pageable);

    @Query("select c from Care c where c.care_name like %:query% or c.care_detail like %:query%")
    List<Care> findByCare_nameContainingIgnoreCaseOrCare_detailContainingIgnoreCase(@Param("query") String query, Sort sort);

    @Query("select c from Care c")
    List<Care> findAllCares();
}