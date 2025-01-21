package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.entity.Chief;
import com.lookatme.smartstay.entity.Manager;
import com.lookatme.smartstay.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class NoticeDTO {

    private Long notice_num; //공지 번호
    private String title; //제목
    private String content; //내용
    private LocalDateTime reg_Date; //작성 시간
    private LocalDateTime modi_date; //수정 시간
    private String create_by; //작성자
    private String modified_by; //수정한 사람
    private ChiefDTO chiefDTO; //호텔(총판)
    private ManagerDTO managerDTO; //호텔(매장)
    private MemberDTO memberDTO; //회원 조인


}
