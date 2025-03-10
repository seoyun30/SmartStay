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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
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
    private final FileService fileService;

    @Value("${imgUploadLocation}")
    private String imgUploadLocation;

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
        // 이미지
        for (ReviewDTO reviewDTO : reviewDTOList) {
            List<Image> reviewImageList = imageRepository.findByTarget("review", reviewDTO.getRev_num());
            if (!reviewImageList.isEmpty()) {
                List<ImageDTO> reviewImageDTOList = reviewImageList.stream()
                        .map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList());
                reviewDTO.setImageDTOList(reviewImageDTOList);
            } else {
                reviewDTO.setImageDTOList(null);
            }
        }

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
        for (ReviewDTO reviewDTO : reviewDTOList) {
            List<Image> reviewImageList = imageRepository.findByTarget("review", reviewDTO.getRev_num());
            if (!reviewImageList.isEmpty()) {
                List<ImageDTO> reviewImageDTOList = reviewImageList.stream()
                        .map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList());
                reviewDTO.setImageDTOList(reviewImageDTOList);
            } else {
                reviewDTO.setImageDTOList(null);
            }
        }

        return reviewDTOList;
    }


    //리뷰 등록<작성>(룸 예약을 한 유저만 등록 가능)
    public void reviewRegister(ReviewDTO reviewDTO, String email, List<MultipartFile> multipartFiles, Long mainImageIndex) throws Exception {
        log.info("리뷰 등록 요청 :{}" , reviewDTO);
        log.info("ReviewDTO: {}", reviewDTO);
        multipartFiles.forEach(multipartFile -> log.info("multipartFile: {}", multipartFile));

        //4. 별점 유효성 체크(0.1~ 5) / 따로 validateScore private로 빼야하는지 확인중
        String scoreStr = reviewDTO.getScore();
        if (scoreStr == null || scoreStr.trim().isEmpty()) {
            throw new IllegalArgumentException("별점은 필수 입력값입니다.");
        } else {
            scoreStr = scoreStr.replaceAll(",", "");
            reviewDTO.setScore(scoreStr);
        }

        if (!scoreStr.matches("^\\d+(\\.\\d+)?$")) {
            throw new IllegalArgumentException("별점은 숫자 형식이어야 합니다.");
        }

        //별점을 숫자로 변환
        log.info("scoreStr: {}", scoreStr);
        double score;
        try {
            score = Double.parseDouble(scoreStr.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("별점 변환 중 오류가 발생했습니다.");
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
        review = reviewRepository.save(review);

        // 저장된 리뷰의 `rev_num`을 target_id로 사용하여 이미지 저장
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            // // imageService의 saveImage 메소드로 다중 파일 처리
            imageService.saveImage(multipartFiles, "review", review.getRev_num());
        }
    }

    //리뷰 상세보기(보이는 리뷰가 있으면 모두 가능)
    public ReviewDTO reviewRead(Long rev_num) {

        Review review = reviewRepository.findById(rev_num)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 리뷰가 존재하지 않습니다." + rev_num ));  //작성회원이 리뷰를 삭제했을 시

        ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);

        //호텔 정보연결용
        Hotel hotel = review.getHotel();
        if (hotel != null) { //호텔이 null이 아니라면 호텔 정보 set
            HotelDTO hotelDTO = modelMapper.map(hotel, HotelDTO.class);
            reviewDTO.setHotelDTO(hotelDTO);
        }
        Room room = review.getRoom();
        if (room != null) {
            RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);
            reviewDTO.setRoomDTO(roomDTO);
        }

        //이미지
        List<Image> imageList = imageRepository.findByTarget("review", rev_num);

        if (imageList != null && !imageList.isEmpty()) {
            List<ImageDTO> imageDTOList = imageList.stream()
                    .map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList());

            List<Long> imageIdList = imageDTOList.stream()
                    .map(ImageDTO::getImage_id).collect(Collectors.toList());

            reviewDTO.setImageDTOList(imageDTOList);
            reviewDTO.setImageIdList(imageIdList);

        }

        return reviewDTO;
    }


    //리뷰 수정(리뷰를 등록한 유저만 가능)
    public void reviewModify(ReviewDTO reviewDTO, List<MultipartFile> multipartFiles, List<Long> delnumList) throws Exception {

        log.info(" 리뷰 수정 요청 : {} " , reviewDTO);

//        // 별점 유효성 체크
//        String scoreStr = reviewDTO.getScore(); // 클라이언트에서 받은 별점
//        if (scoreStr == null || scoreStr.trim().isEmpty()) {
//            throw new IllegalArgumentException("별점은 필수 입력값입니다.");
//        }
//
//        double score;
//        try {
//            score = Double.parseDouble(scoreStr); // 별점을 숫자로 변환
//        } catch (NumberFormatException e) {
//            throw new IllegalArgumentException("별점은 숫자여야 합니다.");
//        }
//
//        // 별점 유효성 체크
//        if (score < 1.0 || score > 5.0 || score * 10 % 5 != 0) {
//            throw new IllegalArgumentException("별점은 1~5점 사이여야 하고, 0.5 단위 입력도 가능합니다.");
//        }

        //리뷰 조회 , 없으면 예외발생
        Review review = reviewRepository.findById(reviewDTO.getRev_num()).orElseThrow(EntityNotFoundException::new);


        //리뷰 업데이트
        review.setScore(reviewDTO.getScore());    //별점
        review.setContent(reviewDTO.getContent()); //리뷰 내용
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
    @Transactional
    public void reviewDelete(Long id) {
        log.info("서비스로 들어온 삭제할 번호 :" + id);

        List<Image> images = imageRepository.findByTarget("review", id);

        if (images == null && images.isEmpty()) {
            log.error("삭제하려는 이미지가 없습니다. rev_num : " + id);
        } else {
            for (Image image : images) {
                try {
                    imageService.deleteImage(image.getImage_id());
                    log.info("삭제된 이미지 id : " + image.getImage_id());
                } catch (Exception e) {
                    log.error("이미지 삭제 실패 : " + e.getMessage());
                }
            }
        }

        // 1. 리뷰 조회


        reviewRepository.deleteById(id);
        log.info("리뷰 삭제 완료 rev_num : " + id);
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
