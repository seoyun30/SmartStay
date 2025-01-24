package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Member;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    public Member findByEmail (String email);
    void deleteByEmail (String email);

}
