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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
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
    private final EmailService emailService;




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
        }else if("CHIEF".equals(member.getRole().name())){ // && member.getPower() == Power.YES
                                                    //나중에 승인기능 개발후 적용예정
            log.info("치프");
            role = Role.CHIEF.name();
            authorities.add(new SimpleGrantedAuthority(Role.CHIEF.name()));
        }else if("MANAGER".equals(member.getRole().name()) ){ //&& member.getPower() == Power.YES
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

    public Member saveMember(MemberDTO memberDTO){ //유저회원저장

        validateDuplicateMember(memberDTO.getEmail());

        Member member =
                MemberDTO.dtoEntity(memberDTO);

        member.setRole(Role.USER);

        log.info(member);
        member =
                memberRepository.save(member);

        return member;
    }

    public Member saveSuperAdminMember(MemberDTO memberDTO){ //최초치프회원 저장

        validateDuplicateMember(memberDTO.getEmail());

        Member member =
                MemberDTO.dtoEntity(memberDTO);

        member.setRole(Role.CHIEF);

        member =
                memberRepository.save(member);

        return member;
    }

    public Member saveChiefMember(MemberDTO memberDTO, BrandDTO brandDTO){ //치프회원 저장

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

    public Member saveManagerMember(MemberDTO memberDTO, BrandDTO brandDTO, HotelDTO hotelDTO){ //매니져회원 저장

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

    public List<MemberDTO> memberList() { //회웍목록리스트

        List<Member> memberList = memberRepository.findAll();
        if(memberList.isEmpty()) {
            return new ArrayList<>();

        }else {
            List<MemberDTO> memberDTOList = memberList.stream()
                    .map(memberA -> modelMapper.map(memberA, MemberDTO.class))
                            .collect(Collectors.toList());

            memberDTOList.forEach(dto -> log.info(dto));

            return memberDTOList;
        }

    }

    public List<MemberDTO> searchMember(String keyword){
       List<Member> members = memberRepository.searchMember(keyword);

       return members.stream()
               .map(member -> modelMapper.map(member, MemberDTO.class))
               .collect(Collectors.toList());
    }




    public MemberDTO readMember(String email){ //회원마이페이지정보
        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            throw new RuntimeException("회원 정보를 찾을 수 없습니다.");
        }

      return MemberDTO.builder()
              .email(member.getEmail())
              .name(member.getName())
              .tel(member.getTel())
              .role(member.getRole())
              .reg_date(member.getReg_date())
              .build();
    }




    public void updateMember(MemberDTO memberDTO){ //회원마이페이지 수정

        //수정하려는 사람찾기
        Member member = memberRepository.findByEmail(memberDTO.getEmail());

        if(member == null){
            throw new EntityNotFoundException("회원 정보를 찾을 수 없습니다.");
        }
        member.setTel(memberDTO.getTel());

        if(!memberDTO.getPassword().isEmpty()){
            log.info("비밀번호가 안비어있다.");
            String encodedPassword = new BCryptPasswordEncoder().encode(memberDTO.getPassword());
            member.setPassword(encodedPassword);

        } else {
            log.info("비밀번호가 비어있다.");
        }

    }



    public List<MemberDTO> adPowerList(String email){ //슈퍼어드민이 승인하는 권한리스트
        List<Member> memberList = memberRepository.selectBySuperAdmin();

        if (email != null && !email.isEmpty()) {
            Member member = memberRepository.findByEmail(email);
            if (member != null) {
                member.setPower(member.getPower() == Power.YES ? Power.NO : Power.YES);
                memberRepository.save(member);
            }
        }

        if(memberList == null) {
            return null;
        }else {
            List<MemberDTO> memberDTOList = memberList.stream()
                    .map(memberA -> modelMapper.map(memberA, MemberDTO.class))
                            .collect(Collectors.toList());

            log.info("dto변환");
            memberDTOList.forEach(dto -> log.info(dto));

            return memberDTOList;
        }
    }


    public List<MemberDTO> cmPowerList(String email) { //치프가 승인하는 권한리스트

        Member member = memberRepository.findByEmail(email);

        log.info(member);
        List<Member> memberList = null;
        if(member != null && member.getBrand() != null) {
            memberList = memberRepository.selectByChief(member.getBrand().getBrand_num());

            log.info("권한리스트");
            memberList.forEach(role -> log.info(role));
        }

        if(memberList == null) {
            return null;
        }else {
            List<MemberDTO> memberDTOList = memberList.stream()
                    .map(memberA -> modelMapper.map(memberA, MemberDTO.class)
                            .setBrandDTO( modelMapper.map(memberA.getBrand() , BrandDTO.class) )
                            .setHotelDTO(modelMapper.map(memberA.getHotel(), HotelDTO.class)))
                            .collect(Collectors.toList());

            log.info("dto변환");
            memberDTOList.forEach(dto -> log.info(dto));

            return memberDTOList;
        }
    }


   /* public void adPowerMember(String email) {

        Member member = memberRepository.findByEmail(email);
        log.info("파워승인파워승인" +email);
        log.info(member);
        if (member != null) {
            member.setPower(Power.YES);
            memberRepository.save(member);
        }
    }*/


    public void powerMember(String email) { // 권한승인 목록

        log.info("파워승인파워승인" + email);
        if (email != null && !email.isEmpty()) {
            Member member = memberRepository.findByEmail(email);
            if (member != null) {
                member.setPower(member.getPower() == Power.YES ? Power.NO : Power.YES);
                memberRepository.save(member);
                log.info("power 변경: " + email + " -> " + member.getPower());
            } else {
                log.info("해당 이메일로 회원을 찾을 수 없음: " + email);
            }
            }

        }


     public Member findID(String name, String tel){ //회원Email찾기

        log.info("name: " + name + " tel: " + tel);

        Member member = memberRepository.findID(name, tel);

        if(member == null){
            throw new EntityNotFoundException("일치하는 회원이 없습니다.");
        }
        log.info("member: " + member);

        return member;
     }


    public void passwordSend(MemberDTO memberDTO){ //pw 이메일로 임시비밀번호 받기

        try{
            Member member = memberRepository.findByEmail(memberDTO.getEmail() );
            if(member == null){
                throw new IllegalStateException("일치하는 회원이 없습니다.");
            }
            if(!member.getName().equals(memberDTO.getName())){
                throw new IllegalStateException("회원 이름이 일치하지 않습니다.");
            }
            if(!member.getTel().equals(memberDTO.getTel())){
                throw new IllegalStateException("전화번호가 일치하지 않습니다.");
            }
            String tempPassword = generateTempPassword(8);
            member.setPassword(passwordEncoder.encode(tempPassword));
            memberRepository.save(member);

            String emailSubject = "임시비밀번호 발급";
            String emailText = "안녕하세요 " +member.getName()+ " 님.\n"+"요청하신 임시 비밀번호는 다음과 같습니다.\n" +
                    tempPassword+"\n"+"로그인 후 반드시 비밀번호를 변경해 주세요.";

            emailService.sendEmail(member.getEmail(), emailSubject, emailText);
            System.out.println("전송완료");

        }catch (IllegalStateException e) {
            System.out.println("회원 가입을 실패하였습니다. " + e.getMessage());
            throw e;
        }catch (Exception e){
            System.out.println("예기치 않은 문제가 발생하였습니다." + e.getMessage());
            throw new RuntimeException("가입 중 오류가 발생하였습니다.");
        }
    }



    private String generateTempPassword(int length){    //비밀번호생성기

        final String chars = "ABCDEFGHIGKLMNOPQRSTUVWXYZabcdefghigklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        try {
            for(int i = 0; i < length; i++) {
                int index = random.nextInt(chars.length());
                sb.append(chars.charAt(index));
            }
            return sb.toString();
        } catch (Exception e) {
            System.err.println("임시 비밀번호 생성 실패!!");
            throw new RuntimeException("임시 비밀번호 생성을 실패하였습니다.");
        }
    }

    }

