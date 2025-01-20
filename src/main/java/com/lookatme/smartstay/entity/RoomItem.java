package com.lookatme.smartstay.entity;

import com.lookatme.smartstay.constant.ServiceState;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RoomItem extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomitem_num; //룸 아이템 기록 번호

    private String in_date; //체크인 날짜
    private String out_date; //체크아웃 날짜
    private String count; //예약 인원수

    @Enumerated(EnumType.STRING)
    private ServiceState service_state; //룸 서비스 여부

    private String reserve_request; //요청 사항

    @ManyToOne
    @JoinColumn(name = "pay_num")
    private Pay pay;

    @ManyToOne
    @JoinColumn(name = "reserve_num")
    private RoomReserve roomReserve;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_num")
    private Room room; //룸

    @ManyToOne
    @JoinColumn(name = "cart_num")
    private Cart cart; //장바구니

}
