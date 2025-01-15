package com.lookatme.smartstay.Entity;

import com.lookatme.smartstay.Constant.PayState;
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
    private Long room_price; //결제 금액
    private Long total_price; //총 결제 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayState pay_state; //결제 여부

    @OneToMany(mappedBy = "pay", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RoomItem> roomItemList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserve_num")
    private RoomReserve	roomReserve; //룸 예약 조인

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num")
    private Member member; //회원 조인

}
