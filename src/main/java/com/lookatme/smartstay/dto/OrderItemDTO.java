package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.entity.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class OrderItemDTO extends BaseEntity {

    private Long service_num; //서비스 주문 번호

    private Long menu_count; //메뉴 수량

    private Long care_count; //케어 수량

    private String menu_request; //요청사항

    private RoomDTO roomDTO; //룸 조인

    private MenuDTO menuDTO; //룸 서비스 조인

    private CareDTO careDTO; //룸 케어서비스 조인

    private OrderReserve orderReserve;

    private CartDTO cartDTO; //장바구니

    private LocalDateTime regDate;

    private LocalDateTime modiDate;

    private String createBy;

    private String modifiedBy;

}
