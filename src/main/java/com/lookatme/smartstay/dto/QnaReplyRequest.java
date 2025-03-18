package com.lookatme.smartstay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

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

    private String writer;

    private Long qna_num; // Qna 엔티티 참조
    private Long member_id; // Member 엔티티 참조

}

