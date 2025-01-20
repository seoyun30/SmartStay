package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.constant.OrderState;
import com.lookatme.smartstay.entity.BaseEntity;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.OrderItem;
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
public class OrderReserveDTO extends BaseEntity {

    private Long order_num; //주문 기록 번호

    private String order_id; //주문 번호

    private Long total_price; //총 가격

    private OrderState order_state; //진행 상태

    private List<OrderItemDTO> orderItemDTOList = new ArrayList<>();

    private MemberDTO memberDTO; //회원 조인

    private LocalDateTime regDate;

    private LocalDateTime modiDate;

    private String createBy;

    private String modifiedBy;

}