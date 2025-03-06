package com.lookatme.smartstay.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Qna extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qna_num; //문의 번호

    @Column(nullable = false)
    private String title; //문의 제목

    @Column(nullable = false, length = 5000)
    private String content; //문의 내용

    private String writer; //문의 작성자

    //private String category;//카테고리

    @ColumnDefault("0")
    private int viewCount;

    // 조회수 증가 메서드
    public void incrementViewCount() {
        this.viewCount += 1;
    }

    // 1:1 관계 설정
    @OneToOne(mappedBy = "qna", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QnaReply qnaReply; // 하나의 문의에 하나의 답변만

    // 댓글 추가 메서드 (1:1 관계에 맞추기 위해 수정)
    public void addReply(QnaReply reply) {
        this.qnaReply = reply; // 1:1 관계이므로 단일 답변만 설정
        reply.setQna(this); // Qna에 답변 설정
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_num")
    private Hotel hotel; //호텔

   /* @OneToMany(mappedBy = "qna", cascade = CascadeType.ALL)
    private List<QnaReply> qnaReplyList = new ArrayList<>(); //qna*/

    @OneToMany(mappedBy = "qna", cascade = CascadeType.ALL)
    private List<Image> imageList = new ArrayList<>(); //이미지


}
