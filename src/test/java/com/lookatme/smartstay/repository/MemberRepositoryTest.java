package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.constant.Role;
import com.lookatme.smartstay.entity.Hotel;
import com.lookatme.smartstay.entity.Member;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Log4j2
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    HotelRepository hotelRepository;

    @Test
    public void testMemberRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Member> memberList =
        memberRepository.findAll(pageable).getContent();
        memberList.forEach(member -> log.info(member));
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void testFindById() {
        Long member_num =2L;
//        int selectnum = 1;
        String role = "CHIEF";
        Long hotel_num =1L;

        Member member = null;
        try {
            member =
            memberRepository.findById(member_num).orElseThrow(EntityNotFoundException::new);

        }catch (EntityNotFoundException e) {
            log.info("디비에 값없음");

//        }
//        if(selectnum == 1 && member != null){
//            //치프로 변경
//            member.setRole(Role.CHIEF);
//
//        }else {
//            //2로 들어면 매니저로 변경
//            member.setRole(Role.MANAGER);
//
//
        }

        if(role == "CHIEF"){
            //치프로 변경
            member.setRole(Role.CHIEF);
            member.setHotel(null);

        }else {
            //매니저로 변경
            member.setRole(Role.MANAGER);

            Hotel hotel = hotelRepository.findById(hotel_num).orElseThrow(EntityNotFoundException::new);
            member.setHotel(hotel);


        }



    }
}