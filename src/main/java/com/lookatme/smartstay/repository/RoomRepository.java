package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Hotel;
import com.lookatme.smartstay.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r WHERE r.hotel = :hotel")
    Page<Room> findByHotel(@Param("hotel") Hotel hotel, Pageable pageable);
}