package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.constant.PayState;
import com.lookatme.smartstay.entity.BaseEntity;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.RoomItem;
import com.lookatme.smartstay.entity.RoomReserve;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PayDTO {

    private Long pay_num; //결제 번호

    private String pay_id; //결제 기록 번호

    private String card_id; //결제 카드 번호
    private Long room_price; //결제 금액
    private Long total_price; //총 결제 금액

    private PayState pay_state; //결제 여부

    private List<RoomItemDTO> roomItemDTOList = new ArrayList<>();
    private List<OrderItemDTO> orderItemDTOList = new ArrayList<>();

    private RoomReserveDTO roomReserveDTO; //룸 예약 조인

    private MemberDTO memberDTO; //회원 조인

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

}
