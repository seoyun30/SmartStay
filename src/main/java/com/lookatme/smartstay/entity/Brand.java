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
public class Brand extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long brand_num; //키

    @Column(unique = true, nullable = false)
    private String business_num; //법인 사업자번호

    @Column(nullable = false)
    private String brand_name; //브랜드 명

    private String owner; //대표자 이름

    private String tel; //연락처
}
