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
public class CareReserveItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carereserveitem_num; //케어 번호

    private Long care_count; // 개별 케어 서비스 수량

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serviceitem_num")
    @ToString.Exclude
    private OrderReserveItem orderReserveItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "care_num")
    @ToString.Exclude
    private Care care;
}
