package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.entity.Hotel;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.RoomReserve;
import com.lookatme.smartstay.repository.MemberRepository;
import com.lookatme.smartstay.repository.RoomReserveRepository;
import com.lookatme.smartstay.service.HotelService;
import com.lookatme.smartstay.service.MemberService;
import com.lookatme.smartstay.service.ReviewService;
import com.lookatme.smartstay.service.RoomReserveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/review")
public class ReviewController {

    // 멤버
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    //룸 예약
    private final RoomReserveService roomReserveService;
    private final RoomReserveRepository roomReserveRepository;
    //호텔
    private final HotelService hotelService;
    //리뷰
    private final ReviewService reviewService;
    private final ModelMapper modelMapper;


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

    //등록 페이지 이동(유저)
    @GetMapping("/reviewRegister")
    public String reviewRegisterFrom(@RequestParam Long reserve_num,
                                    Principal principal, Model model) {
        log.info("입력폼 페이지 이동...");

        if (reserve_num == null) {
            log.error("reserve_num이 제공되지 않았습니다.");
            return "redirect:/errorPage"; //예외
        } else if (principal == null) {
            return "redirect:/member/login";
        }

        if (!roomReserveService.validateroomResereve(reserve_num, principal.getName())) {
            log.error("예약 회원의 이메일과 로그인 이메일이 일치하지 않습니다.");
            return "redirect:/member/myRoomReserveRead"; //
        };

        RoomReserveItemDTO roomReserveItemDTO = roomReserveService.findRoomReserveItem(reserve_num, principal.getName());

        log.info(roomReserveItemDTO);

        //모델에 세팅한 reviewDTO와 roomReserveItemDTO를 전달
        model.addAttribute("roomReserveItemDTO", roomReserveItemDTO); //룸예약 정보
        model.addAttribute("reviewDTO", new ReviewDTO());

        return "review/reviewRegister"; // 로그인한 사용자만 볼수 있음
    }

    //등록 내용저장
    @PostMapping("/reviewRegister")
    public String reviewRegisterPost(@Valid ReviewDTO reviewDTO,
                                     BindingResult bindingResult,
                                     Long reserve_num,
                                     Principal principal,
                                     List<MultipartFile> multipartFileList) throws Exception {

        log.info("리뷰 등록 :{}", reviewDTO); // reviewDTO상태 확인
        log.info("principal: {}", principal);

        if (bindingResult.hasErrors()) {
            log.error("Binding errors: {}", bindingResult.getAllErrors());
            return "review/reviewRegister"; //오류가 있을 경우 등록페이지로 이동
        }

        if (!roomReserveService.validateroomResereve(reserve_num, principal.getName())) {
            log.error("예약 회원의 이메일과 로그인 이메일이 일치하지 앟습니다.");
            return "redirect:/member/myRoomReserveRead"; //
        };

        //리뷰 등록 처리
        try {
            reviewService.reviewRegister(reviewDTO, principal.getName(), multipartFileList);
            return "redirect:/review/reviewList"; //성공 시 목록 페이지
        } catch (Exception e) {
            log.error("Error occurred while registering review: {}", e.getMessage());
            return "redirect:/review/register?error=true"; // 실패 시 다시 등록 페이지
        }
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
