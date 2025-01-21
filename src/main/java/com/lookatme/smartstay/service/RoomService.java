package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.dto.PageResponseDTO;
import com.lookatme.smartstay.dto.RoomDTO;
import com.lookatme.smartstay.entity.Room;
import com.lookatme.smartstay.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2

public class RoomService {

    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final ImageService imageService;

    public Long roomRegister(RoomDTO roomDTO, List<MultipartFile> multipartFileList) throws Exception {

        Room room = modelMapper.map(roomDTO, Room.class);

        room = roomRepository.save(room);

        imageService.saveImage(multipartFileList,"room",room.getRoom_num());

        return room.getRoom_num();
    }

    public RoomDTO roomRead(Long room_num){

        Room room = roomRepository.findById(room_num).orElseThrow(EntityNotFoundException::new);

        RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);

        return roomDTO;
    }

    public RoomDTO roomRead(Long room_num, String email){

        Room room = roomRepository.findById(room_num).orElseThrow(EntityNotFoundException::new);

        RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);

        return roomDTO;
    }

    public PageResponseDTO<RoomDTO> roomList(PageRequestDTO pageRequestDTO) {

        Pageable pageable = pageRequestDTO.getPageable("room_num");

        PageResponseDTO<RoomDTO> roomDTOPageResponseDTO =
                PageResponseDTO.<RoomDTO>withAll().pageRequestDTO(pageRequestDTO)
                        .build();

        return roomDTOPageResponseDTO;

    }


    public RoomDTO roomModify(RoomDTO roomDTO, Long room_num, List<MultipartFile> multipartFileList){

        Room room = roomRepository.findById(roomDTO.getRoom_num()).orElseThrow(EntityNotFoundException::new);

        room.setRoom_name(roomDTO.getRoom_name());
        room.setRoom_info(room.getRoom_info());
        room.setRoom_type(room.getRoom_type());
        room.setRoom_bed(room.getRoom_bed());
        room.setRoom_price(room.getRoom_price());
        room.setRoom_state(room.getRoom_state());

        return null;
    }
}
