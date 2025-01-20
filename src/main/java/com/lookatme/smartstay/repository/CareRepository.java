package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Care;
import com.lookatme.smartstay.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareRepository extends JpaRepository<Care, Long> {
}
