package com.lookatme.smartstay.entity;

import com.lookatme.smartstay.constant.ActiveState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Brand extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long brand_num;

    @Column(unique = true, nullable = false)
    private String business_num;

    @Column(nullable = false)
    private String brand_name;

    private String owner;

    private String tel;

    @Enumerated(EnumType.STRING)
    private ActiveState active_state;

}
