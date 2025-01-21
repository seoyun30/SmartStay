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
public class Chief extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chief_num; //키

    @Column(unique = true, nullable = false)
    private String business_num; //사업자번호

    @Column(nullable = false)
    private String hotel_name; //호텔 명
    private String owner; //대표자 이름
    private String address; //호텔 주소
    private String tel; //연락처
    private String score; //별점
}
