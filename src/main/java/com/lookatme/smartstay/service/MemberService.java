package com.lookatme.smartstay.service;

import com.lookatme.smartstay.constant.Power;
import com.lookatme.smartstay.constant.Role;
import com.lookatme.smartstay.dto.BrandDTO;
import com.lookatme.smartstay.dto.HotelDTO;
import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.entity.Brand;
import com.lookatme.smartstay.entity.Hotel;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.repository.BrandRepository;
import com.lookatme.smartstay.repository.HotelRepository;
import com.lookatme.smartstay.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final BrandRepository brandRepository;
    private final HotelRepository hotelRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member =
                this.memberRepository.findByEmail(email);

        log.info("로그인 시도하는 사람: " + member);

        if(member == null) {
            throw new UsernameNotFoundException("가입된 회원이 아닙니다.");

        }
        String role = "";

        List<GrantedAuthority> authorities = new ArrayList<>();

        if("SUPERADMIN".equals(member.getRole().name())){
            log.info("슈퍼어드민");
            role = Role.SUPERADMIN.name();
            authorities.add(new SimpleGrantedAuthority(Role.SUPERADMIN.name()));
        }else if("CHIEF".equals(member.getRole().name()) && member.getPower() == Power.YES){
                                                    //나중에 승인기능 개발후 적용예정
            log.info("치프");
            role = Role.CHIEF.name();
            authorities.add(new SimpleGrantedAuthority(Role.CHIEF.name()));
        }else if("MANAGER".equals(member.getRole().name()) && member.getPower() == Power.YES){
            log.info("매니져");
            role = Role.MANAGER.name();
            authorities.add(new SimpleGrantedAuthority(Role.MANAGER.name()));
        }else {
            log.info("일반유저");
            role = Role.USER.name();
            authorities.add(new SimpleGrantedAuthority(Role.USER.name()));
        }

        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(role)
                .authorities(authorities)
                .build();
    }

    public MemberDTO findbyEmail(String email) {
        Member member = this.memberRepository.findByEmail(email);
        if(member != null) {
            MemberDTO memberDTO = modelMapper.map(member, MemberDTO.class);

            if (member.getBrand() != null) {
                Brand brand = brandRepository.findById(member.getBrand().getBrand_num()).orElseThrow(EntityNotFoundException::new);
                BrandDTO brandDTO = modelMapper.map(brand, BrandDTO.class);
                memberDTO.setBrandDTO(brandDTO);
            }

            if (member.getHotel() != null) {
                Hotel hotel = hotelRepository.findById(member.getHotel().getHotel_num()).orElseThrow(EntityNotFoundException::new);
                HotelDTO hotelDTO = modelMapper.map(hotel, HotelDTO.class);
                memberDTO.setHotelDTO(hotelDTO);
            }

            return memberDTO;

        }else {
            return null;
        }

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

    public Member saveChiefMember(MemberDTO memberDTO, BrandDTO brandDTO){

        log.info(memberDTO);
        log.info(brandDTO);
        validateDuplicateMember(memberDTO.getEmail());

        Member member =
                MemberDTO.dtoEntity(memberDTO);

        Brand brand =
                brandRepository.findById(brandDTO.getBrand_num()).get();

        member.setRole(Role.CHIEF);
        member.setBrand(brand);


        member =
                memberRepository.save(member);

        return member;
    }

    public Member saveManagerMember(MemberDTO memberDTO, BrandDTO brandDTO, HotelDTO hotelDTO){

        validateDuplicateMember(memberDTO.getEmail());

        Member member =
                MemberDTO.dtoEntity(memberDTO);

        Hotel hotel =
                hotelRepository.findById(hotelDTO.getHotel_num()).get();

        member.setRole(Role.MANAGER);
        member.setHotel(hotel);

        member.setBrand(hotel.getBrand());


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


    public MemberDTO read(String memberDTO){

      return null;
    }




    public Member update(MemberDTO memberDTO){

        validateDuplicateMember(memberDTO.getEmail());

            Member member =
                    MemberDTO.dtoEntity(memberDTO);

            return memberRepository.save(member);
        }



    public List<MemberDTO> adPowerList(){
        List<Member> roleList = memberRepository.selectBySuperAdmin();

        if(roleList == null) {
            return null;
        }else {
            List<MemberDTO> memberDTOList = roleList.stream()
                    .map(memberA -> modelMapper.map(memberA, MemberDTO.class).setBrandDTO( modelMapper.map(memberA.getBrand() , BrandDTO.class) ).setHotelDTO(modelMapper.map(memberA.getHotel(), HotelDTO.class)))
                    .collect(Collectors.toList());

            log.info("dto변환");
            memberDTOList.forEach(dto -> log.info(dto));

            return memberDTOList;
        }
    }

    public List<MemberDTO> cmPowerList(String email) {

        Member member = memberRepository.findByEmail(email);
        log.info(member);
        List<Member> roleList = null;
        if(member != null && member.getBrand() != null) {
            roleList = memberRepository.selectByChief(member.getBrand().getBrand_num());

            log.info("권한리스트");
            roleList.forEach(role -> log.info(role));
        }

        if(roleList == null) {
            return null;
        }else {
            List<MemberDTO> memberDTOList = roleList.stream()
                    .map(memberA -> modelMapper.map(memberA, MemberDTO.class).setBrandDTO( modelMapper.map(memberA.getBrand() , BrandDTO.class) ).setHotelDTO(modelMapper.map(memberA.getHotel(), HotelDTO.class)))
                    .collect(Collectors.toList());

            log.info("dto변환");
            memberDTOList.forEach(dto -> log.info(dto));

            return memberDTOList;
        }

       /*
        Member member = memberRepository.findByEmail(email);
        List<Member> roleList = null;
        if(member != null && member.getBrand() != null) {
            roleList = memberRepository.selectByChief(member.getBrand().getBrand_num());

            log.info("권한리스트");
            roleList.forEach(role -> log.info(role));
        }


        List<MemberDTO> memberDTOList = roleList.stream()
                .map(role -> {
                    MemberDTO memberDTO = modelMapper.map(role, MemberDTO.class);

                    // Brand 정보 설정
                    if (role.getBrand() != null) {
                        Brand brand = role.getBrand();
                        BrandDTO brandDTO = modelMapper.map(brand, BrandDTO.class);
                        memberDTO.setBrandDTO(brandDTO);
                    }

                    // Hotel 정보 설정
                    if (role.getHotel() != null) {
                        Hotel hotel = role.getHotel();
                        HotelDTO hotelDTO = modelMapper.map(hotel, HotelDTO.class);
                        log.info("hotel name:{}", hotelDTO.getHotel_name());
                        memberDTO.setHotelDTO(hotelDTO);
                    }

                    return memberDTO;
                })
                .collect(Collectors.toList());

        return memberDTOList;*/
    }


    public void powerMember(String email) {

        Member member = memberRepository.findByEmail(email);
        log.info("파워승인파워승인" +email);
        log.info("파워승인파워승인" +email);
        log.info("파워승인파워승인" +email);
        log.info("파워승인파워승인");
        log.info("파워승인파워승인");
        log.info("파워승인파워승인");
        log.info(member);
        log.info(member);
        if (member != null) {
            member.setPower(Power.YES);
            memberRepository.save(member);
        }
    }
    }

