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
public class Qna extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qna_num; //문의 번호

    private String title; //문의 제목

    private String content; //문의 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_num")
    private Hotel hotel; //호텔

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserve_num")
    private RoomReserve	roomReserve; //룸 예약

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num")
    private Member member; //회원 조인

}
