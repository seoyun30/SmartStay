package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.MenuReserveItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuReserveItemRepository extends JpaRepository<MenuReserveItem, Long> {

    @Query("select m from MenuReserveItem m where m.orderReserveItem.serviceitem_num = :serviceitem_num")
    List<MenuReserveItem> findByOrderReserveItemServiceitem_num(Long serviceitem_num);

}
