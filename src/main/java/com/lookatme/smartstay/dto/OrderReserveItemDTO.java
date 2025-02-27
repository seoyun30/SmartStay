package com.lookatme.smartstay.dto;

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
public class OrderReserveItemDTO {

    private Long serviceitem_num; //서비스 주문 번호

    private String menu_request; //요청사항

    private RoomReserveItemDTO roomReserveItemDTO; //예약 정보 및 룸 정보 가져오기

    private List<MenuReserveItemDTO> menuReserveItemDTOList = new ArrayList<>(); // 주문한 메뉴들

    private List<CareReserveItemDTO> careReserveItemDTOList = new ArrayList<>(); // 주문한 케어 서비스들

    private OrderReserveDTO orderReserveDTO;

    private PayDTO payDTO;

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

    public OrderReserveItemDTO setMenuReserveItemDTOList (List<MenuReserveItemDTO> menuReserveItemDTOList) {
        this.menuReserveItemDTOList = menuReserveItemDTOList;
        return this;
    }

    public OrderReserveItemDTO setCareReserveItemDTOList (List<CareReserveItemDTO> careReserveItemDTOList) {
        this.careReserveItemDTOList = careReserveItemDTOList;
        return this;
    }

    public OrderReserveItemDTO setOrderReserveDTO(OrderReserveDTO orderReserveDTO) {
        this.orderReserveDTO = orderReserveDTO;
        return this;
    }

    public OrderReserveItemDTO setPayDTO(PayDTO payDTO) {
        this.payDTO = payDTO;
        return this;
    }
}
