package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.Notice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("SELECT n FROM Notice n " +
            "LEFT JOIN Brand b on n.brand.brand_num = b.brand_num " +
            "WHERE n.brand.brand_num = :brand_num")
    Page<Notice> noticeBrandList(@Param("brand_num") Long brand_num, Pageable pageable);

    @Query("SELECT n FROM Notice n " +
            "LEFT  JOIN Brand b on n.brand.brand_num = b.brand_num " +
            "LEFT  JOIN Hotel h on n.hotel.hotel_num = h.hotel_num " +
            "WHERE (b.brand_num = :brand_num) " +
            "AND (" +
            "     n.title LIKE CONCAT('%', :keyword, '%') " +
            "  OR b.brand_name LIKE CONCAT('%', :keyword, '%')" +
            "  OR h.hotel_name LIKE CONCAT('%', :keyword, '%')" +
            ")")
    Page<Notice> searchNotice(@Param("brand_num") Long brand_num, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT n FROM Notice n " +
            "LEFT  JOIN Brand b on n.brand.brand_num = b.brand_num " +
            "LEFT  JOIN Hotel h on n.hotel.hotel_num = h.hotel_num " +
            "WHERE (b.brand_num = :brand_num) " +
            "AND (" +
            "     n.title LIKE CONCAT('%', :keyword, '%') " +
            ")")
    Page<Notice> searchNoticeByTitle(@Param("brand_num") Long brand_num, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT n FROM Notice n " +
            "LEFT  JOIN Brand b on n.brand.brand_num = b.brand_num " +
            "LEFT  JOIN Hotel h on n.hotel.hotel_num = h.hotel_num " +
            "WHERE (b.brand_num = :brand_num) " +
            "AND (" +
            "     b.brand_name LIKE CONCAT('%', :keyword, '%')" +
            "  OR h.hotel_name LIKE CONCAT('%', :keyword, '%')" +
            ")")
    Page<Notice> searchNoticeByHotelName(@Param("brand_num") Long brand_num, @Param("keyword") String keyword, Pageable pageable);


    @Query("SELECT n FROM Notice n " +
            "left join Brand b on n.brand.brand_num = b.brand_num " +
            "left join Hotel h  on n.hotel.hotel_num = h.hotel_num " +
            "where n.title like CONCAT('%', :keyword, '%') " +
            "   or h.hotel_name like CONCAT('%', :keyword, '%') " +
            "   or b.brand_name like  CONCAT('%', :keyword, '%')")
    Page<Notice> userSearchNotice(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT n FROM Notice n " +
            "left join Brand b on n.brand.brand_num = b.brand_num " +
            "left join Hotel h  on n.hotel.hotel_num = h.hotel_num " +
            "where n.title like CONCAT('%', :keyword, '%')")
    Page<Notice> userSearchNoticeByTitle(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT n FROM Notice n " +
            "left join Brand b on n.brand.brand_num = b.brand_num " +
            "left join Hotel h  on n.hotel.hotel_num = h.hotel_num " +
            "where h.hotel_name like CONCAT('%', :keyword, '%') " +
            "   or b.brand_name like  CONCAT('%', :keyword, '%')")
    Page<Notice> userSearchNoticeByHotelName(@Param("keyword") String keyword, Pageable pageable);


    @Query("select n from  Notice  n")
    Page<Notice> AllNotice(Pageable pageable);

}
