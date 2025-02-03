package com.lookatme.smartstay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cart_num; //장바구니 번호

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num")
    private Member member; //회원 조인

    public static Cart createCart(Member member) {
        Cart cart = new Cart();
        cart.setMember(member);
        return cart;
    }

}
