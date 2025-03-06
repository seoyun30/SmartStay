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

    // 1:1 관계 (Qna와의 관계)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "qna_num") // qna_num 외래 키로 연결
    private Qna qna; // 1:1 관계 - 하나의 질문에 하나의 답변만

    //댓글수정
    public void update(String commen) {
        this.comment = commen;
    }
}
