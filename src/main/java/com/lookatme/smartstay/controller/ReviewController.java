package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.ImageDTO;
import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.dto.ReviewDTO;
import com.lookatme.smartstay.service.HotelService;
import com.lookatme.smartstay.service.MemberService;
import com.lookatme.smartstay.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/review")
public class ReviewController {

    private final MemberService memberService;
    private final HotelService hotelService;
    private final ReviewService reviewService;

    //관리자 리뷰 목록 페이지
    @GetMapping("/adMyReviewList")
    public String adMyReviewList(Principal principal, PageRequestDTO pageRequestDTO, Model model) {

        return null;
    }

    //호텔 리뷰 목록 페이지
    @GetMapping("/reviewList")
    public String reviewList(PageRequestDTO pageRequestDTO, Model model) {

        return null;
    }

    //마이 리뷰 목록(유저) 페이지
    @GetMapping("/myReviewList")
    public String myReviewList(Principal principal, PageRequestDTO pageRequestDTO, Model model) {

        return null;
    }

    //등록 페이지 이동
    @GetMapping("/reviewRegister")
    public String reviewRegisterFrom(Principal principal){
        log.info("입력폼 페이지 이동...");

        return "review/reviewRegister";
    }
//
    //등록 내용 저장
    @PostMapping("/reviewRegister")
    public String reviewRegisterPost(ReviewDTO reviewDTO,
                                     Long hotel_num, Principal principal, Long reserve_num,
                                     List<MultipartFile> multipartFileList, BindingResult bindingResult) throws Exception {

        log.info("입력폼 내용을 저장..." + reviewDTO);

        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors());

            return "review/reviewRegister";
        }

        reviewService.reviewRegister(reviewDTO, principal.getName(), hotel_num, reserve_num, multipartFileList );


        return "redirect:/review/reviewList";
    }
//
//    //상세 보기 페이지
//    @GetMapping("/reviewRead")
//    public String reviewRead(Long review_num,
//                             PageRequestDTO pageRequestDTO, Model model){
//        log.info("개별읽기...");
//        ReviewDTO reviewDTO = reviewService.reviewRead(review_num);
//
//        log.info("개별정보를 페이지에 전달...");
//        model.addAttribute("review", reviewDTO);
//
//        return "review/reviewRead";
//    }
//
//    //수정 페이지
//    @GetMapping("/reviewModify")
//    public String reviewModifyGet(Principal principal,
//            PageRequestDTO pageRequestDTO){
//
//        return "review/reviewModify";
//    }
//
//    @PostMapping("/reviewModify")
//    public String reviewModifyPost(ReviewDTO reviewDTO,
//                                   MemberDTO memberDTO, List<Long> delnumList,
//                                   List<MultipartFile> multipartFileList,
//                                   ImageDTO imageDTO,
//                                   PageRequestDTO pageRequestDTO){
//
//
//        return "redirect:/review/reviewList";
//    }
//
//    //삭제
//    @PostMapping("reviewDelete")
//    public String reviewDelete(Long review_num){
//        log.info("삭제 처리...");
//
//        return "redirect:/review/reviewList";
//    }

}
