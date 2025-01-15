package com.lookatme.smartstay.Entity;

import com.lookatme.smartstay.Constant.CheckState;
import com.lookatme.smartstay.Constant.PayState;
import com.lookatme.smartstay.Entity.Member.Member;
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
public class RoomReserve extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reserve_num; //룸 예약 번호

    @Column(unique = true, nullable = false)
    private String reserve_id; //예약 기록 번호

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayState pay_state; //결제 상태

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckState check_state; //체크인 여부

    @OneToMany(mappedBy = "roomReserve", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RoomItem> roomItemList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num")
    private Member member; //회원

}
