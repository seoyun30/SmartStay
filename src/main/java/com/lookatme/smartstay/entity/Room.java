package com.lookatme.smartstay.entity;

import com.lookatme.smartstay.constant.RoomState;
import com.lookatme.smartstay.constant.RoomType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalTime;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType room_type; //룸 타입

    @Column(nullable = false)
    @Min(value = 2, message = "룸 최소 인원은 2인입니다.")
    private Long room_bed; //룸 인원수

    @Column(nullable = false)
    private Long room_price; //룸 가격

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomState room_state; //룸 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_num")
    @ToString.Exclude
    private Hotel hotel; //호텔

    @Column(nullable = false)
    private LocalTime in_time = LocalTime.of(15, 0);  // 체크인 시간: 기본값 오후 3시

    @Column(nullable = false)
    private LocalTime out_time = LocalTime.of(11, 0); // 체크아웃 시간: 기본값 오전 11시

}
