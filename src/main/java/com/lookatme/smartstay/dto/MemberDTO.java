package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.constant.Power;
import com.lookatme.smartstay.constant.Role;
import com.lookatme.smartstay.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;


@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDTO {

    private Long member_num; //회원 번호

    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message = "이메일 형식에 맞춰서 작성해주세요.")
    private String email; //회원 이메일

    @NotBlank(message = "비밀번호를 입력하세요.")
    @Size(min = 8, max= 20, message = "비밀번호는 8 ~ 20 글자로 입력해주세요.")
    private String password; //회원 비밀번호

    @NotBlank(message = "이름을 입력하세요.")
    private String name; //회원 이름

    @NotBlank(message = "연락처를 입력하세요.")
    private String tel; //연락처

    private Power power; //승인

    private Role role; //회원 권한

    private BrandDTO brandDTO; //브랜드

    private HotelDTO hotelDTO; //호텔

    private String corn; // 권한선택값

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

    public static Member dtoEntity(MemberDTO memberDTO) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Member member = new Member();
        member.setName(memberDTO.name);
        member.setEmail(memberDTO.email);
        member.setTel(memberDTO.tel);
        member.setPassword(passwordEncoder.encode(memberDTO.password));
        member.setRole(Role.SUPERADMIN);
        member.setRole(Role.CHIEF);
        member.setRole(Role.MANAGER);
        member.setRole(Role.USER);

        return member;
    }

    public MemberDTO setBrandDTO(BrandDTO brandDTO) {
        this.brandDTO = brandDTO;
        return this;
    }

    public MemberDTO setHotelDTO(HotelDTO hotelDTO) {
        this.hotelDTO = hotelDTO;
        return this;
    }
}
