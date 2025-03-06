package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Hotel;
import com.lookatme.smartstay.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r WHERE r.hotel = :hotel")
    Page<Room> findByHotel(@Param("hotel") Hotel hotel, Pageable pageable);

    List<Room> findByHotel(Hotel hotel);

    @Query("select min(r.room_price) from Room r where r.hotel.hotel_num = :hotel_num")
    Long findLowestRoomPriceByHotelNum(@Param("hotel_num") Long hotel_num);

    @Query("SELECT r FROM Room r WHERE LOWER(r.room_name) LIKE LOWER(CONCAT('%', :roomName, '%'))")
    List<Room> findByRoom_nameContainingIgnoreCase(@Param("roomName") String roomName, Sort sort);

    @Query("SELECT r FROM Room r WHERE LOWER(r.room_info) LIKE LOWER(CONCAT('%', :roomInfo, '%'))")
    List<Room> findByRoom_infoContainingIgnoreCase(@Param("roomInfo") String roomInfo, Sort sort);

    @Query("SELECT r FROM Room r WHERE LOWER(r.room_type) LIKE LOWER(CONCAT('%', :roomType, '%'))")
    List<Room> findByRoom_typeContainingIgnoreCase(@Param("roomType") String roomType, Sort sort);
}