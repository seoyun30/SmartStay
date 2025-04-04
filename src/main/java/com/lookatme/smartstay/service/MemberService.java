package com.lookatme.smartstay.service;

import com.lookatme.smartstay.constant.Power;
import com.lookatme.smartstay.constant.Role;
import com.lookatme.smartstay.dto.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException, AuthenticationException {

        Member member =
                this.memberRepository.findByEmail(email);

        log.info("로그인 시도하는 사람: " + member);

        if(member == null) {
            throw new UsernameNotFoundException("가입된 회원이 아닙니다.");

        }


        if (member.getRole() == Role.CHIEF || member.getRole() == Role.MANAGER) {
            Power power = member.getPower();

            if (power != null && power == Power.NO) {
                log.info("승인되지 않은 회원 로그인 시도 차단: " + member.getEmail());
                throw new AuthenticationException("승인 요청 중입니다.") {
                }; // 로그인 차단
            }


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

    public Member saveMember(MemberDTO memberDTO){ //유저회원저장

        validateDuplicateMember(memberDTO);

        Member member =
                MemberDTO.dtoEntity(memberDTO);

        member.setRole(Role.USER);

        log.info(member);
        member =
                memberRepository.save(member);

        return member;
    }

    public Member saveSuperAdminMember(MemberDTO memberDTO){ //최초치프회원 저장

        validateDuplicateMember(memberDTO);

        Member member =
                MemberDTO.dtoEntity(memberDTO);


        member.setRole(Role.CHIEF);
        member.setPower(Power.NO);


        member =
                memberRepository.save(member);


        return member;
    }

    public Member saveChiefMember(MemberDTO memberDTO, BrandDTO brandDTO){ //치프회원 저장

        log.info(memberDTO);
        log.info(brandDTO);
        validateDuplicateMember(memberDTO);

        Member member =
                MemberDTO.dtoEntity(memberDTO);

        Brand brand =
                brandRepository.findById(brandDTO.getBrand_num()).get();

        member.setRole(Role.CHIEF);
        member.setPower(Power.NO);

        member.setBrand(brand);


        member =
                memberRepository.save(member);

        return member;
    }

    public Member saveManagerMember(MemberDTO memberDTO, BrandDTO brandDTO, HotelDTO hotelDTO){ //매니져회원 저장

        validateDuplicateMember(memberDTO);

        Member member =
                MemberDTO.dtoEntity(memberDTO);

        Hotel hotel =
                hotelRepository.findById(hotelDTO.getHotel_num()).get();

        member.setRole(Role.MANAGER);
        member.setPower(Power.NO);

        member.setHotel(hotel);

        member.setBrand(hotel.getBrand());


        member =
                memberRepository.save(member);

        return member;
    }

    private void validateDuplicateMember(MemberDTO memberDTO){

        Member member =
                memberRepository.findByEmail(memberDTO.getEmail());

        if(member != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }

        boolean existsByTel = memberRepository.existsByTel((memberDTO.getTel()));
        if (existsByTel) {
            throw new IllegalStateException("이미 사용중인 연락처입니다.");
        }



    }

    public PageResponseDTO<MemberDTO> memberList(PageRequestDTO pageRequestDTO, String sortOrder, String orderType, String type){
        Pageable pageable = null;


        if(sortOrder.equals("DESC")){
            pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by(orderType).descending());

        }else {
            pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), Sort.by(orderType).ascending());

        }

        log.info(pageable);
        log.info("정렬방식" + sortOrder);

        Page<Member> memberPage = null;

        if (pageRequestDTO.getKeyword() != null && !pageRequestDTO.getKeyword().equals("")) {

            if(type.equals("1")) {
                memberPage = memberRepository.searchMemberByEmail(pageRequestDTO.getKeyword(), pageable);
            }else if(type.equals("2")) {
                memberPage = memberRepository.searchMemberByName(pageRequestDTO.getKeyword(), pageable);
            }else if(type.equals("3")) {
                memberPage = memberRepository.searchMemberByTel(pageRequestDTO.getKeyword(), pageable);
            }else if(type.equals("4")) {
                memberPage = memberRepository.searchMemberByRole(pageRequestDTO.getKeyword(), pageable);
            } else {
                memberPage = memberRepository.searchMember(pageRequestDTO.getKeyword(), pageable);
            }

        }else {
            memberPage = memberRepository.selectAll(pageable);

        }


        List<Member> memberList = memberPage.getContent();
        log.info("DB에서 가져온 회원 목록 : ");
        memberList.forEach(member -> log.info(member.toString()));


        List<MemberDTO> memberDTOList = memberList.stream().map(member -> modelMapper.map(member, MemberDTO.class) ).collect(Collectors.toList());



        log.info("정렬된 회원 목록:");
        memberDTOList.forEach(memberDTO -> log.info(memberDTO.toString()));

        PageResponseDTO<MemberDTO> memberDTOPageResponseDTO= PageResponseDTO.<MemberDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(memberDTOList)
                .total((int)memberPage.getTotalElements())
                .build();


        return memberDTOPageResponseDTO;
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

    public boolean checkPassword(String email, String password) {

        if(email == null || email.isEmpty()){
            return false;
        }

        MemberDTO member = findbyEmail(email);

        if(member == null){
            return false;
        }

        return passwordEncoder.matches(password, member.getPassword());
    }

    public void updateMember(MemberDTO memberDTO){ //회원마이페이지 수정

        //수정하려는 사람찾기
        Member member = memberRepository.findByEmail(memberDTO.getEmail());

        if(member == null){
            throw new EntityNotFoundException("회원 정보를 찾을 수 없습니다.");
        }
        member.setTel(memberDTO.getTel());

        if(!memberDTO.getPassword().isEmpty()){
            log.info("비밀번호가 안비어있다. 비밀번호 변경예정");
            String encodedPassword = passwordEncoder.encode(memberDTO.getPassword());
            member.setPassword(encodedPassword);
            log.info("비밀번호 변경했음");

        } else {
            log.info("비밀번호가 비어있다.");
        }

    }

    public MemberDTO powerAdmit(String email) {
        Member member = memberRepository.findByEmail(email);

        if (member != null) {
            member.setPower(member.getPower() == Power.YES ? Power.NO : Power.YES);
            memberRepository.save(member);
        }

        member = memberRepository.findByEmail(email);
        MemberDTO memberDTO = modelMapper.map(member, MemberDTO.class);

        return memberDTO;
    }



    public PageResponseDTO<MemberDTO> adPowerList(PageRequestDTO pageRequestDTO, String email, String type) { //슈퍼어드민이 승인하는 권한리스트

        Pageable pageable = pageRequestDTO.getPageable("member_num");

        log.info(pageable);
        log.info("서비스진입");


        //슈퍼어드민이 승인해야 하는 회원리스트만 조회 (페이징적용)
        Page<Member> memberPage;

        if (pageRequestDTO.getKeyword() != null && !pageRequestDTO.getKeyword().isEmpty()) {
            log.info("검색어 적용: " + pageRequestDTO.getKeyword());

            if(type.equals("1")) {
                memberPage = memberRepository.searchSelectBySuperAdminByEmail(pageRequestDTO.getKeyword(), pageable);
            }else if(type.equals("2")) {
                memberPage = memberRepository.searchSelectBySuperAdminByName(pageRequestDTO.getKeyword(), pageable);
            }else if(type.equals("3")) {
                memberPage = memberRepository.searchSelectBySuperAdminByTel(pageRequestDTO.getKeyword(), pageable);
            }else {
                memberPage = memberRepository.searchSelectBySuperAdmin(pageRequestDTO.getKeyword(), pageable);
            }
        } else {
            log.info("전체 슈퍼어드민 리스트 적용");
            memberPage = memberRepository.selectBySuperAdmin(pageable);
        }
        List<Member> memberList = memberPage.getContent();


        if (memberList == null) {
            return null;

        } else {
            List<MemberDTO> memberDTOList = memberList.stream()
                    .map(member -> modelMapper.map(member, MemberDTO.class))
                    .collect(Collectors.toList());

            log.info("dto변환");

            memberDTOList.forEach(dto -> log.info(dto));


            return PageResponseDTO.<MemberDTO>withAll()
                    .pageRequestDTO(pageRequestDTO)
                    .dtoList(memberDTOList)
                    .total((int) memberPage.getTotalElements())
                    .build();

        }
    }


    public PageResponseDTO<MemberDTO> cmPowerList(PageRequestDTO pageRequestDTO, String email, String type) { //치프가 승인하는 권한리스트
    //로그인한 권한을 승인해줄 치프의 이메일과 페이징처리를 위한 내용을 받아서
        Member member  = memberRepository.findByEmail(email);

        if (member == null || member.getBrand() == null) {      //내이메일로 찾아온 정보가 없거나 소속이 없다면
            return null; // 또는 예외 처리
        }

        Long brandNum = member.getBrand().getBrand_num();

        Pageable pageable = pageRequestDTO.getPageable("member_num");

        log.info(pageable);
        log.info("서비스진입");

        Page<Member> memberPage;
        if (pageRequestDTO.getKeyword() != null && !pageRequestDTO.getKeyword().isEmpty()) {
            log.info("검색어 적용: " + pageRequestDTO.getKeyword());

            if (type.equals("1")) {
                memberPage = memberRepository.searchSelectByChiefByEmail(brandNum, pageRequestDTO.getKeyword(), pageable);
            }else if (type.equals("2")) {
                memberPage = memberRepository.searchSelectByChiefByName(brandNum, pageRequestDTO.getKeyword(), pageable);
            }else if (type.equals("3")) {
                memberPage = memberRepository.searchSelectByChiefByTel(brandNum, pageRequestDTO.getKeyword(), pageable);
            }else if (type.equals("4")) {
                memberPage = memberRepository.searchSelectByChiefByRole(brandNum, pageRequestDTO.getKeyword(), pageable);
            }else {
                memberPage = memberRepository.searchSelectByChief(brandNum, pageRequestDTO.getKeyword(), pageable);
            }
        } else {
            memberPage = memberRepository.selectByChief(brandNum, pageable);
        }


        List<Member> memberList = memberPage.getContent();

        log.info("권한리스트");
        memberList.forEach(role -> log.info(role));


        if(memberList == null) {
            return null;
        }else {
            List<MemberDTO> memberDTOList = memberList.stream()
                    .map(memberA -> {
                        MemberDTO dto = modelMapper.map(memberA, MemberDTO.class);
                        if (memberA.getBrand() != null) {
                            dto.setBrandDTO(modelMapper.map(memberA.getBrand(), BrandDTO.class));
                        }
                        if (memberA.getHotel() != null) {
                            dto.setHotelDTO(modelMapper.map(memberA.getHotel(), HotelDTO.class));
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());

            log.info("dto변환");
            memberDTOList.forEach(dto -> log.info(dto));


            return PageResponseDTO.<MemberDTO>withAll()
                    .pageRequestDTO(pageRequestDTO)
                    .dtoList(memberDTOList)
                    .total((int) memberPage.getTotalElements())
                    .build();
        }
    }

    public void changePower(String role, MemberDTO memberDTO, Long hotel_num) {

        Member member = memberRepository.findById(memberDTO.getMember_num())
                .orElseThrow(() -> new EntityNotFoundException("해당 멤버를 찾을 수 없습니다. member_num: " + memberDTO.getMember_num().getClass()));

        if (role.equals("CHIEF")) { //chief로 변경
            member.setRole(Role.CHIEF);
            member.setHotel(null);

        } else if (role.equals("MANAGER")) {
            member.setRole(Role.MANAGER); //manager로 변경

            // hotel_num이 존재하면 그에 맞는 호텔을 찾음
            Hotel hotel = hotelRepository.findById(hotel_num)
                    .orElseThrow(() -> new EntityNotFoundException("호텔을 찾을 수 없습니다."));
            member.setHotel(hotel);
        }
        memberRepository.save(member);
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
            String emailText = "안녕하세요 " + member.getName()+ " 님.\n"+"요청하신 임시 비밀번호는 다음과 같습니다.\n" +
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

