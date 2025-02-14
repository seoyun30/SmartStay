package com.lookatme.smartstay.service;

import com.lookatme.smartstay.constant.Role;
import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.dto.PageResponseDTO;
import com.lookatme.smartstay.dto.ReviewDTO;
import com.lookatme.smartstay.dto.RoomReserveDTO;
import com.lookatme.smartstay.entity.*;
import com.lookatme.smartstay.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final HotelRepository hotelRepository;
    //이미지 사용 시 필요
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final RoomReserveRepository roomReserveRepository; // 호텔예약정보 필요


    // 관리자 , 유저 , 호텔에서 보이는 리뷰가 다르기에 3개로 나누어 서비스 작성
    //관리자 리뷰 전체 목록 (관리자 리뷰 페이지 치프, 매니저)
    public List<ReviewDTO> adMyReviewList(Long hotel_num, String email) {

//        // 로그인한 사용자 정보 확인 (예: Spring Security 사용) // 사용가능한지 확인 필요
//        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
//         Member currentUser = memberRepository.findById(member_num)
//                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        // 호텔 정보 조회
        Hotel hotel = hotelRepository.findById(hotel_num)
                .orElseThrow(() -> new IllegalArgumentException("해당 호텔을 찾을 수 없습니다."));
        //브랜드 관리자 또는 호텔 관리자 권한 확인

        Member member = memberRepository.findByEmail(email); //회원정보 확인
        if (member == null) {
            throw new IllegalArgumentException("해당 이메일의 회원 정보를 찾을 수 없습니다.");
        }

        List<Review> reviews;

        // if문
        if (member.getRole().name().equals("CHIEF") ) {
            // 관리자(브랜드): 브랜드에 속한 모든 호텔 리뷰 조회
            reviews = reviewRepository.findByHotelorBrand(member.getBrand().getBrand_num()); // 해당 브랜드에 속한 모든 호텔의 리뷰 조회
        } else if (member.getRole().name().equals("MANAGER")) {
            // 매니저(호텔): 해당 호텔에 대한 리뷰만 조회
            reviews = reviewRepository.findByHotel(member.getHotel().getHotel_num()); //해당 호텔에 대한 리뷰 조회
        } else {
            throw new AccessDeniedException("리뷰를 조회할 권한이 없습니다.");
        }

        // 리뷰가 없는 경우 빈 리스트 반환
        if (reviews.isEmpty()) {
            return Collections.emptyList();
        }

        // 리뷰(entity) 목록을 ReviewDTO 목록으로 변환
        List<ReviewDTO> reviewDTOS = reviews.stream()
                .map(review -> modelMapper.map(review, ReviewDTO.class))
                .collect(Collectors.toList());

        return reviewDTOS;
    }

    //호텔 리뷰 목록(호텔 리뷰 페이지) //호텔정보
    public List<ReviewDTO> hotelReviewList(Long hotel_num) {

        // 호텔 정보 조회
        Hotel hotel = hotelRepository.findById(hotel_num)
                .orElseThrow(() -> new IllegalArgumentException("해당 호텔을 찾을 수 없습니다.")); //해당하는 호텔이 없으면 리뷰를 볼수 없음

        //해당 호텔에 대한 리뷰 조회
        List<Review> hotelReview = reviewRepository.findByHotel(hotel_num);  //해당 호텔 리뷰 조회

        // 리뷰가 없는 경우 빈 리스트 반환
        if (hotelReview.isEmpty()) {
            return Collections.emptyList();
        }

        //리뷰 리스트를 DTO로 변환
        List<ReviewDTO> reviewDTOS = hotelReview.stream()
                .map(review -> modelMapper.map(review, ReviewDTO.class))
                .collect(Collectors.toList());

        return reviewDTOS;
    }


    //유저 리뷰 전체 목록 (유저 my 페이지)
    public List<ReviewDTO> getuserMyReviewList(String email) {

        //1. 유저 정보 확인
        Member member = memberRepository.findByEmail(email); //회원정보 확인
        if (member ==null) {
            throw new IllegalArgumentException("해당 이메일의 회원 정보를 찾을 수 없습니다..");
        }

        // 권한 확인: 유저만 가능
        List<Review> userMyReviews;

        if (member.getRole() == Role.USER) {
            userMyReviews = reviewRepository.findByUser(email);
            // userMyReviews = reviewRepository.findByMember(member);
        } else {
            throw new AccessDeniedException("리뷰를 조회할 권한이 없습니다.");
        }

        //이전 거
//        if (member.getRole().name().equals("USER")) {
//            throw new AccessDeniedException("리뷰를 조회할 권한이 없습니다.");
//        }

//        List<Review> userMyReviews = reviewRepository.findByMember(member); // member 객체로 조회하는것이 정확하다고 함..
//        List<Review> userMyReviews = reviewRepository.findByUser(email);

        // 유저 리뷰목록을 DTO로 변환
        List<ReviewDTO> reviewDTOS = userMyReviews.stream()
                .map(review -> modelMapper.map(review, ReviewDTO.class))
                .collect(Collectors.toList());

        return reviewDTOS;
    }


    //리뷰 등록<작성>(룸 예약을 한 유저만 등록 가능)
    public void reviewRegister(ReviewDTO reviewDTO, String email, Long hotel_num, Long reserve_num, List<MultipartFile> multipartFiles) throws Exception {

        //1. 예약정보 확인
        RoomReserve roomReserve = roomReserveRepository.findById(reserve_num)
                .orElseThrow(() -> new IllegalArgumentException("룸 예약정보" + reserve_num + "에 대한 정보를 찾을 수 없습니다."));

//        // 이메일이 없을 경우 방어 로직
//        if (email == null || email.trim().isEmpty()) {
//            throw new IllegalArgumentException("이메일이 유효하지 않습니다.");
//        }
        //2. 작성자 정보확인
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new IllegalArgumentException("이메일에 해당하는 작성자 정보를 찾을 수 없습니다.");
        }

        //3. 예약자와 작성자 일치 여부 확인( 만약 룸 예약자가 일치하지 않더라도 리뷰 작성이 가능하면 필요없음)
        if (!roomReserve.getMember().equals(member)) { // 룸 예약을한 회원이 리뷰를 작성하려는 회원과 다른경우
            throw new SecurityException("룸 예약을 한 회원과 리뷰 작성자가 일치하지 않습니다.");
        }

        //3. 리뷰 생성 및 정보 세팅
        Review review = modelMapper.map(reviewDTO, Review.class);   //DTO -> entity로 변환
        review.setRoomReserve(roomReserve);  // 예약 정보를 세팅
        review.setMember(member);  // Member 객체 설정
        review.setCreate_by(member.getEmail()); // 작성자 설정(로그인한 사용자의 이메일로 설정)
        review.setReg_date(LocalDateTime.now()); // 작성일을 현재 시간으로 설정

        //4. 별점 유효성 체크(1~5)
        int score = Integer.parseInt(reviewDTO.getScore());
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("별점은 1~5점 사이여야 합니다.");
        }

        // review 저장
        reviewRepository.save(review);

        // 이미지가 없다면 저장
        if (multipartFiles != null && multipartFiles.isEmpty()) {
            imageService.saveImage(multipartFiles, "review", review.getRev_num());
        }
    }


    //리뷰 상세보기(보이는 리뷰가 있으면 모두 가능)
    public ReviewDTO reviewRead(Long rev_num) {

        Review review = reviewRepository.findById(rev_num)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 리뷰입니다."));  //작성회원이 리뷰를 삭제했을 시

//        Optional<Review> review = reviewRepository.findById(rev_num); // findById가 Optional<>을 반환하여 필요없음

        ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);
        return reviewDTO;
    }


    //리뷰 수정(리뷰를 등록한 유저만 가능)
    public void reviewModify(ReviewDTO reviewDTO, String email) {

        log.info(" 수정 :{}", reviewDTO);

        //리뷰 조회 , 없으면 예외발생
        Review review = reviewRepository.findById(reviewDTO.getRev_num())
                .orElseThrow(() -> new EntityNotFoundException("해당 리뷰를 찾을 수 없습니다."));

        //수정자 정보확인
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new IllegalArgumentException("이메일에 해당하는 작성자 정보를 찾을 수 없습니다.");
        }
        if (review.getMember() == null || review.getCreate_by() == null) {
            throw new IllegalArgumentException("리뷰 작성자 정보가 올바르지 않습니다.");
        }

        //권한 확인: 리뷰작성자와 수정하려는 회원이 일치하는지
        if (!review.getMember().getEmail().equals(review.getCreate_by())) {
            throw new SecurityException("리뷰를 수정할 권한이 없습니다.");
        }

        // 이미지 관련(추가 예정)

        //리뷰 업데이트
        review.setRev_num(reviewDTO.getRev_num()); // 리뷰 번호 등록 그대로 사용
        review.setScore(reviewDTO.getScore());    //별점
        review.setContent(reviewDTO.getContent()); //리뷰 내용
                                                    // 이미지


        review.setModi_date(LocalDateTime.now()); // 지금 시간으로 수정한 시간 설정
        review.setModified_by(member.getEmail()); //// 수정자 정보 설정(로그인한 사용자의 이메일로 설정)
