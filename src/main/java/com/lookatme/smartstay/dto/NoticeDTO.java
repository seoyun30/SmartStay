package com.lookatme.smartstay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class NoticeDTO {


    private Long notice_num; //공지 번호

    @NotBlank(message = "제목을 입력해주세요")
    private String title; //제목

    @NotBlank(message = "내용을 입력해주세요")
    private String content; //내용

    private HotelDTO hotelDTO; //호텔(매장)

    private MemberDTO memberDTO; //회원 조인

    private LocalDateTime reg_date; //작성 시간

    private LocalDateTime modi_date; //수정 시간

    private String create_by; //작성자

    private String modified_by; //수정한 사람




}
