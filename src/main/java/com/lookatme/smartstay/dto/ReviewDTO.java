package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.entity.BaseEntity;
import com.lookatme.smartstay.entity.Hotel;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.RoomReserve;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReviewDTO {

    private Long rev_num; //리뷰 번호

    @NotBlank(message = "리뷰 내용을 적어주세요")
    private String content; //리뷰 내용

    @NotBlank(message = "별점 주세요")
    private String score; //별점

    private RoomReserveDTO roomReserveDTO; //룸 예약 조인

    // 룸 예약 가져오는
    public ReviewDTO setRoomReserveDTO(RoomReserveDTO roomReserveDTO) {
        this.roomReserveDTO = roomReserveDTO;
        return this;
    }

    private MemberDTO memberDTO; //회원 조인

    private HotelDTO hotelDTO; //호텔 조인

    // 호텔 가져오는
    public ReviewDTO setHotelDTO(HotelDTO hotelDTO) {
        this.hotelDTO = hotelDTO;
        return this;
    }

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

    //이미지 리스트들..
    public List<ImageDTO> imageDTOList = new ArrayList<>(); //여러 이미지를 담는 용
    private List<Long> imageIdList = new ArrayList<>(); // 이미지 식별용(이미지 ID를 저장하는 리스트)

}
