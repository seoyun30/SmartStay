package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Query("select h from Hotel h where h.create_by = :email")
    List<Hotel> findByEmail(String email);

    @Query("select h from Hotel h where h.hotel_name like %:query% or h.address like %:query")
    List<Hotel> findByHotel_nameOrAddressContaining(@Param("query") String query);

    @Query("select h from Hotel h where h.member.email = :email")
    Optional<Hotel> findHotelByMemberEmail(@Param("email") String email);
}
