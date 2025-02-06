package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Notice;

import com.lookatme.smartstay.repository.search.NoticeSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long>  , NoticeSearch {

    //호텔명으로 검색
    @Query("select n from Notice n where n.hotel.hotel_name like %:keyword%")
    List<Notice> searchByHotel (String keyword);

    //작성자로 검색
    @Query("select n from Notice n where n.member.name like %:keyword%")
    List<Notice> searchByWriter (String keyword);

    //제목으로 검색
    @Query("select n from Notice n where n.title like %:keyword%")
    List<Notice> searchByTitle (String keyword);

    //호텔명 이나 작성자 나 제목으로 검색
    @Query("select n from Notice n where n.hotel.hotel_name like %:keyword% " +
            "or n.member.name like %:keyword%")
    List<Notice> searchByHotelOrWriter (String keyword);

//    //등록된 순서대로
//    @Query(name = "Notice.findByreg_date")
//    Notice findByReg_date(@Param("reg_date") LocalDateTime reg_date);
//
//    @Query("select n from Notice n where n.reg_date in : reg_dates")
//    List<Notice> findByRegDate(@Param("reg_dates") List<LocalDateTime> reg_dates);
}


