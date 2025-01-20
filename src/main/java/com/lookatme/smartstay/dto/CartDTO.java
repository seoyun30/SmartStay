package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CartDTO {

    private Long cart_num; //장바구니 번호

    private MemberDTO memberDTO; //회원 조인

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;

}
