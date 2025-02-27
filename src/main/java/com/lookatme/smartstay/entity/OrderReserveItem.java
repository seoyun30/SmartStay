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
public class OrderReserveItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceitem_num; //서비스 주문 번호

    @Size(max=255)
    private String menu_request; //요청사항

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "roomreserveitem_num")
    private RoomReserveItem roomReserveItem; //예약 정보 및 룸 정보 가져오기

    @OneToMany(mappedBy = "orderReserveItem", cascade = CascadeType.REMOVE,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MenuReserveItem> menuReserveItemList = new ArrayList<>(); // 주문한 메뉴들

    @OneToMany(mappedBy = "orderReserveItem", cascade = CascadeType.REMOVE,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CareReserveItem> careReserveItemList = new ArrayList<>(); // 주문한 케어 서비스들

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_num")
    private OrderReserve orderReserve;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "pay_num")
    private Pay pay;

    public static OrderReserveItem creatOrederReserveItem(List<MenuReserveItem> menuReserveItemList, List<CareReserveItem> careReserveItemList,
                                                          String menu_request, RoomReserveItem roomReserveItem) {

        OrderReserveItem orderReserveItem = new OrderReserveItem();
        orderReserveItem.setMenu_request(menu_request);
        orderReserveItem.setRoomReserveItem(roomReserveItem);
        orderReserveItem.setMenuReserveItemList(menuReserveItemList);
        orderReserveItem.setCareReserveItemList(careReserveItemList);

        return orderReserveItem;

    }

}
