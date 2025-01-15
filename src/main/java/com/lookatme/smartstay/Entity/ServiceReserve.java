package com.lookatme.smartstay.Entity;

import com.lookatme.smartstay.Constant.OrderState;
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
public class ServiceReserve extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long order_num; //주문 기록 번호

    @Column(unique = true, nullable = false)
    private String order_id; //주문 번호

    private Long total_price; //총 가격

    @Column(nullable = false)
    private OrderState order_state; //진행 상태

    @OneToMany(mappedBy = "serviceReserve", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ServiceItem> serviceItemList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num")
    private Member member; //회원 조인

}
