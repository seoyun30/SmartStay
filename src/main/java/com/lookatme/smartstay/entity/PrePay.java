package com.lookatme.smartstay.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PrePay {

    @Id
    @Column(nullable = false)
    private String merchant_uid; //주문 고유 번호

    private BigDecimal amount; //총 결제 금액

}
