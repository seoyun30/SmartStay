package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.dto.NoticeDTO;
import com.lookatme.smartstay.entity.Notice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long>  {


    //Page<Notice> searchAll(String[] types, String keyword , String searchDateType, Pageable pageable);


    //Page<NoticeDTO> searchAll1(String[] types, String keyword , String searchDateType, Pageable pageable);

    //호텔명으로 검색
    @Query("select n from Notice n where n.hotel.hotel_name like %:keyword%")
    List<Notice> searchByHotel (String keyword);

    //작성자로 검색
    @Query("select n from Notice n where n.member.name like %:keyword%")
    List<Notice> searchByWriter (String keyword);

    //제목으로 검색
    @Query("select n from Notice n where n.title like %:keyword%")
    List<Notice> searchByTitle (String keyword);


    //호텔명 이나 작성자 나 날짜 나 제목으로 검색
    @Query("select n from Notice n where n.hotel.hotel_name like %:keyword% " +
            "or n.member.name like %:keyword%")
    List<Notice> searchByHotelOrWriter (String keyword);

}


