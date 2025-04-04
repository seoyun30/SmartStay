package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.*;
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
import java.util.Collections;
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


    //관리자 리뷰 목록 페이지( manager권한)
    @GetMapping("/adMyReviewList")
    public String adMyReviewList(PageRequestDTO pageRequestDTO, Model model, Principal principal,
                                 @RequestParam(defaultValue = "rev_num") String sortField,
                                 @RequestParam(defaultValue = "asc") String sortDir,
                                 @RequestParam(required = false) String searchType,
                                 @RequestParam(required = false) String searchKeyword) {

        //로그인 회원조회
        if (principal == null) {
            return "redirect:/member/login";
        }

        //로그인한 사용자 정보로 호텔 조회
        HotelDTO hotelDTO = hotelService.myHotel(principal.getName());
        if (hotelDTO == null) {
            return "redirect:/adMain";
        }

        model.addAttribute("hotel_name", hotelDTO.getHotel_name());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);

        String create_by = "";
        ReserveSearchDTO reserveSearchDTO = new ReserveSearchDTO();

        if (searchType != null && !searchType.isEmpty() && searchKeyword != null && !searchKeyword.isEmpty()) {
            //검색 키워드 설정
            if ("room_name".equals(searchType)) {
                reserveSearchDTO.setRoom_name(searchKeyword);
            } else if ("create_by".equals(searchType)) {
                reserveSearchDTO.setReserve_name(searchKeyword);
            } else if ("keyword".equals(searchType)) {
                reserveSearchDTO.setRoom_name(searchKeyword);
                reserveSearchDTO.setReserve_name(searchKeyword);
            }
            log.info("reserveSearchDTO: " + reserveSearchDTO);

            PageResponseDTO<ReviewDTO> pageResponseDTO = reviewService.searchList(principal.getName(), pageRequestDTO, reserveSearchDTO, sortField, sortDir);
            model.addAttribute("pageResponseDTO", pageResponseDTO);
            model.addAttribute("pageRequestDTO", pageRequestDTO);
            model.addAttribute("searchType", searchType);
            model.addAttribute("searchKeyword", searchKeyword);
            model.addAttribute("isSearch", true);
        } else {
            // 일반 조회
            PageResponseDTO<ReviewDTO> pageResponseDTO = reviewService.getAdHotelReviewList(hotelDTO, pageRequestDTO, sortField, sortDir);

            if (pageResponseDTO == null || pageResponseDTO.getDtoList() == null) {
                pageResponseDTO.setDtoList(Collections.emptyList());
            }

            List<ReviewDTO> reviewDTOList = pageResponseDTO.getDtoList();
            for (ReviewDTO reviewDTO : reviewDTOList) {
                ReviewDTO latesReviewDTO = reviewService.reviewRead(reviewDTO.getRev_num());

                reviewDTO.setRoomDTO(latesReviewDTO.getRoomDTO());
                reviewDTO.setHotelDTO(latesReviewDTO.getHotelDTO());
            }

            model.addAttribute("pageResponseDTO", pageResponseDTO);
            model.addAttribute("pageRequestDTO", pageRequestDTO);
            model.addAttribute("isSearch", false);
        }
        return "review/adMyReviewList"; //리뷰 리스트 페이지 반환
    }

    //호텔 리뷰 목록
    @GetMapping("/reviewList/{hotel_num}")
    public String reviewList(@PathVariable("hotel_num") Long hotel_num, Model model,
                             @RequestParam(value = "query", required = false) String query,
                             @RequestParam(value = "sortField", defaultValue = "reg_date") String sortField,
                             @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir, Sort sort) {



        log.info("hotel_num: " + hotel_num);
        log.info("sortField: " + sortField);
        log.info("sortDir: " + sortDir);

        // 호텔 정보 가져오기 (별점 평균 , 리뷰 수 포함)
        HotelDTO hotelDTO = hotelService.read(hotel_num);
        List<ReviewDTO> reviewDTOList = reviewService.gethotelReviewList(hotel_num, sortField, sortDir);

        // 리뷰가 없는 경우, 빈 리스트를 모델에 전달
        if (reviewDTOList.isEmpty()) {
            model.addAttribute("message", "이 호텔에 대한 리뷰가 없습니다." );
        }

        // 모델에 데이터 추가
        model.addAttribute("hotelDTO", hotelDTO);
        model.addAttribute("reviewDTOList", reviewDTOList);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);

        reviewDTOList.forEach(reviewDTO -> log.info("reviewDTO: {}", reviewDTO));

        return "review/reviewList";
    }


    //마이 리뷰 목록(user권한)
    @GetMapping("/myReviewList")
    public String myReviewList(Principal principal, Model model) {
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

        boolean check = reviewService.validateReview(reserve_num, principal.getName());
        if (check) {
            return "redirect:/myReviewList";
        }

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
                                     @RequestParam("multipartFileList") List<MultipartFile> multipartFileList,
                                     Principal principal) throws Exception {

        System.out.println("받은 reserve_num: " + reserve_num);
        log.info("리뷰 등록 :{}", reviewDTO); // reviewDTO상태 확인
        log.info("principal: {}", principal);

        if (!roomReserveService.validateroomResereve(reserve_num, principal.getName())) {
            log.error("예약 회원의 이메일과 로그인 이메일이 일치하지 앟습니다.");
            return "redirect:/member/myRoomReserveRead"; //
        };

        if (multipartFileList != null && !multipartFileList.isEmpty()) {
            log.info("파일있음");
            multipartFileList.forEach(multipartFile -> log.info(multipartFile.getOriginalFilename()));
        } else {
            log.info("파일이 없음");
        }

        //리뷰 등록 처리
        try {
            // 리뷰 등록 서비스 호출(이미지 다중 업로드)
            reviewService.reviewRegister(reviewDTO, principal.getName(), multipartFileList);
            redirectAttributes.addFlashAttribute("msg", "등록이 완료 되었습니다.");
            log.info("호텔 : {}", hotel_num);
            return "redirect:/review/reviewList/" +hotel_num; // "문자열" + id = "redirect:/reviewList/" +hotel_num

        } catch (Exception e) {
            log.error("Error occurred while registering review: {}", e.getMessage());
            return "redirect:/review/register?error=true"; // 실패 시 다시 등록 페이지
        }

    }

    //유저 상세 보기 페이지
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


    //관리자 상세 보기 페이지
    @GetMapping("/adReviewRead")
    public String adReviewRead(@RequestParam(required = false) Long rev_num, Long hotel_num,
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
            return "review/adReviewRead";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("msg", "해당 리뷰를 찾을 수 없습니다..");
            return "redirect:review/adMyReviewList";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msg", "알 수 없는 오류가 발생 되었습니다.");
            return "redirect:review/adMyReviewList";
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
    public String reviewModifyPost(ReviewDTO reviewDTO,
                                   RedirectAttributes redirectAttributes, Long hotel_num,
                                   @RequestParam(value = "multipartFileList", required = false) List<MultipartFile> multipartFileList,
                                   @RequestParam(value = "delnumList", required = false) List<Long> delnumList){

        log.info("수정 요청 : {}" , reviewDTO);

        for (MultipartFile mutipartFile : multipartFileList) {
            log.info(mutipartFile.getOriginalFilename());
        }
        log.info("삭제할 값");
        log.info(delnumList);
        log.info(delnumList);

        if (multipartFileList != null && multipartFileList.stream().allMatch(MultipartFile::isEmpty)) {
            multipartFileList = null;
        }
        if (delnumList != null && delnumList.isEmpty()) {
            delnumList = null;
        }

        try {
            if (multipartFileList != null && multipartFileList.isEmpty()){
                log.info("업로드된 이미지 파일: " + multipartFileList.size());
            } else {
                log.info("이미지 파일이 없습니다.");
            }

            reviewService.reviewModify(reviewDTO, multipartFileList, delnumList);
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
