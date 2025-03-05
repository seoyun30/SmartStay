package com.lookatme.smartstay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class NoticeDTO {


    private Long notice_num; //공지 번호

    @NotBlank(message = "제목을 입력해주세요.")
    private String title; //제목

    @NotBlank(message = "내용을 입력해주세요.")
    private String content; //내용

    private Long hotel_num;
    private Long brand_num;

    private BrandDTO brandDTO; //호텔(매장)

    private HotelDTO hotelDTO;

    private MemberDTO memberDTO; //회원 조인

    private LocalDateTime reg_date; //작성 시간

    private LocalDateTime modi_date; //수정 시간

    private String hotel_name;

    private String create_by; //작성자

    private String modified_by; //수정한 사람


    //이미지 리스트들..
    public List<ImageDTO> imageDTOList = new ArrayList<ImageDTO>(); // 여러 이미지를 담는 용
    private List<Long> imageIdList =new ArrayList<>(); //이미지 식별용 ()

    //공지사항 메인 이미지
    private ImageDTO mainImage;

    // 공지사항 메인 이미지 getter
    public ImageDTO getMainImageDTO() {
        return mainImage;
    }

    // 공지사항 메인 이미지 setter
    public void setMainImageDTO(ImageDTO mainImage) {
        this.mainImage = mainImage;
    }

    //공지사항 이미지
    private ImageDTO noticeImages;






}
