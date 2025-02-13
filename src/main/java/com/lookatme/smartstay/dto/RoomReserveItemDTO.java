package com.lookatme.smartstay.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lookatme.smartstay.entity.BaseEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "roomReserve")
@Builder
public class RoomReserveItemDTO extends BaseEntity {

    private Long roomreserveitem_num; //룸 아이템 기록 번호

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime in_date; //체크인 날짜

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime out_date; //체크아웃 날짜

    private Long day; //n박

    private Long count; //예약 인원수

    private String reserve_request; //요청 사항

    private Long room_num;

    private RoomDTO roomDTO; //룸

    private RoomReserveDTO roomReserveDTO;

    private PayDTO payDTO;

    public List<ImageDTO> imageDTOList = new ArrayList<>();

    private List<Long> imageIdList = new ArrayList<>();

    public RoomReserveItemDTO setRoomDTO(RoomDTO roomDTO) {
        this.roomDTO = roomDTO;
        return this;
    }
}
