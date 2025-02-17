package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Brand;
import com.lookatme.smartstay.entity.Member;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    public Member findByEmail (String email);


    @Query("select m from Member m " + "where m.role = 'CHIEF' and m.brand.brand_num is null")
    Page<Member> selectBySuperAdmin(Pageable pageable);

    @Query("select m from Member m " + "where m.role = 'CHIEF' and m.brand.brand_num is null " +
            "AND (m.email LIKE CONCAT('%', :keyword, '%') " +
            "OR m.name LIKE CONCAT('%', :keyword, '%') " +
            "OR m.tel LIKE CONCAT('%', :keyword, '%') " +
            "OR (m.role IS NOT NULL AND CAST(m.role AS string) LIKE CONCAT('%', :keyword, '%')))")
    Page<Member> searchSelectBySuperAdmin(@Param("keyword") String keyword, Pageable pageable);


    @Query("select m from Member m " + "where (m.role = 'CHIEF' or m.role = 'MANAGER') and m.brand.brand_num = :brand_num")
    Page<Member> selectByChief(@Param("brand_num") Long brand_num, Pageable pageable);


    @Query("SELECT m FROM Member m " +
            "JOIN FETCH m.brand b " +
            "LEFT JOIN FETCH m.hotel h " +
            "WHERE (m.role = 'CHIEF' OR m.role = 'MANAGER') " +
            "AND m.brand.brand_num = :brand_num " +
            "AND (" +
            "     m.email LIKE CONCAT('%', :keyword, '%') " +
            "  OR m.name LIKE CONCAT('%', :keyword, '%') " +
            "  OR m.tel LIKE CONCAT('%', :keyword, '%') " +
            "  OR (m.role IS NOT NULL AND CAST(m.role AS string) LIKE CONCAT('%', :keyword, '%')) " +
            "  OR b.brand_name LIKE CONCAT('%', :keyword, '%') " +
            "  OR h.hotel_name LIKE CONCAT('%', :keyword, '%')" +
            ")")
    Page<Member> searchSelectByChief(@Param("brand_num")Long brand_num, @Param("keyword") String keyword, Pageable pageable);


    @Query("select m from Member m where m.name = :name and m.tel = :tel")
    Member findID(@Param("name") String name, @Param("tel") String tel);

    List<Member> email(String email);

    @Query("SELECT m FROM Member m " +
            "WHERE m.email LIKE CONCAT('%', :keyword, '%') " +
            "OR m.name LIKE CONCAT('%', :keyword, '%') " +
            "OR m.tel LIKE CONCAT('%', :keyword, '%') " +
            "OR (m.role IS NOT NULL AND CAST(m.role AS string) LIKE CONCAT('%', :keyword, '%'))")
    Page<Member> searchMember(@Param("keyword") String keyword, Pageable pageable);

    @Query("select m from  Member  m")
    public Page<Member> selectAll(Pageable pageable);

    //호텔 지우기 위한 쿼리문
    @Modifying
    @Transactional
    @Query("delete from Member m where m.hotel.hotel_num = :hotel_num")
    void deleteByHotelHotel_num(Long hotel_num);


    boolean existsByTel(String tel);
}
