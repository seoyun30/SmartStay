package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.CareReserveItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CareReserveItemRepository extends JpaRepository<CareReserveItem, Long> {

    @Query("select c from CareReserveItem c where c.orderReserveItem.serviceitem_num = :serviceitem_num")
    List<CareReserveItem> findByOrderReserveItemServiceitem_num(Long serviceitem_num);
}