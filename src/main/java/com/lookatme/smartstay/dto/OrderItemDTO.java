package com.lookatme.smartstay.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class OrderItemDTO {

    private Long service_num; //서비스 주문 번호

    private Long[] service_nums;

    private String menu_request; //요청사항

    @JsonProperty("roomreserveitem_num")
    private Long roomreserveitem_num;

    private RoomReserveItemDTO roomReserveItemDTO; //예약 정보 및 룸 정보 가져오기

    private List<MenuItemDTO> menuItemDTOList = new ArrayList<>(); // 여러 개의 메뉴 주문 가능

    private List<CareItemDTO> careItemDTOList = new ArrayList<>(); // 여러 개의 케어 주문 가능

    private CartDTO cartDTO; //장바구니

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

    public OrderItemDTO setRoomReserveItemDTO (RoomReserveItemDTO roomReserveItemDTO) {
        this.roomReserveItemDTO = roomReserveItemDTO;
        return this;
    }

}
