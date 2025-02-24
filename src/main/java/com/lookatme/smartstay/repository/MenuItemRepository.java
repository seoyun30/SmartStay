package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    @Query("select m from MenuItem m where m.orderItem.service_num = :service_num")
    List<MenuItem> findByOrderItemService_num(Long service_num);

}
