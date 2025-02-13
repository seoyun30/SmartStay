package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Member;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Log4j2
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testMemberRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Member> memberList =
        memberRepository.findAll(pageable).getContent();
        memberList.forEach(member -> log.info(member));
    }
}