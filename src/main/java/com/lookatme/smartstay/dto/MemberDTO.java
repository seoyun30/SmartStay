package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.constant.Accept;
import com.lookatme.smartstay.constant.Role;
import com.lookatme.smartstay.entity.Chief;
import com.lookatme.smartstay.entity.Manager;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;

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
    private String email; //회원 이메일

    @Email(message = "이메일 형식에 맞춰서 작성해주세요.")
    @NotBlank(message = "비밀번호를 입력하세요.")
    @Size(min = 2, max= 20, message = "비밀번호는 2 ~ 20 글자로 입력해주세요.")
    private String password; //회원 비밀번호

    @NotBlank(message = "이름을 입력하세요.")
    private String name; //회원 이름

    @NotBlank(message = "연락처를 입력하세요.")
    private String tel; //연락처

    private Accept accept; //승인

    private Role role; //회원 권한

    private Chief chief; //호텔(총판)

    private Manager manager; //호텔(매장)

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

}
