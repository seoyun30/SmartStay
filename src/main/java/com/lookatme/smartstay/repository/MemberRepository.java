package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    public Member findByEmail (String email);


    @Query("select m from Member m " + "where m.role = 'CHIEF' and m.brand.brand_num is null")
    public List<Member> selectBySuperAdmin();


    @Query("select m from Member  m " + "where (m.role = 'CHIEF' or m.role = 'MANAGER') and m.brand.brand_num = :brand_num")
    public List<Member> selectByChief(@Param("brand_num") Long brand_num);


    @Query("select m from Member m where m.name = :name and m.tel = :tel")
    Member findID(@Param("name") String name, @Param("tel") String tel);

    List<Member> email(String email);

    @Query("SELECT m FROM Member m " +
            "WHERE m.email LIKE CONCAT('%', :keyword, '%') " +
            "OR m.name LIKE CONCAT('%', :keyword, '%') " +
            "OR m.tel LIKE CONCAT('%', :keyword, '%') " +
            "OR m.role LIKE CONCAT('%', :keyword, '%')")
    Page<Member> searchMember(@Param("keyword") String keyword, Pageable pageable);

    @Query("select m from  Member  m")
    public Page<Member> selectAll(Pageable pageable);


}
