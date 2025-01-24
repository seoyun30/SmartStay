package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRepository extends JpaRepository<Hotel, Long> {
}
