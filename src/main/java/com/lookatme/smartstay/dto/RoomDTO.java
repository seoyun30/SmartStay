package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.constant.RoomState;
import lombok.*;
import lombok.extern.log4j.Log4j2;


import java.time.LocalDateTime;

@Log4j2
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoomDTO {

    private Long room_num; //룸 번호
    private String room_name; //룸 명
    private String room_info; //룸 정보
    private String room_type; //룸 타입
    private Long room_bed; //룸 인원수
    private Long room_price; //룸 가격

    private RoomState room_state; //룸 상태

    private HotelDTO hotelDTO; //호텔

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;


}
