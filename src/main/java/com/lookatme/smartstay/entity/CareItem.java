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
public class CareItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long careitem_num; //케어 번호

    private Long care_count; // 개별 케어 서비스 수량

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_num")
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "care_num")
    private Care care;
}
