package com.lookatme.smartstay.service;

import com.lookatme.smartstay.constant.CheckState;
import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.RoomReserve;
import com.lookatme.smartstay.entity.RoomReserveItem;
import com.lookatme.smartstay.repository.MemberRepository;
import com.lookatme.smartstay.repository.RoomRepository;
import com.lookatme.smartstay.repository.RoomReserveItemRepository;
import com.lookatme.smartstay.repository.RoomReserveRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.time.temporal.ChronoUnit;
import java.util.*;
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
    public boolean validateroomResereve(Long reserve_num, String email) {

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

    //룸에 있는 룸예약 전체 조회
    public List<RoomReserveItemDTO> findRoomReserve(Long room_num) {

        List<RoomReserveItem> roomReserveItemList = roomReserveItemRepository.findByRoomRoom_num(room_num);
        List<RoomReserveItemDTO> roomReserveItemDTOList = roomReserveItemList.stream()
                .map(roomReserveItem -> modelMapper.map(roomReserveItem, RoomReserveItemDTO.class)
                        .setRoomDTO(modelMapper.map(roomReserveItem.getRoom(), RoomDTO.class))
                        .setRoomReserveDTO(modelMapper.map(roomReserveItem.getRoomReserve(), RoomReserveDTO.class)
                                .setMemberDTO(modelMapper.map(roomReserveItem.getRoomReserve().getMember(), MemberDTO.class)))
                )
                .collect(Collectors.toList());

        return roomReserveItemDTOList;
    }

    //회원의 룸예약 전체 조회
    public List<RoomReserveItemDTO> findMyRoomReserve(String email) {

        List<RoomReserveItem> roomReserveItemList = roomReserveItemRepository.findByEmail(email);
        List<RoomReserveItemDTO> roomReserveItemDTOList = roomReserveItemList.stream()
                .map(roomReserveItem -> modelMapper.map(roomReserveItem, RoomReserveItemDTO.class)
                        .setRoomDTO(modelMapper.map(roomReserveItem.getRoom(), RoomDTO.class)
                                .setHotelDTO(modelMapper.map(roomReserveItem.getRoom().getHotel(),HotelDTO.class)))
                        .setRoomReserveDTO(modelMapper.map(roomReserveItem.getRoomReserve(), RoomReserveDTO.class)
                                .setMemberDTO(modelMapper.map(roomReserveItem.getRoomReserve().getMember(), MemberDTO.class)))
                )
                .collect(Collectors.toList());

        return roomReserveItemDTOList;
    }

    //회원의 룸예약 페이징처리 전체 조회
    public PageResponseDTO<RoomReserveItemDTO> findMyRoomReservePage (String email, PageRequestDTO pageRequestDTO) {

        Pageable pageable = pageRequestDTO.getPageable("roomreserveitem_num");

        Page<RoomReserveItem> result = roomReserveItemRepository.findByEmailPage(email, pageable);

        List<RoomReserveItemDTO> roomReserveItemDTOList = result.stream()
                .map(roomReserveItem -> modelMapper.map(roomReserveItem, RoomReserveItemDTO.class)
                        .setRoomDTO(modelMapper.map(roomReserveItem.getRoom(), RoomDTO.class)
                                .setHotelDTO(modelMapper.map(roomReserveItem.getRoom().getHotel(),HotelDTO.class)))
                        .setRoomReserveDTO(modelMapper.map(roomReserveItem.getRoomReserve(), RoomReserveDTO.class)
                                .setMemberDTO(modelMapper.map(roomReserveItem.getRoomReserve().getMember(), MemberDTO.class)))
                        .setPayDTO(modelMapper.map(roomReserveItem.getPay(), PayDTO.class))
                ).collect(Collectors.toList());

        if (roomReserveItemDTOList.isEmpty()) {
            roomReserveItemDTOList = Collections.emptyList();
        }

        PageResponseDTO<RoomReserveItemDTO> reserveItemDTOPageResponseDTO = PageResponseDTO.<RoomReserveItemDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(roomReserveItemDTOList)
                .total((int) result.getTotalElements())
                .build();

        return reserveItemDTOPageResponseDTO;
    }

    //관리자 전체보기
    //회원의 룸예약 페이징처리 전체 조회
    public PageResponseDTO<RoomReserveItemDTO> findRoomReservePage (String email, PageRequestDTO pageRequestDTO) {

        Pageable pageable = pageRequestDTO.getPageable("roomreserveitem_num");

        Member member = memberRepository.findByEmail(email);

        Page<RoomReserveItem> result = roomReserveItemRepository.findByHotelPage(member.getHotel().getHotel_num(), pageable);

        List<RoomReserveItemDTO> roomReserveItemDTOList = result.stream()
                .map(roomReserveItem -> modelMapper.map(roomReserveItem, RoomReserveItemDTO.class)
                        .setRoomDTO(modelMapper.map(roomReserveItem.getRoom(), RoomDTO.class)
                                .setHotelDTO(modelMapper.map(roomReserveItem.getRoom().getHotel(),HotelDTO.class)))
                        .setRoomReserveDTO(modelMapper.map(roomReserveItem.getRoomReserve(), RoomReserveDTO.class)
                                .setMemberDTO(modelMapper.map(roomReserveItem.getRoomReserve().getMember(), MemberDTO.class)))
                        .setPayDTO(modelMapper.map(roomReserveItem.getPay(), PayDTO.class))
                ).collect(Collectors.toList());

        if (roomReserveItemDTOList.isEmpty()) {
            roomReserveItemDTOList = Collections.emptyList();
        }

        PageResponseDTO<RoomReserveItemDTO> reserveItemDTOPageResponseDTO = PageResponseDTO.<RoomReserveItemDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(roomReserveItemDTOList)
                .total((int) result.getTotalElements())
                .build();

        return reserveItemDTOPageResponseDTO;
    }

    //회원 룸예약 상세보기
    public RoomReserveItemDTO findRoomReserveItem(Long roomreserveitem_num, String email) {
        RoomReserveItem roomReserveItem = roomReserveItemRepository.findByReserveItemNumAndEmail(roomreserveitem_num, email);
        RoomReserveItemDTO roomReserveItemDTO = modelMapper.map(roomReserveItem, RoomReserveItemDTO.class)
                .setRoomDTO(modelMapper.map(roomReserveItem.getRoom(), RoomDTO.class)
                        .setHotelDTO(modelMapper.map(roomReserveItem.getRoom().getHotel(),HotelDTO.class)))
                .setRoomReserveDTO(modelMapper.map(roomReserveItem.getRoomReserve(), RoomReserveDTO.class)
                        .setMemberDTO(modelMapper.map(roomReserveItem.getRoomReserve().getMember(), MemberDTO.class)))
                .setPayDTO(modelMapper.map(roomReserveItem.getPay(), PayDTO.class));
        return roomReserveItemDTO;
    }

    //관리자 룸예약 상세보기
    public RoomReserveItemDTO findRoomReserveItemAd (Long roomreserveitem_num) {
        RoomReserveItem roomReserveItem = roomReserveItemRepository.findById(roomreserveitem_num)
                .orElseThrow(EntityNotFoundException::new);
        RoomReserveItemDTO roomReserveItemDTO = modelMapper.map(roomReserveItem, RoomReserveItemDTO.class)
                .setRoomDTO(modelMapper.map(roomReserveItem.getRoom(), RoomDTO.class)
                        .setHotelDTO(modelMapper.map(roomReserveItem.getRoom().getHotel(),HotelDTO.class)))
                .setRoomReserveDTO(modelMapper.map(roomReserveItem.getRoomReserve(), RoomReserveDTO.class)
                        .setMemberDTO(modelMapper.map(roomReserveItem.getRoomReserve().getMember(), MemberDTO.class)))
                .setPayDTO(modelMapper.map(roomReserveItem.getPay(), PayDTO.class));
        return roomReserveItemDTO;
    }

    //주문 상태 변경
    public RoomReserveDTO stateChange(RoomReserveDTO roomReserveDTO) {
        RoomReserve roomReserve = roomReserveRepository.findById(roomReserveDTO.getReserve_num())
                .orElseThrow(EntityNotFoundException::new);

        roomReserve.setCheck_state(roomReserveDTO.getCheck_state());

        roomReserve = roomReserveRepository.save(roomReserve);
        return modelMapper.map(roomReserve, RoomReserveDTO.class);
    }

    /* 예약된 날짜 목록 조회 메서드 */
    public List<Map<String, String>> getReserveDatesByRoom(Long room_num) {
        log.info("들어온 방번호: " + room_num);
        List<RoomReserveItem> roomReserveItemList =
                roomReserveItemRepository.findRoomNotReserve(room_num,
                        CheckState.IN, CheckState.RESERVE);

        roomReserveItemList.forEach(roomReserveItem -> log.info(roomReserveItem));

        return roomReserveItemList.stream().map(roomReserveItem -> {
            Map<String, String> map = new HashMap<>();
            map.put("in_date", roomReserveItem.getIn_date().truncatedTo(ChronoUnit.MINUTES).toString()); // 초 제거
            map.put("out_date", roomReserveItem.getOut_date().truncatedTo(ChronoUnit.MINUTES).toString());
            return map;
        }).collect(Collectors.toList());
    }

}
