package com.lookatme.smartstay.Entity;

import com.lookatme.smartstay.Entity.Member.Chief;
import com.lookatme.smartstay.Entity.Member.Manager;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CareService extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long care_num; //케어 번호

    private String care_name; //케어 명
    private String care_info; //케어 정보
    private String care_detail; //케어 상세
    private Long care_price; //케어 가격

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chief_num")
    private Chief chief; //호텔(총판)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_num")
    private Manager manager; //호텔(매장)
}
