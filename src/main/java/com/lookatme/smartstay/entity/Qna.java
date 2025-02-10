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
public class Qna extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qna_num; //문의 번호

    private String title; //문의 제목

    private String content; //문의 내용

    private String writer; //문의 작성자

}
