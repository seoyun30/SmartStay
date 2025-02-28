package com.lookatme.smartstay.service;

import com.lookatme.smartstay.constant.Role;
import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.entity.*;
import com.lookatme.smartstay.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;

    //권한 설정 시 필요
    private final MemberRepository memberRepository;
    private final HotelRepository hotelRepository;

    //이미지 사용 시 필요
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final RoomReserveRepository roomReserveRepository; // 호텔예약정보 필요
    private final RoomReserveItemRepository roomReserveItemRepository;

    //리뷰의 전체 조회
    //관리자 리뷰 전체 목록 (관리자 리뷰 페이지 치프, 매니저)
    public List<ReviewDTO> getBrandReviewList (Long brand_num, String email) {

        List<Review> brandReviews = reviewRepository.findByHotelorBrand(brand_num);

        // 리뷰가 없는 경우 빈 리스트 반환
        if (brandReviews.isEmpty()) {
            return Collections.emptyList();
        }

        // 리뷰(entity) 목록을 ReviewDTO 목록으로 변환
        List<ReviewDTO> reviewDTOList = brandReviews.stream()
                .map(review -> modelMapper.map(review, ReviewDTO.class)
                        .setRoomDTO(modelMapper.map(review.getRoom(), RoomDTO.class)
                                .setHotelDTO(modelMapper.map(review.getHotel(), HotelDTO.class)))
                )
                .collect(Collectors.toList());

        return reviewDTOList;
    }

    //호텔에 있는 리뷰 전체 조회
    public List<ReviewDTO> gethotelReviewList(Long hotel_num) {

        List<Review> hotelreviews = reviewRepository.findByHotel(hotel_num);

        // 리뷰가 없는 경우 빈 리스트 반환
        if (hotelreviews.isEmpty()) {
            return Collections.emptyList();
        }

        List<ReviewDTO> reviewDTOList = hotelreviews.stream()
                        .map(review -> modelMapper.map(review, ReviewDTO.class)
                                .setRoomDTO(modelMapper.map(review.getRoom(), RoomDTO.class)
                                        .setHotelDTO(modelMapper.map(review.getHotel(), HotelDTO.class)))
                        )
                .collect(Collectors.toList());

        return reviewDTOList;
    }


    //유저 리뷰 전체 목록 (유저 my 페이지)
    public List<ReviewDTO> userMyReviewList(String email) {

        List<Review> MyReviews = reviewRepository.findByUser(email);
        if (MyReviews.isEmpty()) {
            return Collections.emptyList();
        }

        List<ReviewDTO> reviewDTOList = MyReviews.stream()
                .map(review -> modelMapper.map(review, ReviewDTO.class)
                        .setRoomDTO(modelMapper.map(review.getRoom(), RoomDTO.class)
                                .setHotelDTO(modelMapper.map(review.getHotel(), HotelDTO.class)))
                )
                .collect(Collectors.toList());
        // 이미지

        return reviewDTOList;
    }


    //리뷰 등록<작성>(룸 예약을 한 유저만 등록 가능)
    public void reviewRegister(ReviewDTO reviewDTO, String email, List<MultipartFile> multipartFiles) throws Exception {
        log.info("리뷰 등록 요청 :{}" , reviewDTO);
        log.info("ReviewDTO: {}", reviewDTO);

        //4. 별점 유효성 체크(0.1~ 5) / 따로 validateScore private로 빼야하는지 확인중
        String scoreStr = reviewDTO.getScore();
        if (scoreStr == null || scoreStr.trim().isEmpty()) {
            throw new IllegalArgumentException("별점은 필수 입력값입니다.");
        }
        //별점을 숫자로 변환
        double score;
        try {
            score = Double.parseDouble(scoreStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("별점은 숫자여야 합니다.");
        }
        // 1.0~ 5.0 사이의 값인지 체크
        if (score < 1.0 || score > 5.0 || score * 10 % 5 != 0) {
            throw new IllegalArgumentException("별점은 1~5점 사이여야 하고, 0.5 단위 입력도 가능합니다.");
        }

        //조인 사용방법
        // 리뷰 객체 생성 및 데이터 설정
        Review review = modelMapper.map(reviewDTO, Review.class);
        //룸 예약 찾기
        RoomReserveItem roomReserveItem = roomReserveItemRepository.findById(reviewDTO.getRoomreserveitem_num())
                        .orElseThrow(EntityNotFoundException::new);

        review.setRoomReserve(roomReserveItem.getRoomReserve());  // 예약 정보를 세팅
        review.setHotel(roomReserveItem.getRoom().getHotel());  //호텔 정보 설정
        review.setRoom(roomReserveItem.getRoom()); //룸 정보 설정
        review.setMember(roomReserveItem.getRoomReserve().getMember());  // 회원 정보 설정

        //먼저 리뷰 저장
        reviewRepository.save(review);

        // 저장된 리뷰의 `rev_num`을 target_id로 사용하여 이미지 저장
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

        //호텔 정보연결용
        Hotel hotel = review.getHotel();
        if (hotel != null) { //호텔이 null이 아니라면 호텔 정보 set
            HotelDTO hotelDTO = modelMapper.map(hotel, HotelDTO.class);

            reviewDTO.setHotelDTO(hotelDTO);
        }

        List<Image> imageList = imageRepository.findByTarget("review", rev_num);

        if (imageList != null && !imageList.isEmpty()) {
            List<ImageDTO> imageDTOList = imageList.stream()
                    .map(image -> modelMapper.map(image, ImageDTO.class))
                    .collect(Collectors.toList());

            List<Long> imageIdList = imageDTOList.stream()
                    .map(ImageDTO::getImage_id)
                    .collect(Collectors.toList());

            reviewDTO.setImageDTOList(imageDTOList);
            reviewDTO.setImageIdList(imageIdList);

        }

        return reviewDTO;
    }

//    private Double ratingAvg(Long review_num) {
//
//    }


    //리뷰 수정(리뷰를 등록한 유저만 가능)
    public void reviewModify(ReviewDTO reviewDTO, String email, List<MultipartFile> multipartFiles, List<Long> delnumList) {

        log.info(" 가져온 리뷰 :  " + reviewDTO);

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
        if (!review.getMember().getEmail().equals(email)) {
            throw new SecurityException("리뷰를 수정할 권한이 없습니다.");
        }

        //리뷰 업데이트
//        review.setRev_num(reviewDTO.getRev_num()); // 리뷰 번호 등록 그대로 사용
        review.setScore(reviewDTO.getScore());    //별점
        review.setContent(reviewDTO.getContent()); //리뷰 내용
        review.setModi_date(LocalDateTime.now()); // 지금 시간으로 수정한 시간 설정
        review.setModified_by(member.getEmail()); //// 수정자 정보 설정(로그인한 사용자의 이메일로 설정)
//        review.setCreate_by(reviewDTO.getCreate_by()); // 작성자 정보가 변경 불가로 주석 처리

        reviewRepository.save(review);

        boolean hasNewImages = multipartFiles != null && multipartFiles.stream().anyMatch(file -> !file.isEmpty());
        boolean hasDeletedImages = delnumList != null && delnumList.isEmpty();

        if (hasNewImages || hasDeletedImages) {
            log.info("이미지 업데이트");
            try {
                imageService.updateImage(
                        hasNewImages ? multipartFiles : null,
                        hasDeletedImages ? delnumList : null,
                        "review",
                        review.getRev_num()
                );
            } catch (IndexOutOfBoundsException e) {
                log.error("이미지 업데이트 중 인덱스 오류 발생: {}", e.getMessage());
                throw new IllegalArgumentException("업로드된 파일이나 삭제 요청이 잘못되었습니다.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            log.info("이미지 업데이트 없이 수정");
        }
    }


    //리뷰 삭제(작성한 본인 리뷰만 가능)
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


    //페이지네이션 (리스트가 3개여서 나눠서 작성)
    //관리자 리뷰 페이지네이션
    public PageResponseDTO<ReviewDTO> adMyReviewList(PageRequestDTO pageRequestDTO) {



        return null;
    }




    public List<ReviewDTO> getLimitedReviews (Long hotel_num, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Review> reviews = reviewRepository.findTopNByHotelNum(hotel_num, pageable);

        return reviews.stream().map(review -> modelMapper.map(review, ReviewDTO.class)).collect(Collectors.toList());
    }

    public List<ReviewDTO> getLimitedRoomReviews (Long room_num, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Review> reviews = reviewRepository.findTopNByRoomNum(room_num, pageable);

        return reviews.stream().map(review -> modelMapper.map(review, ReviewDTO.class)).collect(Collectors.toList());
    }

    public int getReviewCountByHotel (Long hotel_num) {
        return reviewRepository.countByHotelNum(hotel_num);
    }

}
