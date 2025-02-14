package com.lookatme.smartstay.entity;

import com.lookatme.smartstay.constant.RoomState;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Room extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long room_num; //룸 번호

    @Column(nullable = false)
    private String room_name; //룸 명

    @Column(nullable = false)
    private String room_info; //룸 정보

    @Column(nullable = false)
    private String room_type; //룸 타입

    @Column(nullable = false)
    private Long room_bed; //룸 인원수

    @Column(nullable = false)
    private Long room_price; //룸 가격

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomState room_state; //룸 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_num")
    private Hotel hotel; //호텔
}
