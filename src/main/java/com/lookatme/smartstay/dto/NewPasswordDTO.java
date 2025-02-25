package com.lookatme.smartstay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class NewPasswordDTO {

    @NotBlank(message = "기존 비밀번호를 입력하세요.")
    @Size(min = 8, max= 20, message = "비밀번호는 8 ~ 20 글자로 입력해주세요.")
    private String password; //회원 비밀번호

    @NotBlank(message = "새로운 비밀번호를 입력하세요.")
    @Size(min = 8, max= 20, message = "비밀번호는 8 ~ 20 글자로 입력해주세요.")
    private String newPassword; // 임시 비밀번호

    @NotBlank(message = "새로운 비밀번호를 다시 입력하세요.") // 추가
    @Size(min = 8, max= 20, message = "비밀번호는 8 ~ 20 글자로 입력해주세요.")
    private String rePassword; // 비밀번호 확인
}
