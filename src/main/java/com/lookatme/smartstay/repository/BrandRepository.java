package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    @Query("SELECT b FROM Brand b WHERE b.brand_num = :brand_num")
    List<Brand> findByBrandNum(@Param("brand_num") Long brand_num);
}
