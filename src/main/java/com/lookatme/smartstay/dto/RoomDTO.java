package com.lookatme.smartstay.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lookatme.smartstay.constant.RoomState;
import com.lookatme.smartstay.constant.RoomType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoomDTO {

    private Long room_num; //룸 번호

    @NotBlank(message = "룸 이름을 입력해주세요.")
    private String room_name; //룸 명

    @NotBlank(message = "룸 정보를 입력해주세요.")
    private String room_info; //룸 정보

    @NotNull(message = "룸 타입을 설정해주세요.")
    private RoomType room_type; //룸 타입

    @NotNull(message = "룸 인원을 입력해주세요.")
    @Min(value = 2, message = "룸 최소 인원은 2인입니다.")
    private Long room_bed = 2L; //룸 인원수

    @NotNull(message = "룸 가격을 입력해주세요.")
    @Min(value = 100, message = "룸 최소 가격은 100원입니다.")
    private Long room_price; //룸 가격

    @NotNull(message = "룸 상태를 설정해주세요.")
    private RoomState room_state; //룸 상태

    @JsonFormat(pattern = "HH:mm")
    @NotNull
    private LocalTime in_time = LocalTime.of(15, 0); // 오후 3시

    @JsonFormat(pattern = "HH:mm")
    @NotNull
    private LocalTime out_time = LocalTime.of(11, 0); // 오전 11시

    private HotelDTO hotelDTO; //호텔

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

    public List<ImageDTO> imageDTOList = new ArrayList<>();

    private List<Long> imageIdList = new ArrayList<>();

    public HotelDTO getHotelDTO() {
        return hotelDTO;
    }

    public RoomDTO setHotelDTO(HotelDTO hotelDTO) {
        this.hotelDTO = hotelDTO;
        return this;
    }

    private ImageDTO mainImage;

    public ImageDTO getMainImage() {
        return mainImage;
    }

    public void setMainImage(ImageDTO mainImage) {
        this.mainImage = mainImage;
    }
}
