package com.lookatme.smartstay.service;

import com.lookatme.smartstay.constant.Accept;
import com.lookatme.smartstay.constant.Role;
import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.repository.MemberRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member =
                this.memberRepository.findByEmail(email);

        if(member == null) {
            throw new UsernameNotFoundException("가입된 회원이 아닙니다.");

        }
        String role = "";
        if("SUPERADMIN".equals(member.getRole().name())){
            log.info("슈퍼어드민");
            role = Role.SUPERADMIN.name();
        }else if("CHIEF".equals(member.getRole().name()) && member.getAccept() == Accept.Y){
            log.info("치프");
            role = Role.CHIEF.name();
        }else if("MANAGER".equals(member.getRole().name()) && member.getAccept() == Accept.Y){
            log.info("매니져");
            role = Role.MANAGER.name();
        }else {
            log.info("일반유저");
            role = Role.USER.name();
        }

        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(role)
                .build();
    }

    public Member saveMember(MemberDTO memberDTO){

        validateDuplicateMember(memberDTO.getEmail());

        Member member =
                MemberDTO.dtoEntity(memberDTO);
        member.setRole(Role.USER);

        log.info(member);
        member =
                memberRepository.save(member);

        return member;
    }

    public Member saveSuperAdminMember(MemberDTO memberDTO){

        validateDuplicateMember(memberDTO.getEmail());

        Member member =
                MemberDTO.dtoEntity(memberDTO);
        member.setRole(Role.CHIEF);

        member =
                memberRepository.save(member);

        return member;
    }

    public Member saveChiefMember(MemberDTO memberDTO){

        validateDuplicateMember(memberDTO.getEmail());

        Member member =
                MemberDTO.dtoEntity(memberDTO);
        member.setRole(Role.CHIEF);

        member =
                memberRepository.save(member);

        return member;
    }

    public Member saveManagerMember(MemberDTO memberDTO){

        validateDuplicateMember(memberDTO.getEmail());

        Member member =
                MemberDTO.dtoEntity(memberDTO);
        member.setRole(Role.MANAGER);

        member =
                memberRepository.save(member);

        return member;
    }

    private void validateDuplicateMember(String memberDTO){

        Member member =
                memberRepository.findByEmail(memberDTO);

        if(member != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }

    }
}
