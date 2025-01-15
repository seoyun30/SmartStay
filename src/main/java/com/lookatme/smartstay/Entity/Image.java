package com.lookatme.smartstay.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long image_id; //이미지 번호

    @Column(nullable = false)
    private String imageName; //UUID + 이미지 원본 이름

    @Column(nullable = false)
    private String originName; //이미지 원본 이름

    @Column(nullable = false)
    private String imageUrl; //이미지 경로

    @Column(nullable = false)
    private String repimgYn; //대표 이미지 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_num")
    private Room room; //룸 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_num")
    private RoomService	roomService; //룸 서비스 이미지

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rev_num")
    private Review review; //후기 글 번호

}
