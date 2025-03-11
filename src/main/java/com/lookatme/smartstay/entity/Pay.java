package com.lookatme.smartstay.entity;

import com.lookatme.smartstay.constant.OrderState;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Pay extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pay_num; //결제 번호

    @Column(nullable = false)
    private String merchant_uid; //주문 고유 번호

    private String imp_uid; //실결제 번호

    private String pay_method; //결제 방법

    private BigDecimal amount; //총 결제 금액

    @Enumerated(EnumType.STRING)
    private OrderState pay_state; //결제 상태

    @OneToMany(mappedBy = "pay", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<RoomItem> roomItemList = new ArrayList<>();

    @OneToMany(mappedBy = "pay", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<RoomReserveItem> roomReserveItemList = new ArrayList<>();

    @OneToMany(mappedBy = "pay", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<OrderItem> orderItemList = new ArrayList<>();

    @OneToMany(mappedBy = "pay", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<OrderReserveItem> orderReserveItemList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_num")
    private Member member; //회원 조인

}
