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

    @Query("select r from Room r where r.hotel.hotel_num = :hotel_num and r.room_name like %:room_name%")
    List<Room> findByHotelNumAndRoom_nameContainingIgnoreCase(@Param("hotel_num") Long hotel_num, @Param("room_name") String room_name, Sort sort);

    @Query("select r from Room r where r.hotel.hotel_num = :hotel_num and r.room_info like %:room_info%")
    List<Room> findByHotelNumAndRoom_infoContainingIgnoreCase(@Param("hotel_num") Long hotel_num, @Param("room_info") String room_info, Sort sort);
}