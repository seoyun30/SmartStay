package com.lookatme.smartstay.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RoomItemDTO {

    private Long roomitem_num; //룸 아이템 기록 번호

    private String in_date; //체크인 날짜

    private String out_date; //체크아웃 날짜

    private String count; //예약 인원수

    private String reserve_request; //요청 사항

    private Long room_num; //룸 번호

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

}
