package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.constant.OrderState;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderReserveDTO {

    private Long order_num; //주문 기록 번호

    private String order_id; //주문 번호

    private Long total_price; //총 가격

    private OrderState order_state; //진행 상태

    private List<OrderReserveItemDTO> orderReserveItemDTOList = new ArrayList<>();

    private MemberDTO memberDTO; //회원 조인

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

    public OrderReserveDTO setMemberDTO(MemberDTO memberDTO) {
        this.memberDTO = memberDTO;
        return this;
    }

}
