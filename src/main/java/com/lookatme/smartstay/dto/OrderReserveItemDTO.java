package com.lookatme.smartstay.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class OrderReserveItemDTO {

    private Long serviceitem_num; //서비스 주문 번호

    private Long menu_count; //메뉴 수량

    private Long care_count; //케어 수량

    private String menu_request; //요청사항

    private RoomReserveItemDTO roomReserveItemDTO; //예약 정보 및 룸 정보 가져오기

    private MenuDTO menuDTO; //음식 주문 조인

    private CareDTO careDTO; //룸 케어 조인

    private CartDTO cartDTO; //장바구니

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

}
