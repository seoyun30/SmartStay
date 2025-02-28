package com.lookatme.smartstay.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ImageDTO {

    private Long image_id; //이미지 번호

    private String image_name; //UUID + 이미지 원본 이름

    private String origin_name; //이미지 원본 이름

    private String image_url; //이미지 경로

    private String repimg_yn; //대표 이미지 여부

    private String thumbnail_url; // 썸네일 URL

    private String targetType;

    private BrandDTO brandDTO; //브랜드 이미지

    private HotelDTO hotelDTO; //호텔 이미지

    private RoomDTO roomDTO; //룸 이미지

    private MenuDTO menuDTO; //메뉴 이미지

    private CareDTO careDTO; //룸 케어 이미지

    private ReviewDTO reviewDTO; //리뷰 이미지

    private QnaDTO qnaDTO; //문의사항 이미지

    private NoticeDTO noticeDTO; //공지사항 이미지

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

}
