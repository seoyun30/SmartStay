package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.RoomItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomItemRepository extends JpaRepository<RoomItem, Long> {

    @Query("select r from RoomItem r where r.cart.cart_num = :cart_num")
    List<RoomItem> findByCart_Cart_num (Long cart_num);

    @Query("select r from RoomItem r where r.room.room_num = :room_num" +
            " and r.in_date = :in_date and r.out_date = :out_date")
    List<RoomItem> findByRoom_numAndIn_dateAndOut_date
            (Long room_num, LocalDateTime in_date, LocalDateTime out_date);

}
