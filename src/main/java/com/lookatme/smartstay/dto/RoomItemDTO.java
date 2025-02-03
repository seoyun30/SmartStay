package com.lookatme.smartstay.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RoomItemDTO {

    private Long roomitem_num; //룸 아이템 기록 번호

    @NotNull
    private String in_date; //체크인 날짜

    @NotNull
    private String out_date; //체크아웃 날짜

    @Min(value = 1, message = "최소 예약 인원수는 1명입니다.")
    private Long count; //예약 인원수

    private String reserve_request; //요청 사항

    @NotNull
    private Long room_num; //룸 번호

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

}
