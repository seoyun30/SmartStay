package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.RoomReserveItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomReserveItemRepository extends JpaRepository<RoomReserveItem, Long> {

    //내 장바구니에 담긴 아이템 찾기
   // @Query("select r from RoomItem r where r.cart.cart_num = :cart_num and r.room.room_num = :room_num")
    //public List<RoomItem> findByCart_Cart_numAndRoom_num (Long cart_num, Long room_num);

   // @Query("select r from RoomItem r where r.cart.cart_num = :cart_num")
    //public List<RoomItem> findByCart_Cart_num (Long cart_num);

    @Query("select r from RoomReserveItem r where r.room.room_num = :room_num")
    public List<RoomReserveItem> findByRoomRoom_num(Long room_num);

    //추후 룸서비스 버전도 생성 예정


}
