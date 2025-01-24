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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2

public class RoomService {

    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final ImageService imageService;

    public Long roomRegister(RoomDTO roomDTO) throws Exception {

        Room room = modelMapper.map(roomDTO, Room.class);

        room = roomRepository.save(room);

        return room.getRoom_num();
    }

    public RoomDTO roomRead(Long room_num) {

        Room room = roomRepository.findById(room_num).orElseThrow(EntityNotFoundException::new);

        RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);

        return roomDTO;
    }

    public RoomDTO roomRead(Long room_num, String email) {

        Room room = roomRepository.findById(room_num).orElseThrow(EntityNotFoundException::new);

        RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);

        return roomDTO;
    }

    public List<RoomDTO> list () {
       List<Room> roomList = roomRepository.findAll();
        List<RoomDTO> roomDTOList =
                roomList.stream()
                        .map(room -> modelMapper.map(room, RoomDTO.class))
                        .collect(Collectors.toList());

        return roomDTOList;
    }

    public PageResponseDTO<RoomDTO> roomList(PageRequestDTO pageRequestDTO) {

        Pageable pageable = pageRequestDTO.getPageable("room_num");

        Page<Room> result = roomRepository.findAll(pageable);
        log.info(result);

        List<RoomDTO> roomDTOList = result.stream()
                .map(room -> modelMapper.map(room, RoomDTO.class))
                .collect(Collectors.toList());

        PageResponseDTO<RoomDTO> roomDTOPageResponseDTO =
                PageResponseDTO.<RoomDTO>withAll().pageRequestDTO(pageRequestDTO)
                        .dtoList(roomDTOList).total((int)result.getTotalElements()).build();

        return roomDTOPageResponseDTO;
    }


    public RoomDTO roomModify(RoomDTO roomDTO){

        Room room = roomRepository.findById(roomDTO.getRoom_num()).orElseThrow(EntityNotFoundException::new);

        room.setRoom_name(roomDTO.getRoom_name());
        room.setRoom_info(roomDTO.getRoom_info());
        room.setRoom_type(roomDTO.getRoom_type());
        room.setRoom_bed(roomDTO.getRoom_bed());
        room.setRoom_price(roomDTO.getRoom_price());
        room.setRoom_state(roomDTO.getRoom_state());

        room = roomRepository.save(room);

        return modelMapper.map(room, RoomDTO.class);
    }

    public void roomDelete(Long id) {

        roomRepository.deleteById(id);
    }
}
