package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.entity.*;
import com.lookatme.smartstay.repository.HotelRepository;
import com.lookatme.smartstay.repository.MemberRepository;
import com.lookatme.smartstay.repository.ReviewRepository;
import com.lookatme.smartstay.repository.RoomReserveRepository;
import com.lookatme.smartstay.service.HotelService;
import com.lookatme.smartstay.service.MemberService;
import com.lookatme.smartstay.service.ReviewService;
import com.lookatme.smartstay.service.RoomReserveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
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
    private final HotelRepository hotelRepository;
    //리뷰
    private final ReviewService reviewService;
    private final ModelMapper modelMapper;
    private final ReviewRepository reviewRepository;


    //관리자 리뷰 목록 페이지(chief, manager권한)
    @GetMapping("/adMyReviewList")
    public String adMyReviewList(Principal principal,
                                 @RequestParam("brand_num") Long brand_num,
                                 @RequestParam("hotel_num") Long hotel_num, PageRequestDTO pageRequestDTO, Model model) {

        if (principal == null) {
            return "redirect:/member/login";
            //권한 : Chief(브랜드 별 리뷰목록)
        }

        Member member = memberRepository.findByEmail(principal.getName());
        if (member.getRole().name().equals("CHIEF")) {
            List<ReviewDTO> reviewDTOList = reviewService.getBrandReviewList(brand_num, principal.getName());
            model.addAttribute("reviewDTOList", reviewDTOList);
            reviewDTOList.forEach(reviewDTO -> log.info("reviewDTO: {}", reviewDTO));
            return "redirect:/review/adMyReviewList";

            //권한 : 매니저(호텔 별 리뷰목록)
        } else if (member.getRole().name().equals("MANAGER")) {
            List<ReviewDTO> reviewDTOList = reviewService.gethotelReviewList(hotel_num);
            model.addAttribute("reviewDTOList", reviewDTOList);
            reviewDTOList.forEach(reviewDTO -> log.info("reviewDTO: {}", reviewDTO));
            return "redirect:/review/adMyReviewList";
        }

        //권한이 없는 경우 에러
        model.addAttribute("errorMessage", "접근 권한이 없습니다.");
        return "redirect:/member/login";
    }

    //호텔 리뷰 목록
    @GetMapping("/reviewList/{hotel_num}")
    public String reviewList(@PathVariable("hotel_num") Long hotel_num, Model model,
                             PageRequestDTO pageRequestDTO, @RequestParam(value = "query", required = false) String query, Sort sort) {

        log.info("hotel_num: " + hotel_num);

        List<ReviewDTO> reviewDTOList = reviewService.gethotelReviewList(hotel_num);
        model.addAttribute("reviewDTOList", reviewDTOList);

        reviewDTOList.forEach(reviewDTO -> log.info("reviewDTO: {}", reviewDTO));

        return "review/reviewList";
    }


    //마이 리뷰 목록(user권한)
    @GetMapping("/myReviewList")
    public String myReviewList(Principal principal, PageRequestDTO pageRequestDTO, Model model) {
        log.info("My 리뷰");
        log.info("principal: " + principal.getName());

        List<ReviewDTO> reviewDTOList = reviewService.userMyReviewList(principal.getName());
        model.addAttribute("reviewDTOList", reviewDTOList);

        return "review/myReviewList";
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
    @PostMapping("/reviewRegister/{hotel_num}")
    public String reviewRegisterPost(@Valid ReviewDTO reviewDTO,
                                     @PathVariable("hotel_num") Long hotel_num,
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
            log.info("호텔 : {}", hotel_num);
            return "redirect:/reviewList/"+hotel_num; //성공 시 목록 페이지

        } catch (Exception e) {
            log.error("Error occurred while registering review: {}", e.getMessage());
            return "redirect:/review/register?error=true"; // 실패 시 다시 등록 페이지
        }

    }
//
    //상세 보기 페이지
    @GetMapping("/reviewRead")
    public String reviewRead(Long review_num,
                             PageRequestDTO pageRequestDTO, Model model){
        log.info("개별읽기...");
        ReviewDTO reviewDTO = reviewService.reviewRead(review_num);

        log.info("개별정보를 페이지에 전달...");
        model.addAttribute("review", reviewDTO);

        return "review/reviewRead";
    }

    //수정 페이지
    @GetMapping("/reviewModify")
    public String reviewModifyGet(Principal principal,
            PageRequestDTO pageRequestDTO){

        return "review/reviewModify";
    }

    @PostMapping("/reviewModify")
    public String reviewModifyPost(ReviewDTO reviewDTO,
                                   MemberDTO memberDTO, List<Long> delnumList,
                                   List<MultipartFile> multipartFileList,
                                   ImageDTO imageDTO,
                                   PageRequestDTO pageRequestDTO){


        return "redirect:/review/reviewList";
    }

    //삭제
    @PostMapping("reviewDelete")
    public String reviewDelete(Long review_num){
        log.info("삭제 처리...");

        return "redirect:/review/reviewList";
    }

}
