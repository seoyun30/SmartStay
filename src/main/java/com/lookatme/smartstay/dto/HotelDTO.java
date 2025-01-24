package com.lookatme.smartstay.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class HotelDTO {

    private Long hotel_num; //키

    private String business_num; //개인 사업자번호

    private String hotel_name; //호텔 명

    private String owner; //대표자 이름

    private String address; //호텔 주소

    private String tel; //연락처

    private String score; //별점

    private BrandDTO brandDTO; //브랜드

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

}