package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.HotelDTO;
import com.lookatme.smartstay.dto.ImageDTO;
import com.lookatme.smartstay.dto.RoomDTO;
import com.lookatme.smartstay.dto.RoomItemDTO;
import com.lookatme.smartstay.entity.Image;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.Room;
import com.lookatme.smartstay.entity.RoomItem;
import com.lookatme.smartstay.repository.ImageRepository;
import com.lookatme.smartstay.repository.MemberRepository;
import com.lookatme.smartstay.repository.RoomItemRepository;
import com.lookatme.smartstay.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class RoomItemService {

    private final RoomItemRepository roomItemRepository;
    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;

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
        roomItem.setDay(roomItemDTO.getDay());
        roomItem.setReserve_request(roomItemDTO.getReserve_request());

        return roomItem.getRoomitem_num();
    }

    public RoomItemDTO findRoomDTO (RoomItemDTO roomItemDTO) {

        Room room = roomRepository.findById(roomItemDTO.getRoom_num())
                .orElseThrow(EntityNotFoundException::new);
        RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);
        HotelDTO hotelDTO = modelMapper.map(room.getHotel(), HotelDTO.class);
        roomDTO.setHotelDTO(hotelDTO);
        roomItemDTO.setRoomDTO(roomDTO);

        List<Image> imageList = imageRepository.findByTarget("room", roomItemDTO.getRoom_num());

        if (imageList != null && !imageList.isEmpty()) {
            List<ImageDTO> imageDTOList = imageList.stream()
                    .map(image -> modelMapper.map(image, ImageDTO.class))
                    .collect(Collectors.toList());

            List<Long> imageIdList = imageDTOList.stream()
                    .map(ImageDTO::getImage_id)
                    .collect(Collectors.toList());

            roomItemDTO.setImageDTOList(imageDTOList);
            roomItemDTO.setImageIdList(imageIdList);
        }

        return roomItemDTO;
    }
}
