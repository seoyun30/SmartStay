package com.lookatme.smartstay.dto;

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
public class ReviewDTO {

    private Long rev_num; //리뷰 번호

    @NotBlank(message = "리뷰 내용을 적어주세요")
    private String content; //리뷰 내용

    @NotBlank(message = "별점은 0.5점 부터 시작합니다")
    private String score; //별점

    @NotNull
    private Long roomreserveitem_num;

    private RoomReserveDTO roomReserveDTO; //룸 예약 조인

    // 룸 예약 설정
    public ReviewDTO setRoomReserveDTO(RoomReserveDTO roomReserveDTO) {
        this.roomReserveDTO = roomReserveDTO;
        return this;
    }

    private MemberDTO memberDTO; //회원 조인

//    public ReviewDTO setMemberDTO(MemberDTO memberDTO) {
//        this.memberDTO = memberDTO;
//        return this;
//    }

    private HotelDTO hotelDTO; //호텔 조인

    private RoomDTO roomDTO;

    // 호텔 설정
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
