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
public class HotelDTO {

    private Long hotel_num; //키

    private String business_num; //개인 사업자번호

    private String hotel_name; //호텔 명

    private String owner; //대표자 이름

    private String address; //호텔 주소

    private String tel; //연락처

    private String score; //별점

    private BrandDTO brandDTO; //브랜드

    public HotelDTO setBrandDTO(BrandDTO brandDTO) {
        this.brandDTO = brandDTO;
        return this;
    } //추가

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

    public Long lowestPrice;

    public List<ImageDTO> imageDTOList = new ArrayList<>();

    private List<Long> imageIdList = new ArrayList<>();

    private ActiveState active_state; //활성 비활성

    private ImageDTO mainImage;

    public ImageDTO getMainImage() {
        return mainImage;
    }

    public void setMainImage(ImageDTO mainImage) {
        this.mainImage = mainImage;
    }

    private int review_count;
}
