package com.lookatme.smartstay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class MenuReserveItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menureserveitem_num; //메뉴 번호

    private Long menu_count; // 개별 메뉴 수량

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serviceitem_num")
    @ToString.Exclude
    private OrderReserveItem orderReserveItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_num")
    @ToString.Exclude
    private Menu menu;
}
