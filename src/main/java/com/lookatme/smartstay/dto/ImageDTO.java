package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.entity.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ImageDTO {

    private Long image_id; //이미지 번호

    private String image_name; //UUID + 이미지 원본 이름

    private String origin_name; //이미지 원본 이름

    private String image_url; //이미지 경로

    private String repimg_yn; //대표 이미지 여부

    private ChiefDTO chiefDTO; //호텔 이미지

    private ManagerDTO managerDTO; //매장 이미지

    private RoomDTO roomDTO; //룸 이미지

    private MenuDTO menuDTO; //룸 서비스 이미지

    private CareDTO careDTO; //케어 서비스 이미지

    private ReviewDTO reviewDTO; //후기 글 번호

    private QnaDTO qnaDTO; //후기 글 번호

    private NoticeDTO noticeDTO; //후기 글 번호

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

}
