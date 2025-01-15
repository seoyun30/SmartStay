package com.lookatme.smartstay.Entity.Room;

import com.lookatme.smartstay.Constant.RoomState;
import com.lookatme.smartstay.Entity.BaseEntity;
import com.lookatme.smartstay.Entity.Chief;
import com.lookatme.smartstay.Entity.Manager;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long room_num; //룸 번호

    private String room_name; //룸 명
    private String room_info; //룸 정보
    private String room_type; //룸 타입
    private Long room_bed; //룸 인원수
    private Long room_price; //룸 가격

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomState room_state; //룸 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chief_num")
    private Chief chief; //호텔(총판)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_num")
    private Manager manager; //호텔(매장)
}
