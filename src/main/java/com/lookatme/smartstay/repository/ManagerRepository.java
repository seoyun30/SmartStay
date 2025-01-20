package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Chief;
import com.lookatme.smartstay.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
}
