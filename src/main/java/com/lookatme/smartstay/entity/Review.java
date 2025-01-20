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
public class Review extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rev_num; //리뷰 번호
    private String content; //리뷰 내용
    private String writer; //작성자
    private String score; //별점

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserve_num")
    private RoomReserve roomReserve; //룸 예약 조인

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num")
    private Member member; //회원 조인
}
