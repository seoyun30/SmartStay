package com.lookatme.smartstay.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReviewDTO {

    private Long rev_num; //리뷰 번호

    @NotNull(message = "리뷰 내용을 적어주세요")
    private String content; //리뷰 내용

    @NotBlank(message = "별점은 0.5점 부터 시작합니다")
    private String score; //별점

    public String getScore() {
        return score;
    }
    public void setScore(String score) {
        this.score = score;
    }

    @NotNull
    private Long roomreserveitem_num;

    private RoomReserveDTO roomReserveDTO; //룸 예약 조인

//    private Long brand_num;

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

    public ReviewDTO setRoomDTO(RoomDTO roomDTO) {
        this.roomDTO = roomDTO;
        return this;
    }

    // 작성일
    private LocalDateTime reg_date;

    // 경과 시간 계산
    public  String getFormattedReg_date() {
        return getTimeAgo(reg_date);
    }
    private String getTimeAgo(LocalDateTime pastDateTime) {
        Duration duration = Duration.between(pastDateTime, LocalDateTime.now());
        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return "방금 전";
        } else if (seconds < 3600) {
            return (seconds / 60) + "분 전";
        } else if (seconds < 86400) { //허루 (24시간) 이내
            return (seconds /3600) + "시간 전";
        } else {
            return (seconds / 86400) + "일 전";
        }
    }

    private LocalDateTime getReg_date() {
        return reg_date;
    }
    public void setReg_date(LocalDateTime reg_date) {
        this.reg_date = reg_date;
    }


    // 수정일
    private LocalDateTime modi_date;


    private String create_by;

    private String modified_by;

    //이미지
    public List<ImageDTO> imageDTOList = new ArrayList<>(); //여러 이미지를 담는 용

    private List<Long> imageIdList = new ArrayList<>(); // 이미지 식별용(이미지 ID를 저장하는 리스트)

    private ImageDTO mainImage;

    public ImageDTO getMainImage() {
        return mainImage;
    }

    public void setMainImageDTO(ImageDTO mainImage) {
        this.mainImage = mainImage;
    }



}
