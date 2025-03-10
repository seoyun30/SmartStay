package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.dto.ReviewDTO;
import com.lookatme.smartstay.dto.RoomReserveItemDTO;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.repository.HotelRepository;
import com.lookatme.smartstay.repository.MemberRepository;
import com.lookatme.smartstay.repository.ReviewRepository;
import com.lookatme.smartstay.repository.RoomReserveRepository;
import com.lookatme.smartstay.service.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final ImageService imageService;


    //관리자 리뷰 목록 페이지(chief, manager권한)
    @GetMapping("/adMyReviewList")
    public String adMyReviewList(Principal principal,
                                 @RequestParam(value = "brand_num", required = false) Long brand_num,
                                 @RequestParam(value = "hotel_num", required = false) Long hotel_num, PageRequestDTO pageRequestDTO, Model model) {

        if (principal == null) {
            return "redirect:/member/login";
            //권한 : Chief(브랜드 별 리뷰목록)
        }

        Member member = memberRepository.findByEmail(principal.getName());
        if (member.getRole().name().equals("CHIEF")) {
            List<ReviewDTO> reviewDTOList = reviewService.getBrandReviewList(brand_num, principal.getName());
            model.addAttribute("reviewDTOList", reviewDTOList);
            reviewDTOList.forEach(reviewDTO -> log.info("reviewDTO: {}", reviewDTO));
            return "review/adMyReviewList";

            //권한 : 매니저(호텔 별 리뷰목록)
        } else if (member.getRole().name().equals("MANAGER")) {
            List<ReviewDTO> reviewDTOList = reviewService.gethotelReviewList(hotel_num);
            model.addAttribute("reviewDTOList", reviewDTOList);
            reviewDTOList.forEach(reviewDTO -> log.info("reviewDTO: {}", reviewDTO));
            return "review/adMyReviewList";
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
    public String reviewRegisterGet(@RequestParam("reserve_num") Long reserve_num,
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
        log.info(new ReviewDTO());

        return "review/reviewRegister"; // 로그인한 사용자만 볼수 있음
    }

    //등록 내용저장
    @PostMapping("/reviewRegister/{reserve_num}")
    public String reviewRegisterPost(@Valid ReviewDTO reviewDTO, RedirectAttributes redirectAttributes,
                                     @PathVariable("reserve_num") Long reserve_num, Long hotel_num,
                                     @RequestParam(value = "multipartFiles", required = false) List<MultipartFile> multipartFiles,
                                     @RequestParam(value = "mainImageIndex", required = false, defaultValue = "0") Long mainImageIndex,
                                     //BindingResult bindingResult,
                                     Principal principal) throws Exception {

        System.out.println("받은 reserve_num: " + reserve_num);
        log.info("리뷰 등록 :{}", reviewDTO); // reviewDTO상태 확인
        log.info("principal: {}", principal);


//        if (bindingResult.hasErrors()) {
//            log.error("Binding errors: {}", bindingResult.getAllErrors());
//            return "review/reviewRegister"; //오류가 있을 경우 등록페이지로 이동
//        }
        if (!roomReserveService.validateroomResereve(reserve_num, principal.getName())) {
            log.error("예약 회원의 이메일과 로그인 이메일이 일치하지 앟습니다.");
            return "redirect:/member/myRoomReserveRead"; //
        };

        //리뷰 등록 처리
        try {
            // 리뷰 등록 서비스 호출(이미지 다중 업로드)
            reviewService.reviewRegister(reviewDTO, principal.getName(), multipartFiles, mainImageIndex);
            redirectAttributes.addFlashAttribute("msg", "등록이 완료 되었습니다.");
            log.info("호텔 : {}", hotel_num);
            return "redirect:/review/reviewList/" +hotel_num; // "문자열" + id = "redirect:/reviewList/" +hotel_num

        } catch (Exception e) {
            log.error("Error occurred while registering review: {}", e.getMessage());
            return "redirect:/review/register?error=true"; // 실패 시 다시 등록 페이지
        }

    }
//
    //상세 보기 페이지
    @GetMapping("/reviewRead")
    public String reviewRead(@RequestParam(required = false) Long rev_num, Long hotel_num,
                             Model model, RedirectAttributes redirectAttributes) {

        if (rev_num == null) {
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 리뷰입니다.");
            return "redirect:/review/reviewList/" + hotel_num;
        }

        try {
            log.info("개별읽기...");
            ReviewDTO reviewDTO = reviewService.reviewRead(rev_num);

            log.info("개별정보를 페이지에 전달...");
            model.addAttribute("review", reviewDTO);
            log.info("받은 정보 : {}", reviewDTO );

            if (reviewDTO.getHotelDTO() != null) {
                model.addAttribute("hotel_name", reviewDTO.getHotelDTO().getHotel_name());
            } else {
                model.addAttribute("hotel_name", "해당 호텔 정보가 없습니다.");
            }
            return "review/reviewRead";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("msg", "해당 리뷰를 찾을 수 없습니다..");
            return "redirect:/review/reviewList/" + hotel_num;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msg", "알 수 없는 오류가 발생 되었습니다.");
            return "redirect:/review/reviewList/" + hotel_num;
        }
    }

    //수정 페이지
    @GetMapping("/reviewModify")
    public String reviewModifyGet(@RequestParam Long rev_num, Principal principal, Model model) {

        if (principal == null) {
            return "redirect:/member/login";
        }
        ReviewDTO reviewDTO = reviewService.reviewRead(rev_num);

        if (reviewDTO.getHotelDTO() == null) {
            throw new IllegalArgumentException("리뷰에 해당하는 호텔을 찾을 수 없습니다.");
        }

        if (!reviewDTO.getCreate_by().equals(principal.getName())) {
            throw new SecurityException("수정 권한이 없습니다.");
        }
        model.addAttribute("reviewDTO", reviewDTO);
        log.info("들어옴?" + reviewDTO);

        return "review/reviewModify";
    }

    @PostMapping("/reviewModify")
    public String reviewModifyPost(ReviewDTO reviewDTO, Principal principal, Model model,
                                   RedirectAttributes redirectAttributes, Long hotel_num,
                                   @RequestParam(value = "multipartFiles", required = false) List<MultipartFile> multipartFiles,
                                   @RequestParam(value = "delnumList", required = false) List<Long> delnumList){

        log.info("수정 요청 : {}" , reviewDTO);

        if (multipartFiles != null && multipartFiles.stream().allMatch(MultipartFile::isEmpty)) {
            multipartFiles = null;
        }
        if (delnumList != null && delnumList.isEmpty()) {
            delnumList = null;
        }

        //getCreate_by를 못불러온다.. 수정 테스트 완료
//        if (!reviewDTO.getCreate_by().equals(principal.getName())) {
//            throw new SecurityException("수정 권한이 없습니다.");
//        }


        try {
            reviewService.reviewModify(reviewDTO, multipartFiles, delnumList);
            redirectAttributes.addFlashAttribute("msg", "리뷰가 수정되었습니다.");
            return "redirect:/review/reviewList/" + hotel_num;
        } catch (Exception e) {
            log.error("수정 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "수정 중 오류가 발생하였습니다.");
            return "redirect:/review/reviewModify?rev_num=" + reviewDTO.getRev_num() ;
        }

    }

    //삭제
    @PostMapping("reviewDelete")
    public String reviewDelete(@RequestParam("id") Long rev_num, Principal principal, RedirectAttributes redirectAttributes) {
        log.info("삭제 처리...");

        if (principal == null) {
            return "redirect:/member/login";
        }

        ReviewDTO reviewDTO = reviewService.reviewRead(rev_num);

        if (!reviewDTO.getCreate_by().equals(principal.getName())) {
            throw new SecurityException("리뷰 삭제 권한이 없습니다.");
        }

        try {
            reviewService.reviewDelete(rev_num);
            redirectAttributes.addFlashAttribute("successMessage", "삭제 완료");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "삭제 오류 발생");
        }

        return "redirect:/review/myReviewList";
    }

    @DeleteMapping("/deleteImage/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable("imageId") Long imageId){
        try {
            imageService.deleteImage(imageId);
            return ResponseEntity.ok("이미지가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            log.error("이미지 삭제 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 삭제 실패");
        }
    }
}
