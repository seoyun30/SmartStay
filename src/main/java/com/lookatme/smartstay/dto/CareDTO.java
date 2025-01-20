package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.entity.BaseEntity;
import com.lookatme.smartstay.entity.Chief;
import com.lookatme.smartstay.entity.Manager;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CareDTO extends BaseEntity {

    private Long care_num; //케어 번호

    private String care_name; //케어 명
    private String care_info; //케어 정보
    private String care_detail; //케어 상세
    private Long care_price; //케어 가격

    private Chief chief; //호텔(총판)

    private Manager manager; //호텔(매장)

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;
}