//        review.setCreate_by(reviewDTO.getCreate_by()); // 작성자 정보가 변경 불가로 주석 처리



        reviewRepository.save(review);

        //기존에 있던 ..
//        Optional<Review> review = reviewRepository.findById(reviewDTO.getRev_num());  //findById 가 Optional<Review> 반환해서 필요없음
//
//        if (review.isPresent()) {
//            Review review1 = modelMapper.map(reviewDTO, Review.class);
//
//            reviewRepository.save(review1);
//        }
    }


    //리뷰 삭제(작성한 본인 리뷰만 가능) // 일단 기본 상태 보류 //  (데이터베이스에서 삭제되어 리스트에 보이는 사항은 신경 안써두 됨)
    public void reviewDelete(Long id, String email) {
        log.info("서비스로 들어온 삭제할 번호 :" + id);

        // 리뷰를 작성한 사용자만 삭제 가능으로 추가
        // 1. 리뷰 조회
        Review review = reviewRepository.findById(id)
                        .orElseThrow(()-> new EntityNotFoundException("해당 리뷰를 찾을 수 없습니다."));

        //2. 권한 확인 : 작성자와 삭제 요청자를 이메일로 비교
        if (!review.getMember().getEmail().equals(email)) {
            throw new SecurityException("리뷰를 삭제할 권한이 없습니다.");
        }

        reviewRepository.deleteById(id);
    }


    //페이지네이션
    public PageResponseDTO<ReviewDTO> reviewList(PageRequestDTO pageRequestDTO) {


        return null;
    }







}
