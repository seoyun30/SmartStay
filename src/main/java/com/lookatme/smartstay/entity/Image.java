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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chief_num")
    private Chief chief; //호텔 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_num")
    private Manager	manager; //매장 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_num")
    private Room room; //룸 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_num")
    private Menu menu; //룸 서비스 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "care_num")
    private Care care; //케어 서비스 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rev_num")
    private Review review; //후기 글 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qna_num")
    private Qna qna; //후기 글 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_num")
    private Notice notice; //후기 글 번호

}
