package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query("SELECT i FROM Image i WHERE " +
            "(:targetType = 'brand' AND i.brand.brand_num = :targetId) OR " +
            "(:targetType = 'hotel' AND i.hotel.hotel_num = :targetId) OR " +
            "(:targetType = 'room' AND i.room.room_num = :targetId) OR " +
            "(:targetType = 'menu' AND i.menu.menu_num = :targetId) OR " +
            "(:targetType = 'care' AND i.care.care_num = :targetId) OR " +
            "(:targetType = 'review' AND i.review.rev_num = :targetId) OR " +
            "(:targetType = 'qna' AND i.qna.qna_num = :targetId) OR " +
            "(:targetType = 'notice' AND i.notice.notice_num = :targetId)")
    List<Image> findByTarget(@Param("targetType") String targetType, @Param("targetId") Long targetId);

}
