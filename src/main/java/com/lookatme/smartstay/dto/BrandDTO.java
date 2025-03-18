package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.constant.ActiveState;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BrandDTO {

    private Long brand_num;

    private String business_num;

    private String brand_name;

    private String owner;

    private String tel;

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

    public List<ImageDTO> imageDTOList = new ArrayList<>();

    private List<Long> imageIdList = new ArrayList<>();

    private ActiveState active_state;

    private ImageDTO mainImage;
}
