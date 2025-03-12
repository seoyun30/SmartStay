package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Brand;
import com.lookatme.smartstay.entity.Hotel;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Query("select h from Hotel h where h.create_by = :email")
    List<Hotel> findByEmail(String email);

    List<Hotel> findAll(Sort sort);

    @Query("select h from Hotel h where h.hotel_name like %:query% or h.address like %:query%")
    List<Hotel> findByHotel_nameIgnoreCaseOrAddressContainingIgnoreCase(@Param("query") String query);

    //브랜드는 남겨두고 호텔을 삭제하기 위한 메서드
    @Modifying
    @Transactional
    @Query("update Hotel h set h.brand = null where h.hotel_num = :hotel_num")
    void updateHotelBrandToNull(@Param("hotel_num") Long hotel_num);

    //브랜드에 속한 호텔 목록 조회
    @Query("select h from Hotel h where h.brand= :brand")
    List<Hotel> findByMyBrand(Brand brand);

    //호텔 활성화(관리자 전체 보여짐)
    @Query("SELECT h FROM Hotel h WHERE h.brand.active_state = 'ACTIVE' and h.active_state = 'ACTIVE'")
    List<Hotel> findActiveHotel();

    //호텔 페이징 및 검색
    @Query("select h from Hotel h where h.brand.brand_num = :brand_num")
    Page<Hotel> hotelPage(Long brand_num, Pageable pageable);

    //호텔명 검색 hotel_num
    @Query("select h from Hotel h where h.brand.brand_num = :brand_num and h.hotel_name like concat('%', :keyword, '%') ")
    public Page<Hotel> findByB_numAndHotel_name(Long brand_num, String keyword, Pageable pageable);

    //대표자 이름 검색 owner
    @Query("select h from Hotel h where h.brand.brand_num = :brand_num and h.owner like concat('%', :keyword, '%') ")
    public Page<Hotel> findByB_numAndOwner(Long brand_num, String keyword, Pageable pageable);

    //호텔 주소 address
    @Query("select h from Hotel h where h.brand.brand_num = :brand_num and h.address like concat('%', :keyword, '%') ")
    public Page<Hotel> findByB_numAndAddress(Long brand_num, String keyword, Pageable pageable);

    //활성상태 active_state
    @Query("select h from Hotel h where h.brand.brand_num = :brand_num " +
            "and h.active_state = 'ACTIVE' "
            + "and h.hotel_name like concat('%', :keyword, '%') ")
    public Page<Hotel> findByB_numAndActive_stateContaining(Long brand_num, String keyword, Pageable pageable);

    //전체
    @Query("select h from Hotel h where h.brand.brand_num = :brand_num and h.hotel_name like concat('%', :str, '%') or h.owner like concat('%', :str, '%') "
            + "or h.address like concat('%', :str, '%') or h.tel like concat('%', :str, '%') or h.business_num like concat('%', :str, '%') " )
    public Page<Hotel> findByAllSearch(Long brand_num, String str, Pageable pageable);


}
