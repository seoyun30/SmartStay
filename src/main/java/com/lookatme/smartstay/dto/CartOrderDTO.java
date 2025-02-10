package com.lookatme.smartstay.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CartOrderDTO {

    private Long[] roomitem_num; //룸 아이템 기록 번호

    private Long[] service_num; //서비스 주문 번호

//    private List<CartOrderDTO> cartOrderDTOList;
    //장바구니에서 여러개의 상품을 주문하므로 자기자신을 List로

}
