package com.lookatme.smartstay.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hotel> hotels = new ArrayList<>();

    @OneToMany(mappedBy = "brand")
    private List<Member> members = new ArrayList<>();
}
