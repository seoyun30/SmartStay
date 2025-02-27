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
public class MenuReserveItemDTO {

    private Long menureserveitem_num; //메뉴 번호

    private Long menu_count; // 개별 메뉴 수량

    private OrderReserveItemDTO orderReserveItem;

    private MenuDTO menuDTO;

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

    public List<ImageDTO> imageDTOList = new ArrayList<>();

    private List<Long> imageIdList = new ArrayList<>();

    public MenuReserveItemDTO setMenuDTO (MenuDTO menuDTO) {
        this.menuDTO = menuDTO;
        return this;
    }
}
