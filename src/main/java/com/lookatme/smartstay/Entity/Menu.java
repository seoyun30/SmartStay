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
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menu_num; //메뉴 번호
    private String menu_name; //메뉴 명
    private String service_info; //서비스 정보
    private String menu_detail; //메뉴 상세
    private Long menu_price; //메뉴 가격

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chief_num")
    private Chief chief; //호텔(총판)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_num")
    private Manager	manager; //호텔(매장)
}
