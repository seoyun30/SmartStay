package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.constant.RoomState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
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

    @NotBlank(message = "룸 타입을 입력해주세요.")
    private String room_type; //룸 타입

    @NotNull(message = "룸 인원을 입력해주세요.")
    private Long room_bed; //룸 인원수

    @NotNull(message = "룸 가격을 입력해주세요.")
    private Long room_price; //룸 가격

    @NotNull(message = "룸 상태를 설정해주세요.")
    private RoomState room_state; //룸 상태

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
    public void setHotelDTO(HotelDTO hotelDTO) {
        this.hotelDTO = hotelDTO;
    }
}
