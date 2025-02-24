package com.lookatme.smartstay.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long service_num; //서비스 주문 번호

    @Size(max=255)
    private String menu_request; //요청사항

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomreserveitem_num")
    private RoomReserveItem roomReserveItem; //예약 정보 및 룸 정보 가져오기

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_num")
    private Cart cart; //장바구니

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pay_num")
    private Pay pay;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.REMOVE,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MenuItem> menuItemList = new ArrayList<>(); // 주문한 메뉴들

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.REMOVE,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CareItem> careItemList = new ArrayList<>(); // 주문한 케어 서비스들

}
