package com.lookatme.smartstay.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class PayDTO {

    private Long pay_num; //결제 번호

    private String merchant_uid; //주문 고유 번호

    private String imp_uid; //실결제 번호

    private String pay_method; //결제 방법

    private BigDecimal amount; //총 결제 금액

    private List<RoomItemDTO> roomItemDTOList = new ArrayList<>();

    private List<OrderItemDTO> orderItemDTOList = new ArrayList<>();

    private MemberDTO memberDTO; //회원 조인

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

}
