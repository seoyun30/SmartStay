package com.lookatme.smartstay.service;

import com.lookatme.smartstay.constant.CheckState;
import com.lookatme.smartstay.dto.RoomDTO;
import com.lookatme.smartstay.dto.RoomItemDTO;
import com.lookatme.smartstay.dto.RoomReserveItemDTO;
import com.lookatme.smartstay.entity.*;
import com.lookatme.smartstay.repository.MemberRepository;
import com.lookatme.smartstay.repository.RoomRepository;
import com.lookatme.smartstay.repository.RoomReserveItemRepository;
import com.lookatme.smartstay.repository.RoomReserveRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class RoomReserveService {

    private final RoomReserveRepository roomReserveRepository;
    private final RoomReserveItemRepository roomReserveItemRepository;
    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    //내 주문이 맞는지 확인
    public boolean validateOrder(Long reserve_num, String email) {

        Member member = memberRepository.findByEmail(email);
        RoomReserve roomReserve =
                roomReserveRepository.findById(reserve_num)
                        .orElseThrow(EntityNotFoundException::new);

        //주문id의 회원 email과 현재 로그인한 사람 정보 비교
        if (!StringUtils.equals(member.getEmail(), roomReserve.getMember().getEmail())) {
            return false;
        }

        return true;

    }

    public  List<RoomReserveItemDTO> findRoomReserve(Long room_num) {

        List<RoomReserveItem> roomReserveItemList = roomReserveItemRepository.findByRoomRoom_num(room_num);
        List<RoomReserveItemDTO> roomReserveItemDTOList = roomReserveItemList.stream()
                .map(roomReserveItem -> modelMapper.map(roomReserveItem, RoomReserveItemDTO.class)
                        .setRoomDTO(modelMapper.map(roomReserveItem.getRoom(), RoomDTO.class)))
                .collect(Collectors.toList());

        return roomReserveItemDTOList;
    }

    //주문 진행
    public Long order (RoomItemDTO roomItemDTO, String email) {
        Room room = roomRepository.findById(roomItemDTO.getRoom_num())
                .orElseThrow(EntityNotFoundException::new);

        Member member = memberRepository.findByEmail(email);

        //예약인원수는 room의 최대인원수보다 작거나 같아야 함
        //방상태가 예약 가능 상태일 때에만 가능
        //차후 예약날짜가 다른 경우, 그 방의 예약이 가능하도록 구현
        if (room.getRoom_bed() >= roomItemDTO.getCount()
                && room.getRoom_state().name().equals("YES")) {

            RoomItem roomItem = RoomItem.builder()
                    .room(room)
                    .count(roomItemDTO.getCount())
                    .in_date(roomItemDTO.getIn_date())
                    .out_date(roomItemDTO.getOut_date())
                    .reserve_request(roomItemDTO.getReserve_request())
                    .build();

            RoomReserve roomReserve = new RoomReserve();
            roomReserve.setMember(member);

            String date = LocalDate.now().toString().replace("-","");
            String num = room.getRoom_num().toString();
            String reserveId = date + "-" + num + "-" + member.getMember_num().toString();
            roomReserve.setReserve_id(reserveId); //오늘날짜-방번호-회원번호

            //roomReserve.setRoomItemList(roomItem);
            roomReserve.setCheck_state(CheckState.RESERVE);
            roomReserve.setReg_date(LocalDateTime.now());

            //roomItem에도 RoomReserve를 set하여 양방향 등록, pk 자동 참조
            roomItem.setRoomReserve(roomReserve);

            roomReserve = roomReserveRepository.save(roomReserve);

            return roomReserve.getReserve_num();

        } else {
            return null;
        }
    }
}
