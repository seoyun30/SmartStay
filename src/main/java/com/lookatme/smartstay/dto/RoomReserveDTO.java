package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.constant.CheckState;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoomReserveDTO {

    private Long reserve_num; //룸 예약 번호

    private String reserve_id; //예약 기록 번호

    private CheckState check_state; //체크인 여부

    private List<RoomItemDTO> roomItemDTOList = new ArrayList<>();

    private MemberDTO memberDTO; //회원

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

}
