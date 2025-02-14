package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Hotel;
import com.lookatme.smartstay.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query("select m from Menu m where m.hotel = :hotel")
    Page<Menu> findByHotel(@Param("hotel") Hotel hotel, Pageable pageable);

    @Query("select m from Menu m where m.menu_name like %:query%")
    List<Menu> findByMenu_nameContaining(@Param("query") String query);

    @Query("select m from Menu m")
    List<Menu> findAllMenus();
}
