package com.lookatme.smartstay.DTO;

import com.lookatme.smartstay.Constant.RoomState;
import com.lookatme.smartstay.Entity.Chief;
import com.lookatme.smartstay.Entity.Manager;
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

    private Chief chief; //호텔(총판)

    private Manager manager; //호텔(매장)

    private LocalDateTime regDate;

    private LocalDateTime modiDate;

    private String createBy;

    private String modifiedBy;


}
