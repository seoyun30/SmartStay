package com.lookatme.smartstay.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class BrandDTO {

    private Long brand_num; //키

    private String business_num; //법인 사업자번호

    private String brand_name; //브랜드 명

    private String owner; //대표자 이름

    private String tel; //연락처

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

}
