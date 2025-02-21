package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.entity.OrderItem;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class MenuItemDTO {

    private Long menuitem_num; //메뉴 번호

    private Long menu_count; // 개별 메뉴 수량

    private OrderItem orderItem;

    private MenuDTO menuDTO;

    private HotelDTO hotelDTO; //호텔

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;
}
