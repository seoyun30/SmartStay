package com.lookatme.smartstay.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CareItemDTO{

    private Long careitem_num; //케어 번호

    private Long care_count; // 개별 케어 서비스 수량

    private OrderItemDTO orderItemDTO;

    private CareDTO careDTO;

    private HotelDTO hotelDTO; //호텔

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

}
