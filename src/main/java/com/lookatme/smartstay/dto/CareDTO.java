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

public class CareDTO {

    private Long care_num; //케어 번호

    private String care_name; //케어 명

    private String care_detail; //케어 상세

    private Long care_price; //케어 가격

    private HotelDTO hotelDTO; //호텔

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

    public List<ImageDTO> imageDTOList = new ArrayList<>();

    public List<Long> imageIdList = new ArrayList<>();
}
