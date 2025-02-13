package com.lookatme.smartstay.entity;

import com.lookatme.smartstay.constant.CheckState;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoomReserve extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reserve_num; //룸 예약 번호

    @Column(unique = true, nullable = false)
    private String reserve_id; //예약 기록 번호

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckState check_state; //예약 상태 여부

    @OneToMany(mappedBy = "roomReserve", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RoomReserveItem> roomReserveItemList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num")
    private Member member; //회원

    public void setRoomItemList(RoomReserveItem roomReserveItem) {
        this.roomReserveItemList.add(roomReserveItem);
    }

    public void setRoomItemList(List<RoomReserveItem> roomReserveItemList) {
        this.roomReserveItemList = roomReserveItemList;
    }

}
