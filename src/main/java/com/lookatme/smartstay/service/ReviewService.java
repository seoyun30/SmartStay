package com.lookatme.smartstay.service;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

        // 리뷰(entity) 목록을 ReviewDTO 목록으로 변환
        List<ReviewDTO> reviewDTOS = reviews.stream()
                .map(review -> modelMapper.map(review, ReviewDTO.class))
                .collect(Collectors.toList());

        // 변환된 DTO 리스트 반환

//        List<Review> reviews = reviewRepository.findAll(); // 모두 조회
//
//        List<ReviewDTO> reviewDTOS = Arrays.asList(modelMapper.map(reviews, ReviewDTO[].class));
//
//        return reviewDTOS;
        return reviewDTOS;
    }

    //호텔 리뷰 목록(호텔 리뷰 페이지) //호텔정보
    public List<ReviewDTO> hotelReviewList(Long hotel_num) {

        // 호텔 정보 조회
        Hotel hotel = hotelRepository.findById(hotel_num)
                .orElseThrow(() -> new IllegalArgumentException("해당 호텔을 찾을 수 없습니다.")); //해당하는 호텔이 없으면 리뷰를 볼수 없음

        //해당 호텔에 대한 리뷰 조회
        List<Review> hotelReview = reviewRepository.findByHotel(hotel_num);  //해당 호텔 리뷰 조회

        //리뷰 리스트를 DTO로 변환
        List<ReviewDTO> reviewDTOS = hotelReview.stream()
                .map(review -> modelMapper.map(review, ReviewDTO.class))
                .collect(Collectors.toList());

        return reviewDTOS;


//        if (hotel.getHotel_num() != null) {
//            reviewHotel = reviewRepository.findByHotel(hotel.getHotel_num());  // 해당 호텔에 대한 리뷰 조회
//        }


//       List<Review> reviews = reviewRepository.findAll(); //모두 조회

    }


    //유저 리뷰 전체 목록 (유저 my 페이지)
    public List<ReviewDTO> getuserMyReviewList(String email) {

        List<Review> userMyReviews = reviewRepository.findByUser(email);

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

        //2. 작성자 정보확인
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new IllegalArgumentException("사용자 정보를 찾을 수 없습니다.");
        }

        //3. 리뷰 생성
        Review review = modelMapper.map(reviewDTO, Review.class);   //DTO -> entity로 변환
        review.setRoomReserve(roomReserve);  // 예약 정보를 세팅
        review.setMember(member);  // 리뷰 작성자로 세팅

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



    //리뷰 상세보기(일단 보류)
    public ReviewDTO reviewRead(Long rev_num) {

        Review review = reviewRepository.findById(rev_num)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 리뷰입니다."));

//        Optional<Review> review = reviewRepository.findById(rev_num); // findById가 Optional<>을 반환하여 필요없음

        ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);
        return reviewDTO;
    }


    //리뷰 수정(리뷰를 등록한 유저만 가능)
    public void reviewModify(ReviewDTO reviewDTO) {

        log.info("리뷰 수정 : {}", reviewDTO);

        //리뷰 조회 , 없으면 예외발생
        Review review = reviewRepository.findById(reviewDTO.getRev_num())
                .orElseThrow(() -> new EntityNotFoundException("해당 리뷰를 찾을 수 없습니다."));

        // 리뷰를 수정하려는 유저email이 리뷰를 작성한
        if (!review.getMember().getEmail().equals(review.getCreate_by())) {
            throw new SecurityException("리뷰를 수정할 권한이 없습니다.");
        }

//        ReviewDTO newReviewDTO = modelMapper.map(reviewDTO, ReviewDTO.class); //???

        //리뷰 업데이트
        review.setScore(reviewDTO.getScore());    //별점
        review.setContent(reviewDTO.getContent()); //리뷰 내용
        // 이미지

        review.setModi_date(LocalDateTime.now()); // 수정한 시간
        review.setModified_by(reviewDTO.getModified_by()); // 작성자 본인만 수정가능.
//        review.setCreate_by(reviewDTO.getCreate_by()); //

        reviewRepository.save(review);

//        Optional<Review> review = reviewRepository.findById(reviewDTO.getRev_num());  //findById 가 Optional<Review> 반환해서 필요없음
//
//        if (review.isPresent()) {
//            Review review1 = modelMapper.map(reviewDTO, Review.class);
//
//            reviewRepository.save(review1);
//        }
    }


    //리뷰 삭제(작성한 본인 리뷰만 가능) // 일단 기본 상태 보류 //  (데이터베이스에서 삭제되어 리스트에 보이는 사항은 신경 안써두 됨)
    public void reviewDelete(Long id) {
        log.info("서비스로 들어온 삭제할 번호 :" + id);
        reviewRepository.deleteById(id);
    }


    //페이지네이션




    //리뷰 별점(등록 = 유저만 가능 )


    //등록

    //변경



}
