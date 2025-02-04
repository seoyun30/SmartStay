package com.lookatme.smartstay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "roomReserve")
@Builder
public class RoomItem extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomitem_num; //룸 아이템 기록 번호

    private String in_date; //체크인 날짜

    private String out_date; //체크아웃 날짜

    private Long day; //n박

    private Long count; //예약 인원수

    private String reserve_request; //요청 사항

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_num")
    private Room room; //룸

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserve_num")
    private RoomReserve roomReserve;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pay_num")
    private Pay pay;

    @ManyToOne
    @JoinColumn(name = "cart_num")
    private Cart cart; //장바구니

    public static RoomItem createRoomItem(Cart cart, Room room, String in_date
            , String out_date, String reserve_request, Long count) {
        RoomItem roomItem = new RoomItem();
        roomItem.setCart(cart);
        roomItem.setRoom(room);
        roomItem.setIn_date(in_date);
        roomItem.setOut_date(out_date);
        roomItem.setReserve_request(reserve_request);
        roomItem.setCount(count);

        return roomItem;
    }

}
