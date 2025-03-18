package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

 Cart findByMemberEmail(String email);
}
