package com.lookatme.smartstay.entity;

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
@Builder
public class Pay extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pay_num; //결제 번호

    @Column(nullable = false)
    private String pay_id; //결제 기록 번호

    private String card_id; //결제 카드 번호

    private Long total_price; //총 결제 금액

    @OneToMany(mappedBy = "pay", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RoomItem> roomItemList = new ArrayList<>();

    @OneToMany(mappedBy = "pay", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItemList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num")
    private Member member; //회원 조인

}
