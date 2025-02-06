package com.lookatme.smartstay.repository.search;

import com.lookatme.smartstay.dto.NoticeDTO;
import com.lookatme.smartstay.entity.Notice;
import com.lookatme.smartstay.entity.QNotice;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class NoticeSearchImpl extends QuerydslRepositorySupport implements NoticeSearch {

    public NoticeSearchImpl() {
        super(Notice.class);
    }

    @Override
    public Page<Notice> searchAll(String[] types, String keyword , String searchDateType, Pageable pageable) {

        QNotice notice = QNotice.notice; // Q도메인 객체 entity를 QNotice로 바꾼것

        JPQLQuery<Notice> query = from(notice); // select * from notice

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (types != null && types.length > 0 && keyword != null) {
            for (String type : types) {
                if (type.equals("t"))
                    switch (type){
                    case "t":
                        booleanBuilder.or(notice.title.contains(keyword));
                        break;
                    case "h":
                        booleanBuilder.or(notice.hotel.hotel_name.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(notice.member.name.contains(keyword));
                        break;

                    } //switch //만약에 th가 들어왔다면 where문 이후 title like %키워드% or notice.hotel_name like %키워드%
            } //for문

        } //if문 //검색조건까지
        query.where(booleanBuilder); //검색조건완료
        System.out.println(query);
        System.out.println("-----------------");

        query.where(notice.notice_num.gt(0L));  //select * from notice // notice.nnotice_num > 0
        System.out.println(query);
        System.out.println("-----------------");

        //페이징
        this.getQuerydsl().applyPagination(pageable, query);
        //데이터 List<T>
        List<Notice> noticeList = query.fetch();

        // 총게시물 수 row
        long count =
                query.fetchCount();

        //return
        return new PageImpl<>(noticeList, pageable, count);

    }

    public Page<NoticeDTO> searchAll1(String[] types, String keyword , String searchDateType, Pageable pageable) {

        QNotice notice = QNotice.notice;  // Q 도메인 객체 entity를 Qnotice로 바꿈

        JPQLQuery<Notice> query = from(notice); //select * from notice

        System.out.println(query); //select * from notice
        System.out.println("----------------------");

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (types != null && types.length > 0 && keyword != null) {
            for (String type : types) {
                if (type.equals("t"))
                    switch (type){
                    case "t":
                        booleanBuilder.or(notice.title.contains(keyword));
                        break;
                    case "h":
                        booleanBuilder.or(notice.hotel.hotel_name.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(notice.member.name.contains(keyword));
                        break;

                    } //switch
            } //for문

        } //if문
        query.where(booleanBuilder); //검색조건완료
        System.out.println(query);
        System.out.println("-------------------");

        query.where(notice.notice_num.gt(0L)); //select * from notice// // notice.notice_num

        System.out.println(query);
        System.out.println("--------------------");

        //페이징
        this.getQuerydsl().applyPagination(pageable, query);
        //데이터 List<T>
        List<Notice> noticeList = query.fetch();

        // 총게시물수 row수
        long count =
                query.fetchCount();

        //return
        return new PageImpl<>(noticeList.stream().map(notice1 -> new ModelMapper().map(notice1, NoticeDTO.class)).collect(Collectors.toList()), pageable, count);

    }


//    private BooleanBuilder betweemTime(LocalDateTime reg_date, LocalDateTime modi_date, boolean conTainTimeClosing) {
//
//        if (conTainTimeClosing) {
//
//            if (reg_date == null){
//                return null;
//            }
//
//            return betweemTime(reg_dateAppointmentDate.between(reg_date, modi_date)).;
//
//        }
//    }



}
