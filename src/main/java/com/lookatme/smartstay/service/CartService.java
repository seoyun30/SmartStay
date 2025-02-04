package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.CartOrderDTO;
import com.lookatme.smartstay.dto.RoomItemDTO;
import com.lookatme.smartstay.entity.Cart;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.Room;
import com.lookatme.smartstay.entity.RoomItem;
import com.lookatme.smartstay.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final RoomReserveRepository roomReserveRepository;
    private final OrderReserveRepository orderReserveRepository;
    private final PayRepository payRepository;
    private final RoomItemRepository roomItemRepository;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;

    //룸예약 장바구니 등록
    public Long addCartRoomItem(RoomItemDTO roomItemDTO, String email) {

        log.info("장바구니 서비스 email : " + email);
        log.info("장바구니 서비스 roomItemDTO : " + roomItemDTO);

        Member member = memberRepository.findByEmail(email);
        log.info("장바구니 서비스 member : " + member);

        Room room = roomRepository.findById(roomItemDTO.getRoom_num())
                .orElseThrow(EntityNotFoundException::new);
        log.info("장바구니 서비스 room : " + room);

        Cart cart = cartRepository.findByMemberEmail(email);

        if (cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        RoomItem savedRoomItem =
                roomItemRepository.findByCart_Cart_numAndRoom_num(cart.getCart_num(), room.getRoom_num());

        //장바구니가 이미 있다면
        if (savedRoomItem != null) {
            return savedRoomItem.getRoomitem_num();
        } else {
            RoomItem roomItem =
                    RoomItem.createRoomItem(cart, room, roomItemDTO.getIn_date(),
                            roomItemDTO.getOut_date(), roomItemDTO.getReserve_request(), roomItemDTO.getCount()
                            );

            roomItemRepository.save(roomItem);

            return roomItem.getRoomitem_num();
        }

    }

    //룸예약 장바구니 조회
    public List<RoomItemDTO> getCartRoomItemList (String email) {
        List<RoomItemDTO> roomItemDTOList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);

        Cart cart = cartRepository.findByMemberEmail(email);

        if (cart == null) {
            return roomItemDTOList;
        }

        List<RoomItem> roomItemList = roomItemRepository.findByCart_Cart_num(cart.getCart_num());
        roomItemDTOList = roomItemList.stream()
                .map(roomItem -> modelMapper.map(roomItem, RoomItemDTO.class))
                .collect(Collectors.toList());

        return roomItemDTOList;
    }

    //자신의 룸예약이 맞는지 확인
    public boolean validateCartRoomItem(Long roomitem_num, String email) {
        log.info("서비스 roomitem_num : " + roomitem_num);
        log.info("서비스 email : " + email);

        Member member = memberRepository.findByEmail(email);
        RoomItem roomItem = roomItemRepository.findById(roomitem_num).orElseThrow(EntityNotFoundException::new);

        if (member != null && roomItem != null) {

            if ( !member.getEmail().equals(roomItem.getCart().getMember().getEmail()) ) {
                return false; //다르다
            }
        }

        return true; //같다
    }

    //장바구니의 룸예약 삭제
    public void deleteCartRoomItem(Long roomitem_num) {

        RoomItem roomItem = roomItemRepository.findById(roomitem_num).orElseThrow(EntityNotFoundException::new);

        roomItemRepository.delete(roomItem);

    }

    //차후 룸서비스 관련 추가 필요

    //장바구니의 주문
    public Long orderCartItem(List<CartOrderDTO> cartOrderDTOList, String email) {

        //수정 필요
        return null;
    }

}
