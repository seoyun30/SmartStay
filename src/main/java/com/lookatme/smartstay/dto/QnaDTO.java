package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.entity.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    private String writer; //작성자

    private ChiefDTO chiefDTO; //호텔(총판)

    private ManagerDTO managerDTO; //호텔(매장)

    private RoomReserveDTO roomReserveDTO; //룸 예약

    private MemberDTO memberDTO; //회원 조인

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

}
