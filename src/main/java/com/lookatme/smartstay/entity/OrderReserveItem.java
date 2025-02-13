package com.lookatme.smartstay.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class OrderReserveItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceitem_num; //서비스 주문 번호

    private Long menu_count; //메뉴 수량

    private Long care_count; //케어 수량

    @Size(max=255)
    private String menu_request; //요청사항

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomreserveitem_num")
    private RoomReserveItem roomReserveItem; //예약 정보 및 룸 정보 가져오기

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_num")
    private Menu menu; //메뉴 조인

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "care_num")
    private Care care; //룸 케어 조인

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_num")
    private OrderReserve orderReserve;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pay_num")
    private Pay pay;
}
