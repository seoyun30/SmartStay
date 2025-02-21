package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

}
