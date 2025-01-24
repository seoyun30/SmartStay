package com.lookatme.smartstay.entity;

import com.lookatme.smartstay.constant.Power;
import com.lookatme.smartstay.constant.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Member extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long member_num; //회원 번호

    @Email
    @Column(unique = true, nullable = false)
    private String email; //회원 이메일

    private String password; //회원 비밀번호

    private String name; //회원 이름

    private String tel; //연락처

    @Enumerated(EnumType.STRING)
    private Power power; //승인

    @Enumerated(EnumType.STRING)
    private Role role; //회원 권한

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_num")
    private Brand brand; //브랜드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_num")
    private Hotel hotel; //호텔
}
