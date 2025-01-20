package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.entity.Chief;
import com.lookatme.smartstay.entity.Manager;
import com.lookatme.smartstay.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NoticeDTO {

    private Long notice_num; //공지 번호
    private String title; //제목
    private String content; //내용


    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime reg_Date; //작성 시간

    @LastModifiedDate
    private LocalDateTime modi_date; //수정 시간

    @CreatedBy
    @Column(updatable = false)
    private String create_by; //작성자
    private String modified_by; //수정한 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chief_num")
    private Chief chief; //호텔(총판)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_num")
    private Manager manager; //호텔(매장)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num")
    private Member member; //회원 조인


}
