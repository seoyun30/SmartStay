package com.lookatme.smartstay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    private LocalDateTime in_date; //체크인 날짜

    private LocalDateTime out_date; //체크아웃 날짜

    private Long day; //n박

    private Long count; //예약 인원수

    private String reserve_request; //요청 사항

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "room_num")
    private Room room; //룸

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "reserve_num")
    private RoomReserve roomReserve;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "pay_num")
    private Pay pay;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_num")
    private Cart cart; //장바구니

    public static RoomItem createRoomItem(Cart cart, Room room, LocalDateTime in_date
            , LocalDateTime out_date, Long day, String reserve_request, Long count) {
        RoomItem roomItem = new RoomItem();
        roomItem.setCart(cart);
        roomItem.setRoom(room);
        roomItem.setIn_date(in_date);
        roomItem.setOut_date(out_date);
        roomItem.setDay(day);
        roomItem.setReserve_request(reserve_request);
        roomItem.setCount(count);

        return roomItem;
    }

}
