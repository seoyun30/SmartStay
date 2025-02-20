package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.constant.MenuSort;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "메뉴 이름을 입력해주세요.")
    private String menu_name; //메뉴 명

    @NotNull(message = "메뉴 카테고리를 설정해주세요.")
    private MenuSort menu_sort; //메뉴 분류

    @NotBlank(message = "메뉴 설명을 입력해주세요.")
    private String menu_detail; //메뉴 상세

    @NotNull(message = "메뉴 가격을 입력해주세요.")
    @Min(value = 100, message = "메뉴 최소 가격은 100원입니다.")
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
