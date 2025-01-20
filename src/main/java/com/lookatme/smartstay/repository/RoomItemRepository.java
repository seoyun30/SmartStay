package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Pay;
import com.lookatme.smartstay.entity.RoomItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomItemRepository extends JpaRepository<RoomItem, Long> {
}
