package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.constant.CheckState;
import com.lookatme.smartstay.entity.RoomReserveItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomReserveItemRepository extends JpaRepository<RoomReserveItem, Long> {

    @Query("select r from RoomReserveItem r where r.roomReserve.member.email = :email")
    List<RoomReserveItem> findByEmail(String email);

    @Query("select r from RoomReserveItem r where r.roomReserve.member.email = :email")
    Page<RoomReserveItem> findByEmailPage (String email, Pageable pageable);
    
    @Query("select r from RoomReserveItem r where r.room.room_num = :room_num " +
            "and r.in_date <= current_date " +
            "and r.out_date >= current_date " +
            "and r.roomReserve.check_state <> 'CANCEL'")
    List<RoomReserveItem> findByRoomRoom_num(Long room_num);

    @Query("select r from RoomReserveItem r where r.room.room_num = :room_num " +
            "and (r.roomReserve.check_state in :excludedState1 " +
            "or r.roomReserve.check_state in :excludedState2)")
    List<RoomReserveItem> findRoomNotReserve(Long room_num, CheckState excludedState1, CheckState excludedState2);

    @Query("select r from RoomReserveItem r where r.roomreserveitem_num = :roomreserveitem_num and r.roomReserve.member.email = :email")
    RoomReserveItem findByReserveItemNumAndEmail(Long roomreserveitem_num, String email);

    @Query("SELECT COUNT(r) > 0 FROM RoomReserveItem r " +
            "WHERE r.room.room_num = :room_num " +
            "AND (r.in_date < :out_date AND r.out_date > :in_date) ")
    boolean isRoomAlreadyReserve(Long room_num,
                                LocalDateTime in_date,
                                LocalDateTime out_date);
    @Query("SELECT r FROM RoomReserveItem r WHERE r.roomReserve.member.email = :email "
            + "AND (:reserve_id IS NULL OR r.roomReserve.reserve_id LIKE %:reserve_id%) "
            + "AND (:hotel_name IS NULL OR r.room.hotel.hotel_name LIKE %:hotel_name%) "
            + "AND (:room_name IS NULL OR r.room.room_name LIKE %:room_name%) "
            + "AND (:state IS NULL OR r.roomReserve.check_state = :state) "
            + "AND (:sdate IS NULL OR r.in_date >= :sdate) "
            + "AND (:edate IS NULL OR r.out_date <= :edate)")
    Page<RoomReserveItem> findMyRoomReserveBySearch(
            @Param("email") String email,
            @Param("reserve_id") String reserve_id,
            @Param("hotel_name") String hotel_name,
            @Param("room_name") String room_name,
            @Param("state") CheckState state,
            @Param("sdate") LocalDateTime sdate,
            @Param("edate") LocalDateTime edate,
            Pageable pageable);

    @Query("SELECT r FROM RoomReserveItem r WHERE r.room.hotel.hotel_num = :hotel_num "
            + "AND (:reserve_id IS NULL OR r.roomReserve.reserve_id LIKE %:reserve_id%) "
            + "AND (:room_name IS NULL OR r.room.room_name LIKE %:room_name%) "
            + "AND (:reserve_name IS NULL OR r.roomReserve.member.name LIKE %:reserve_name% )"
            + "AND (:state IS NULL OR r.roomReserve.check_state = :state) "
            + "AND (:sdate IS NULL OR r.in_date >= :sdate) "
            + "AND (:edate IS NULL OR r.out_date <= :edate)")
    Page<RoomReserveItem> findRoomReserveBySearch(
            @Param("hotel_num") Long hotel_num,
            @Param("reserve_id") String reserve_id,
            @Param("room_name") String room_name,
            @Param("reserve_name") String reserve_name,
            @Param("state") CheckState state,
            @Param("sdate") LocalDateTime sdate,
            @Param("edate") LocalDateTime edate,
            Pageable pageable);

}
