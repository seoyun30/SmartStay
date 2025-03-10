package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.constant.RoomState;
import com.lookatme.smartstay.entity.Hotel;
import com.lookatme.smartstay.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r WHERE r.hotel = :hotel")
    Page<Room> findByHotel(@Param("hotel") Hotel hotel, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Room r SET r.room_state = :room_state WHERE r.room_num = :room_num")
    void updateRoomState(@Param("room_num") Long room_num, @Param("room_state") RoomState room_state);

    List<Room> findByHotel(Hotel hotel);

    @Query("SELECT r FROM Room r JOIN r.hotel h WHERE h.hotel_num = :hotel_num")
    List<Room> findRoomsByHotelNum(@Param("hotel_num") Long hotel_num);

    @Query("select min(r.room_price) from Room r where r.hotel.hotel_num = :hotel_num")
    Long findLowestRoomPriceByHotelNum(@Param("hotel_num") Long hotel_num);

    @Query("SELECT r FROM Room r WHERE LOWER(r.room_name) LIKE LOWER(CONCAT('%', :roomName, '%'))")
    Page<Room> findByRoom_nameContainingIgnoreCase(@Param("roomName") String roomName, Pageable pageable);

    @Query("SELECT r FROM Room r WHERE LOWER(r.room_info) LIKE LOWER(CONCAT('%', :roomInfo, '%'))")
    Page<Room> findByRoom_infoContainingIgnoreCase(@Param("roomInfo") String roomInfo, Pageable pageable);

    @Query("SELECT r FROM Room r WHERE LOWER(r.room_type) LIKE LOWER(CONCAT('%', :roomType, '%'))")
    Page<Room> findByRoom_typeContainingIgnoreCase(@Param("roomType") String roomType, Pageable pageable);

    @Query("SELECT r FROM Room r " +
            "WHERE LOWER(r.room_name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(r.room_info) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(r.room_type) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Room> findByRoom_nameContainingIgnoreCaseOrRoom_infoContainingIgnoreCaseOrRoom_typeContainingIgnoreCase(@Param("keyword") String keyword, Pageable pageable);
}