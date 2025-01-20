package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.OrderItem;
import com.lookatme.smartstay.entity.Pay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
