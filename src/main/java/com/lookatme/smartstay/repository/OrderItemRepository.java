package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("select o from OrderItem o where o.cart.cart_num = :cart_num")
    List<OrderItem> findByCart_Cart_num (Long cart_num);

}
