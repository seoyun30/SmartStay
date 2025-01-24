package com.lookatme.smartstay.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class QnaDTO {

    private Long qna_num; //문의 번호

    private String title; //문의 제목

    private String content; //문의 내용

    private HotelDTO hotelDTO; //호텔(매장)

    private RoomReserveDTO roomReserveDTO; //룸 예약

    private MemberDTO memberDTO; //회원 조인

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

}
