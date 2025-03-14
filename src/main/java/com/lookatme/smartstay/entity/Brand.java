package com.lookatme.smartstay.entity;

import com.lookatme.smartstay.constant.ActiveState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @Enumerated(EnumType.STRING)
    private ActiveState active_state; //활성 비활성

}
