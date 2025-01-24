package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.entity.BaseEntity;
import com.lookatme.smartstay.entity.Hotel;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.RoomReserve;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ReviewDTO {

    private Long rev_num; //리뷰 번호

    private String content; //리뷰 내용

    private String score; //별점

    private RoomReserveDTO roomReserveDTO; //룸 예약 조인

    private MemberDTO memberDTO; //회원 조인

    private HotelDTO hotelDTO; //호텔 조인

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;
}
