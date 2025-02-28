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
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long image_id; //이미지 번호

    @Column(nullable = false)
    private String image_name; //UUID + 이미지 원본 이름

    @Column(nullable = false)
    private String origin_name; //이미지 원본 이름

    @Column(nullable = false)
    private String image_url; //이미지 경로

    @Column(nullable = false)
    private String repimg_yn; //대표 이미지 여부

    @Column
    private String thumbnail_url; // 썸네일 URL

    @Column(nullable = false)
    private String targetType;

    // 부모 테이블인 Brand와의 관계에서 자식 테이블인 Image에서 'ManyToOne'으로 관계 설정) 모두 삭제
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_num") // Image 테이블에 brand_num 외래 키 추가
    private Brand brand; //브랜드 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_num")
    @ToString.Exclude
    private Hotel hotel; //호텔 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_num")
    private Room room; //룸 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_num")
    private Menu menu; //메뉴 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "care_num")
    private Care care; //룸 케어 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rev_num")
    private Review review; //리뷰 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qna_num")
    private Qna qna; //문의사항 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_num")
    private Notice notice; //공지사항 이미지

}
