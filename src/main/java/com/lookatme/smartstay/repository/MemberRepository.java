package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.constant.Power;
import com.lookatme.smartstay.constant.Role;
import com.lookatme.smartstay.entity.Member;
import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.ParameterScriptAssert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    public Member findByEmail (String email);
    void deleteByEmail (String email);

    @Query("select m  from Member m " +
            "where m.role = 'CHIEF' and m.power = 'NO' and m.brand.brand_num is null")
    public List<Member> selectByCPB();

    @Query("select m from Member m " + "where m.role = 'CHIEF' and m.brand.brand_num is null")
    public List<Member> selectBySuperAdmin();


    @Query("select m from Member m " + "where (m.role = 'CHIEF' or m.role = 'MANAGER') and m.brand.brand_num = :brand_num")
    public List<Member> selectByChief(@Param("brand_num") Long brand_num);

    public List<Member> findByRoleAndPowerAndBrandIsNull(Role role, Power power);


    List<Member> email(@Email String email);
}
