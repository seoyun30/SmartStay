package com.lookatme.smartstay.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
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
public class RoomItemDTO {

    private Long roomitem_num; //룸 아이템 기록 번호

    private Long[] roomitem_nums;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @NotNull
    private LocalDateTime in_date; //체크인 날짜

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @NotNull
    private LocalDateTime out_date; //체크아웃 날짜

    @NotNull
    private Long room_num;

    private Long day; //n박

    @Min(value = 1, message = "최소 예약 인원수는 1명입니다.")
    private Long count; //예약 인원수

    private String reserve_request; //요청 사항

    private RoomDTO roomDTO;

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

    public List<ImageDTO> imageDTOList = new ArrayList<>();

    private List<Long> imageIdList = new ArrayList<>();

    public RoomItemDTO setRoomDTO(RoomDTO roomDTO) {
        this.roomDTO = roomDTO;
        return this;
    }
}
