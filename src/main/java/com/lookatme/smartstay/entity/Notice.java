package com.lookatme.smartstay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notice_num; //공지 번호

    private String title; //제목
    private String content; //내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chief_num")
    private Chief chief; //호텔(총판)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_num")
    private Manager	manager; //호텔(매장)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num")
    private Member member; //회원 조인

}
