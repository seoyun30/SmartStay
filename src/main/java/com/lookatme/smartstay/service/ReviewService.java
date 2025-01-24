package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.ReviewDTO;
import com.lookatme.smartstay.entity.Review;
import com.lookatme.smartstay.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;

    //관리자 리뷰 전체 목록 (본점, 지점)
    public List<ReviewDTO> adMyReviewList() {
        List<Review> reviews = reviewRepository.findAll(); // 모두 조회

        List<ReviewDTO> reviewDTOS = Arrays.asList(modelMapper.map(reviews, ReviewDTO[].class));

        return reviewDTOS;
    }

    //해당 호텔 리뷰 목록
    public List<ReviewDTO> hotelReviewList() {

        return null;
    }


    //유저 리뷰 전체 목록 (my 페이지)
    public List<ReviewDTO> myReviewList() {

        return null;
    }



    //리뷰 등록(룸 예약을 한 유저만 등록 가능)
    public void reviewRegister(ReviewDTO reviewDTO) {
        Review review = modelMapper.map(reviewDTO, Review.class);

        reviewRepository.save(review);
    }



    //리뷰 상세보기
    public ReviewDTO reviewRead(Long rev_num) {
        Optional<Review> review = reviewRepository.findById(rev_num);

        ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);
        return reviewDTO;
    }


    //리뷰 수정(User)
    public void reviewModify(ReviewDTO reviewDTO) {

        Optional<Review> review = reviewRepository.findById(reviewDTO.getRev_num());

        if (review.isPresent()) {
            Review review1 = modelMapper.map(reviewDTO, Review.class);

            reviewRepository.save(review1);
        }
    }


    //리뷰 삭제(User)
    public void reviewDelete(Long rev_num) {
        reviewRepository.deleteById(rev_num);

    }



    //검색

    //
    //리뷰 별점(User)

}
