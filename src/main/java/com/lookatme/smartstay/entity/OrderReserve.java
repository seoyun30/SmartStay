package com.lookatme.smartstay.entity;

import com.lookatme.smartstay.constant.OrderState;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderReserve extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long order_num; //주문 기록 번호

    @Column(unique = true, nullable = false)
    private String order_id; //주문 번호

    private Long total_price; //총 가격

    @Column(nullable = false)
    private OrderState order_state; //진행 상태

    @OneToMany(mappedBy = "orderReserve", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderReserveItem> orderReserveItemList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_num")
    private Member member; //회원 조인

}
