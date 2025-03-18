package com.lookatme.smartstay.entity;

import com.lookatme.smartstay.constant.ActiveState;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Hotel extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hotel_num;

    @Column(unique = true, nullable = false)
    private String business_num;

    @Column(nullable = false)
    private String hotel_name;

    private String owner;

    private String address;

    private String tel;

    private String score;

    @Enumerated(EnumType.STRING)
    private ActiveState active_state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_num")
    @ToString.Exclude
    private Brand brand;

    private Long lowestPrice;
}
