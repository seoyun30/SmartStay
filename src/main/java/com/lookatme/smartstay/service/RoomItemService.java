package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.RoomItemDTO;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.RoomItem;
import com.lookatme.smartstay.repository.MemberRepository;
import com.lookatme.smartstay.repository.RoomItemRepository;
import com.lookatme.smartstay.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class RoomItemService {

    private final RoomItemRepository roomItemRepository;
    private final MemberRepository memberRepository;

    //장바구니에 담긴 인원 수 수정, 날짜 수정, 요청사항 수정
    public Long updateRoomItem(RoomItemDTO roomItemDTO, String email) {
        //내 장바구니 확인
        Member member = memberRepository.findByEmail(email);

        RoomItem roomItem = roomItemRepository.findById(roomItemDTO.getRoomitem_num())
                .orElseThrow(EntityNotFoundException::new);

        //회원정보가 다를시 예외처리
        if (roomItem.getCart() != null && member != null && !roomItem.getCart().getMember().getEmail().equals(member.getEmail())) {
            throw new EntityNotFoundException();
        }

        if (roomItem.getRoom().getRoom_bed() >= roomItemDTO.getCount()) {
            roomItem.setCount(roomItemDTO.getCount());
        }
        roomItem.setIn_date(roomItemDTO.getIn_date());
        roomItem.setOut_date(roomItemDTO.getOut_date());
        roomItem.setReserve_request(roomItemDTO.getReserve_request());

        return roomItem.getRoomitem_num();
    }
}
