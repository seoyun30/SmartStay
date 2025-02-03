package com.lookatme.smartstay.repository;

import com.lookatme.smartstay.entity.Pay;
import com.lookatme.smartstay.entity.Room;
import com.lookatme.smartstay.entity.RoomItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomItemRepository extends JpaRepository<RoomItem, Long> {

    //내 장바구니에 담긴 아이템 찾기
    public RoomItem findByCartIdAndRoom_num (Long cart_num, Long room_num);

    public List<RoomItem> findByCartId (Long cart_id);

    //추후 룸서비스 버전도 생성 예정


}
