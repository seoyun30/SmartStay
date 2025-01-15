package com.lookatme.smartstay.Entity;

import com.lookatme.smartstay.Entity.Member.Chief;
import com.lookatme.smartstay.Entity.Member.Manager;
import com.lookatme.smartstay.Entity.Member.Member;
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
    private String writer; //작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chief_num")
    private Chief chief; //호텔(총판)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_num")
    private Manager manager; //호텔(매장)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserve_num")
    private RoomReserve	roomReserve; //룸 예약

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num")
    private Member member; //회원 조인

}
