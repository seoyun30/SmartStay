package com.lookatme.smartstay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class QnaReply extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qnaReply_num;

    @Column(nullable = false, length = 5000)
    private String comment;

    private String writer;

    /*@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "qna_num")
    private Qna qna;*/

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_num")
    private Member member;

    // QnA와 QnaReply의 N:1 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qna_num") // 외래 키 컬럼 이름
    private Qna qna;

    //댓글수정
    public void update(String comment) {
        this.comment = comment;
    }

    // Lombok의 @ToString 제거 후 직접 작성

}
