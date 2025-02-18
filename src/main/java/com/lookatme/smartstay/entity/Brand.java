package com.lookatme.smartstay.entity;

import jakarta.persistence.*;
import lombok.*;

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

    // 양방향 매핑의 역방향(Non-owning side)-자식테이블
    @OneToOne(mappedBy = "brand", fetch = FetchType.LAZY)
    private Member member; // 해당 브랜드를 소유한 회원

    // 하나의 브랜드는 여러개의 호텔과 연결되어 있다. - 부모테이블
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_num") // Member 테이블에 brand_num 컬럼 생성
    private List<Hotel> hotelList = new ArrayList<>(); //호텔 목록

    // 하나의 브랜드는 여러개의 이미지와연결되어 있다. - 부모테이블
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "image_num") // Member 테이블에 brand_num 컬럼 생성
    private List<Image> imageList = new ArrayList<>(); //이미지 목록
}
