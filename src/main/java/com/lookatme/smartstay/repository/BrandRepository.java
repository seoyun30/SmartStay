package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    @Query("select b from Brand b where b.create_by = :email")
    public List<Brand> findByEmail(String email);
}
