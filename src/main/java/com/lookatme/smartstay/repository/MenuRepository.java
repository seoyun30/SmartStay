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

 /*   //한식 메뉴만
    @Query("select m from Menu m where m.hotel = :hotel and m.menu_sort = 'KOREAN'")
    Page<Menu> findKoreanMenu(@Param("hotel") Hotel hotel, Pageable pageable);

    //중식 메뉴만
    @Query("select m from Menu m where m.hotel = :hotel and m.menu_sort = 'CHINESE'")
    Page<Menu> findChineseMenu(@Param("hotel") Hotel hotel, Pageable pageable);

    //일식 메뉴만
    @Query("select m from Menu m where m.hotel = :hotel and m.menu_sort = 'JAPANESE'")
    Page<Menu> findJapaneseMenu(@Param("hotel") Hotel hotel, Pageable pageable);

    //양식 메뉴만
    @Query("select m from Menu m where m.hotel = :hotel and m.menu_sort = 'WESTERN'")
    Page<Menu> findWesternMenu(@Param("hotel") Hotel hotel, Pageable pageable);

    //분식 메뉴만
    @Query("select m from Menu m where m.hotel = :hotel and m.menu_sort = 'SNACK'")
    Page<Menu> findSnackMenu(@Param("hotel") Hotel hotel, Pageable pageable);

    //음료 메뉴만
    @Query("select m from Menu m where m.hotel = :hotel and m.menu_sort = 'DRINK'")
    Page<Menu> findDrinkMenu(@Param("hotel") Hotel hotel, Pageable pageable);

    //기타 메뉴만
    @Query("select m from Menu m where m.hotel = :hotel and m.menu_sort = 'ETC'")
    Page<Menu> findEtcMenu(@Param("hotel") Hotel hotel, Pageable pageable);
*/

}
