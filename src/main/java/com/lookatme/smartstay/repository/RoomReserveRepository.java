package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Pay;
import com.lookatme.smartstay.entity.RoomReserve;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomReserveRepository extends JpaRepository<RoomReserve, Long> {
}
