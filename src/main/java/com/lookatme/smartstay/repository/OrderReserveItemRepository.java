package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.OrderReserveItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderReserveItemRepository extends JpaRepository<OrderReserveItem, Long> {

    @Query("select o from OrderReserveItem o where o.roomReserveItem.room.hotel.hotel_num = :hotel_num")
    Page<OrderReserveItem> findByHotelPage (Long hotel_num, Pageable pageable);

    @Query("select o from OrderReserveItem o where o.orderReserve.member.email = :email")
    Page<OrderReserveItem> findByEmailPage (String email, Pageable pageable);

    @Query("select o from OrderReserveItem o where o.serviceitem_num = :serviceitem_num and o.orderReserve.member.email = :email")
    OrderReserveItem findByServiceitemNumAndEmail(Long serviceitem_num, String email);

}
