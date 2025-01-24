package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
}
