package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.constant.ServiceState;
import com.lookatme.smartstay.entity.*;
import jakarta.persistence.*;
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

    private ServiceState service_state; //룸 서비스 여부

    private String reserve_request; //요청 사항

    private PayDTO payDTO;

    private RoomReserveDTO roomReserveDTO;

    private RoomDTO roomDTO; //룸

    private CartDTO cartDTO; //장바구니

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

}
