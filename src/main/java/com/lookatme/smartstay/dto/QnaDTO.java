package com.lookatme.smartstay.dto;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class QnaDTO {

    private Long qna_num; //문의 번호

    private String title; //문의 제목

    private String content; //문의 내용

    private String writer; //문의 작성자

    //private String category; //카테고리

    private int viewCount; // 조회수 추가

    private Long hotel_num;

    private HotelDTO hotelDTO;

    public QnaDTO setHotelDTO(HotelDTO hotelDTO) {
        this.hotelDTO = hotelDTO;
        return this;
    }

    public List<ImageDTO> imageDTOList = new ArrayList<>();

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;
}

