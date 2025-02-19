package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.constant.MenuSort;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MenuDTO {

    private Long menu_num; //메뉴 번호

    private String menu_name; //메뉴 명

    private MenuSort menu_sort; //메뉴 분류

    private String menu_detail; //메뉴 상세

    private Long menu_price; //메뉴 가격

    private HotelDTO hotelDTO; //호텔

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

    public List<ImageDTO> imageDTOList = new ArrayList<>();

    public List<Long> imageIdList = new ArrayList<>();

    public MenuDTO setHotelDTO(HotelDTO hotelDTO) {
        this.hotelDTO = hotelDTO;
        return this;
    }
}
