package com.lookatme.smartstay.service;

import com.lookatme.smartstay.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final RoomReserveItemRepository roomReserveItemRepository;
    private final MenuRepository menuRepository;
    private final MenuItemRepository MenuItemRepository;
    private final CareRepository careRepository;
    private final CareItemRepository careItemRepository;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;

//    public OrderItem createOrder(OrderItemDTO orderItemDTO) {
//        // 예약된 방 정보 가져오기
//        RoomReserveItem roomReserveItem = roomReserveItemRepository.findById(orderItemDTO.getRoomReserveItemDTO().getRoomreserveitem_num())
//                .orElseThrow(() -> new IllegalArgumentException("해당 룸 예약 정보가 존재하지 않습니다."));
//
//        // 체크인 상태인지 확인
//        if (!roomReserveItem.getRoomReserve().getCheck_state().equals(CheckState.IN)) {
//            throw new IllegalStateException("체크인된 방에서만 룸서비스를 주문할 수 있습니다.");
//        }
//
//        // 메뉴 정보 가져오기
//        Menu menu = menuRepository.findById(orderItemDTO.getMenuDTO().getMenu_num())
//                .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 존재하지 않습니다."));
//
//        // 케어 서비스 가져오기 (선택 사항)
//        Care care = null;
//        if (orderItemDTO.getCareDTO().getCare_num() != null) {
//            care = careRepository.findById(orderItemDTO.getCareDTO().getCare_num())
//                    .orElseThrow(() -> new IllegalArgumentException("해당 케어 서비스가 존재하지 않습니다."));
//        }
//
//        // 주문 엔티티 생성
//        OrderItem orderItem = new OrderItem();
//        orderItem.setRoomReserveItem(roomReserveItem); // 방 정보 설정
////        orderItem.setMenu(menu);
////        orderItem.setMenu_count(orderItemDTO.getMenu_count());
////        orderItem.setCare(care);
////        orderItem.setCare_count(orderItemDTO.getCare_count());
//        orderItem.setMenu_request(orderItemDTO.getMenu_request());
//
//        // 주문 저장
//        return orderItemRepository.save(orderItem);
//    }
}
