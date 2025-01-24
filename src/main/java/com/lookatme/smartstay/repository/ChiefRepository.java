package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChiefRepository extends JpaRepository<Brand, Long> {
}
