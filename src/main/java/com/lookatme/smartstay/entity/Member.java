package com.lookatme.smartstay.entity;

import com.lookatme.smartstay.constant.Accept;
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
    private Accept accept; //승인

    @Enumerated(EnumType.STRING)
    private Role role; //회원 권한

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chief_num")
    private Chief chief; //호텔(총판)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_num")
    private Manager	manager; //호텔(매장)
}
