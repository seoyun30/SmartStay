package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.OrderReserve;
import com.lookatme.smartstay.entity.RoomReserve;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderReserveRepository extends JpaRepository<OrderReserve, Long> {
}
