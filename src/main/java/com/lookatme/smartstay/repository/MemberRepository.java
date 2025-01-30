package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.constant.Power;
import com.lookatme.smartstay.constant.Role;
import com.lookatme.smartstay.entity.Member;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    public Member findByEmail (String email);
    void deleteByEmail (String email);

    @Query("select m  from Member m " +
            "where m.role = 'CHIEF' and m.power = 'NO'  and m.brand.brand_num is null")
    public List<Member> selectByCPB();


    public List<Member> findByRoleAndPowerAndBrandIsNull(Role role, Power power);

}
