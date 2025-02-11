package com.lookatme.smartstay.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PrePayDTO {

    private String merchant_uid; //주문 고유 번호

    private BigDecimal amount; //총 결제 금액

}
