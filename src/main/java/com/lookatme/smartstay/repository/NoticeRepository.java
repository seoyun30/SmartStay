package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    //조직 이나 작성자 나 날짜로 검색
    @Query("select n from Notice n where n.hotel.hotel_name like %:keyword% " +
            "or n.member.name like %:keyword%")
    List<Notice> searchByHotelOrWriter (String keyword);
}


