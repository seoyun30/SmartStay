package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.entity.BaseEntity;
import com.lookatme.smartstay.entity.QnaReply;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.Qna;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QnaReplyRequest {

    private Long qnaReply_num;

    @NotBlank(message = "댓글은 공백일 수 없습니다.")
    @Size(min = 2, max = 255, message = "댓글은 2~255 글자 사이여야 합니다.")
    private String comment;

    @NotBlank(message = "작성자는 공백일 수 없습니다.")
    @Size(min = 2, max = 255, message = "작성자는 2~255 글자 사이여야 합니다.")
    private String writer;

    private Long qna_num; // Qna 엔티티 참조
    private Long member_id; // Member 엔티티 참조

    public QnaReply toEntity(Qna qna, Member member) {
        return QnaReply.builder()
                .comment(comment)
                .writer(writer)
                .qna(qna)
                .member(member)
                .build();
    }
}

