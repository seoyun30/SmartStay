package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Cart;
import com.lookatme.smartstay.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
