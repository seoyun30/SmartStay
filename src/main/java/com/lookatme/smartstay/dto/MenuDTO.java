package com.lookatme.smartstay.dto;

import com.lookatme.smartstay.entity.BaseEntity;
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
public class MenuDTO extends BaseEntity {

    private Long menu_num; //메뉴 번호
    private String menu_name; //메뉴 명
    private String service_info; //서비스 정보
    private String menu_detail; //메뉴 상세
    private Long menu_price; //메뉴 가격

    private Manager manager; //호텔(매장)

    private LocalDateTime reg_date;

    private LocalDateTime modi_date;

    private String create_by;

    private String modified_by;
}
