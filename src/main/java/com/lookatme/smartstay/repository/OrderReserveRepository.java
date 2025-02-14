package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.OrderReserve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderReserveRepository extends JpaRepository<OrderReserve, Long> {

    @Query("SELECT o FROM OrderReserve o " +
            "JOIN FETCH o.member m " +
            "JOIN FETCH o.menu mn " +
            "JOIN FETCH o.care c " +
            "WHERE m.email = :email")
    List<OrderReserve> findOrdersByMemberEmail(@Param("email") String email);
}
