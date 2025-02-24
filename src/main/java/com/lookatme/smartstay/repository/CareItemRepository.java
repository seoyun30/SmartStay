package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.CareItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CareItemRepository extends JpaRepository<CareItem, Long> {

    @Query("select c from CareItem c where c.orderItem.service_num = :service_num")
    List<CareItem> findByOrderItemService_num(Long service_num);
}