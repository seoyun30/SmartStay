package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.entity.BaseEntity;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.Qna;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class QnaReplyDTO {

    private Long reply_num;

    private String comment;
    private String writer;

    private QnaDTO qnaDTO;

    private MemberDTO memberDTO;

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;
}
