package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.constant.OrderState;
import com.lookatme.smartstay.entity.OrderReserveItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface OrderReserveItemRepository extends JpaRepository<OrderReserveItem, Long> {

    @Query("select o from OrderReserveItem o where o.roomReserveItem.room.hotel.hotel_num = :hotel_num")
    Page<OrderReserveItem> findByHotelPage (Long hotel_num, Pageable pageable);

    @Query("select o from OrderReserveItem o where o.orderReserve.member.email = :email")
    Page<OrderReserveItem> findByEmailPage (String email, Pageable pageable);

    @Query("select o from OrderReserveItem o where o.serviceitem_num = :serviceitem_num and o.orderReserve.member.email = :email")
    OrderReserveItem findByServiceitemNumAndEmail(Long serviceitem_num, String email);

    @Query("SELECT o FROM OrderReserveItem o WHERE o.orderReserve.member.email = :email "
            + "AND (:reserve_id IS NULL OR o.orderReserve.order_id LIKE %:reserve_id%) "
            + "AND (:hotel_name IS NULL OR o.roomReserveItem.room.hotel.hotel_name LIKE %:hotel_name%) "
            + "AND (:room_name IS NULL OR o.roomReserveItem.room.room_name LIKE %:room_name%) "
            + "AND (:state IS NULL OR o.orderReserve.order_state = :state) "
            + "AND (:sdate IS NULL OR o.reg_date >= :sdate) "
            + "AND (:edate IS NULL OR o.reg_date <= :edate)")
    Page<OrderReserveItem> findOrderReserveBySearch(
            @Param("email") String email,
            @Param("reserve_id") String reserve_id,
            @Param("hotel_name") String hotel_name,
            @Param("room_name") String room_name,
            @Param("state") OrderState state,
            @Param("sdate") LocalDateTime sdate,
            @Param("edate") LocalDateTime edate,
            Pageable pageable);

    @Query("SELECT o FROM OrderReserveItem o WHERE o.roomReserveItem.room.hotel.hotel_num = :hotel_num "
            + "AND (:reserve_id IS NULL OR o.orderReserve.order_id LIKE %:reserve_id%) "
            + "AND (:room_name IS NULL OR o.roomReserveItem.room.room_name LIKE %:room_name%) "
            + "AND (:reserve_name IS NULL OR o.orderReserve.member.name LIKE %:reserve_name% )"
            + "AND (:state IS NULL OR o.orderReserve.order_state = :state) "
            + "AND (:sdate IS NULL OR o.reg_date >= :sdate) "
            + "AND (:edate IS NULL OR o.reg_date <= :edate)")
    Page<OrderReserveItem> findOrderReserveBySearch(
            @Param("hotel_num") Long hotel_num,
            @Param("reserve_id") String reserve_id,
            @Param("room_name") String room_name,
            @Param("reserve_name") String reserve_name,
            @Param("state") OrderState state,
            @Param("sdate") LocalDateTime sdate,
            @Param("edate") LocalDateTime edate,
            Pageable pageable);

}
