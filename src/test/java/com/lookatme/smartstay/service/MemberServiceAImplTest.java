package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.PageRequestDTO;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Log4j2
@Transactional
class MemberServiceAImplTest {

    @Autowired
    private MemberService memberService;

    @Test
    public void testMemberService() {

        PageRequestDTO pageRequestDTO = new PageRequestDTO();
        memberService.memberList(pageRequestDTO);

    }

}