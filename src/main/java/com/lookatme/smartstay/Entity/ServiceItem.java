package com.lookatme.smartstay.Entity;

import com.lookatme.smartstay.Constant.OrderState;
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
public class ServiceItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long service_num; //서비스 주문 번호

    private Long menu_count; //메뉴 수량

    private Long care_count; //케어 수량

    @Size(max=255)
    private String menu_request; //요청사항

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_num")
    private Room room; //룸 조인

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_num")
    private RoomService	roomservice; //룸 서비스 조인

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "care_num")
    private CareService	careservice; //룸 케어서비스 조인

    @ManyToOne
    @JoinColumn(name = "order_num")
    private ServiceReserve serviceReserve;

    @ManyToOne
    @JoinColumn(name = "cart_num")
    private Cart cart; //장바구니
}
