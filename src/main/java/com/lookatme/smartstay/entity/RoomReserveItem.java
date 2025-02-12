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
public class RoomReserveItem extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomreserveitem_num; //룸 아이템 기록 번호

    private LocalDateTime in_date; //체크인 날짜

    private LocalDateTime out_date; //체크아웃 날짜

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

    public static RoomReserveItem createRoomItem(Room room, LocalDateTime in_date
            , LocalDateTime out_date, Long day, String reserve_request, Long count) {
        RoomReserveItem roomReserveItem = new RoomReserveItem();
        roomReserveItem.setRoom(room);
        roomReserveItem.setIn_date(in_date);
        roomReserveItem.setOut_date(out_date);
        roomReserveItem.setDay(day);
        roomReserveItem.setReserve_request(reserve_request);
        roomReserveItem.setCount(count);

        return roomReserveItem;
    }

}
