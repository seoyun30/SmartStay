package com.lookatme.smartstay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class QnaReplyDTO {

    private Long qnaReply_num;

    @NotBlank(message = "댓글은 공백일 수 없습니다.")
    @Size(min = 2, max = 255, message = "댓글은 2~255 글자 사이여야 합니다.")
    private String comment;

    private String writer;

    private QnaDTO qnaDTO;

    private MemberDTO memberDTO;

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

    public QnaReplyDTO setMemberDTO(MemberDTO memberDTO) {
        this.memberDTO = memberDTO;
        return this;
    }
}
