package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.RoomReserveItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomReserveItemRepository extends JpaRepository<RoomReserveItem, Long> {

    //내 장바구니에 담긴 아이템 찾기
   // @Query("select r from RoomItem r where r.cart.cart_num = :cart_num and r.room.room_num = :room_num")
    //public List<RoomItem> findByCart_Cart_numAndRoom_num (Long cart_num, Long room_num);

   // @Query("select r from RoomItem r where r.cart.cart_num = :cart_num")
    //public List<RoomItem> findByCart_Cart_num (Long cart_num);

    @Query("select r from RoomReserveItem r where r.roomReserve.member.email = :email")
    List<RoomReserveItem> findByEmail(String email);

    @Query("select r from RoomReserveItem r where r.roomReserve.member.email = :email")
    Page<RoomReserveItem> findByEmailPage (String email, Pageable pageable);

    @Query("select r from RoomReserveItem r where r.room.hotel.hotel_num = :hotel_num")
    Page<RoomReserveItem> findByHotelPage (Long hotel_num, Pageable pageable);
    
    @Query("select r from RoomReserveItem r where r.room.room_num = :room_num")
    List<RoomReserveItem> findByRoomRoom_num(Long room_num);

    @Query("select r from RoomReserveItem r where r.roomreserveitem_num = :roomreserveitem_num and r.roomReserve.member.email = :email")
    RoomReserveItem findByReserveItemNumAndEmail(Long roomreserveitem_num, String email);

    @Query("SELECT COUNT(r) > 0 FROM RoomReserveItem r " +
            "WHERE r.room.room_num = :room_num " +
            "AND (r.in_date < :out_date AND r.out_date > :in_date) " +
            "AND (:roomreserveitem_num IS NULL OR r.roomreserveitem_num <> :roomreserveitem_num)")
    boolean isRoomAlreadyReserve(Long room_num,
                                LocalDateTime in_date,
                                LocalDateTime out_date,
                                Long roomreserveitem_num);

}
