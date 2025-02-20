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
public class Care extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long care_num; //케어 번호

    @Column(nullable = false)
    private String care_name; //케어 명

    @Column(nullable = false)
    private String care_detail; //케어 상세

    @Column(columnDefinition = "bigint default 0")
    private Long care_price = 0L; //케어 가격

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_num")
    private Hotel hotel; //호텔
}
