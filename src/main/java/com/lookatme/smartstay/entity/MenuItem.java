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
public class MenuItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuitem_num; //메뉴 번호

    private Long menu_count; // 개별 메뉴 수량

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_num")
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_num")
    private Menu menu;
}
