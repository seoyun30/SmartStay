package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.entity.Hotel;
import com.lookatme.smartstay.entity.Image;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.Room;
import com.lookatme.smartstay.repository.HotelRepository;
import com.lookatme.smartstay.repository.ImageRepository;
import com.lookatme.smartstay.repository.MemberRepository;
import com.lookatme.smartstay.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
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
    private final ImageRepository imageRepository;
    private final HotelRepository hotelRepository;
    private final MemberRepository memberRepository;

    public void roomRegister(RoomDTO roomDTO, Long hotel_num, List<MultipartFile> multipartFiles, Long mainImageIndex) throws Exception {

        Hotel hotel = hotelRepository.findById(hotel_num)
                .orElseThrow(() -> new IllegalArgumentException("호텔을 찾을 수 없습니다."));

        Room room = modelMapper.map(roomDTO, Room.class);
        room.setHotel(hotel);
        roomRepository.save(room);

        if (multipartFiles != null && !multipartFiles.isEmpty()) {

                imageService.saveImage(multipartFiles, "room", room.getRoom_num());
        }
    }


    public RoomDTO roomRead(Long room_num) {

        Room room = roomRepository.findById(room_num)
                .orElseThrow(() -> new EntityNotFoundException("Room not found with id " + room_num));

        RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);

        Hotel hotel = room.getHotel();
        if (hotel != null) {
            HotelDTO hotelDTO = modelMapper.map(hotel, HotelDTO.class);
            roomDTO.setHotelDTO(hotelDTO);
        }

        List<Image> imageList = imageRepository.findByTarget("room", room_num);

        if (imageList != null && !imageList.isEmpty()) {
            List<ImageDTO> imageDTOList = imageList.stream()
                    .map(image -> modelMapper.map(image, ImageDTO.class))
                    .collect(Collectors.toList());

            List<Long> imageIdList = imageDTOList.stream()
                    .map(ImageDTO::getImage_id)
                    .collect(Collectors.toList());

            roomDTO.setImageDTOList(imageDTOList);
            roomDTO.setImageIdList(imageIdList);
        }

        return roomDTO;
    }

    public PageResponseDTO<RoomDTO> getRoomsByHotel(HotelDTO hotelDTO, PageRequestDTO pageRequestDTO, String sortField, String sortDir) {

        Hotel hotel = modelMapper.map(hotelDTO, Hotel.class);

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), sort);

        Page<Room> result = roomRepository.findByHotel(hotel, pageable);
        log.info("PageResult: " + result);

        List<RoomDTO> roomDTOList = result.stream()
                .map(room -> {
                    RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);
                    return roomDTO;
                })
                .collect(Collectors.toList());

        if (roomDTOList == null) {
            roomDTOList = Collections.emptyList();
        }

        PageResponseDTO<RoomDTO> roomDTOPageResponseDTO =
                PageResponseDTO.<RoomDTO>withAll().pageRequestDTO(pageRequestDTO)
                        .dtoList(roomDTOList).total((int)result.getTotalElements()).build();

        log.info("PageResponseDTO: " + roomDTOPageResponseDTO);

        return roomDTOPageResponseDTO;
    }

    public void roomModify(RoomDTO roomDTO, List<MultipartFile> multipartFiles, HotelDTO hotel, List<Long> delnumList) throws Exception {

        log.info("룸 수정 시작 : {}", roomDTO);

        Room room = roomRepository.findById(roomDTO.getRoom_num())
                        .orElseThrow(() -> new EntityNotFoundException("해당 룸 정보를 찾을 수 없습니다."));

        if (!room.getHotel().getHotel_num().equals(hotel.getHotel_num())) {
            throw new SecurityException("권한이 없습니다.");
        }

        Hotel hotel1 = hotelRepository.findById(hotel.getHotel_num())
                .orElseThrow(() -> new EntityNotFoundException("해당 호텔 정보 찾을 수 없음."));
        room.setHotel(hotel1);

        room.setRoom_name(roomDTO.getRoom_name());
        room.setRoom_info(roomDTO.getRoom_info());
        room.setRoom_type(roomDTO.getRoom_type());
        room.setRoom_bed(roomDTO.getRoom_bed());
        room.setRoom_price(roomDTO.getRoom_price());
        room.setRoom_state(roomDTO.getRoom_state());

        roomRepository.save(room);

        boolean hasNewImages = multipartFiles != null && multipartFiles.stream().anyMatch(file -> !file.isEmpty());
        boolean hasDeletedImages = delnumList != null && !delnumList.isEmpty();

        if (hasNewImages || hasDeletedImages) {
            log.info("이미지 업데이트 실행");
            try {
                imageService.updateImage(
                        hasNewImages ? multipartFiles : null,
                        hasDeletedImages ? delnumList : null,
                        "room",
                        room.getRoom_num()
                );
            } catch (IndexOutOfBoundsException e) {
                log.error("이미지 업데이트 중 인덱스 오류 발생: {}", e.getMessage());
                throw new IllegalArgumentException("업로드된 파일이나 삭제 요청이 잘못되었습니다.");
            }
        } else {
            log.info("이미지 업데이트 없이 텍스트 정보만 수정");
        }
    }

    public void roomDelete(Long id) {

        List<Image> images = imageRepository.findByTarget("room", id);

        if (images == null || images.isEmpty()) {
            log.error("삭제하려는 룸의 이미지가 없습니다. room_num : " +id);
        }else {
            for (Image image : images) {
                try {
                    imageService.deleteImage(image.getImage_id());
                    imageRepository.deleteById(image.getImage_id());
                    log.info("삭제된 이미지 id : " + image.getImage_id());
                }catch (Exception e) {
                    log.error("이미지 삭제 실패 :" + e.getMessage());
                }
            }
        }
        roomRepository.deleteById(id);
        log.info("룸이 삭제되었습니다. room_num: " + id);
    }

    public PageResponseDTO<RoomDTO> searchList(String searchType, String searchKeyword, String sortField, String sortDir, PageRequestDTO pageRequestDTO) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Page<Room> result;

        switch (searchType) {
            case "keyword":
                result = roomRepository.findByRoom_nameContainingIgnoreCaseOrRoom_infoContainingIgnoreCaseOrRoom_typeContainingIgnoreCase(searchKeyword, PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), sort));
                break;
            case "roomName":
                result = roomRepository.findByRoom_nameContainingIgnoreCase(searchKeyword, PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), sort));
                break;
            case "roomInfo":
                result = roomRepository.findByRoom_infoContainingIgnoreCase(searchKeyword, PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), sort));
                break;
            case "roomType":
                result = roomRepository.findByRoom_typeContainingIgnoreCase(searchKeyword, PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), sort));
                break;
            default:
                result = roomRepository.findAll(PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), sort));
        }

        List<RoomDTO> roomDTOList = result.stream()
                .map(room -> modelMapper.map(room, RoomDTO.class)
                        .setHotelDTO(modelMapper.map(room.getHotel(), HotelDTO.class)))
                .collect(Collectors.toList());

        for (RoomDTO roomDTO : roomDTOList) {
            List<Image> roomImageList = imageRepository.findByTarget("room", roomDTO.getRoom_num());
            if (!roomImageList.isEmpty()) {
                List<ImageDTO> roomImageDTOList = roomImageList.stream()
                        .map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList());
                roomDTO.setImageDTOList(roomImageDTOList);
            } else {
                roomDTO.setImageDTOList(null);
            }
        }
            PageResponseDTO<RoomDTO> roomDTOPageResponseDTO = PageResponseDTO.<RoomDTO>withAll()
                    .pageRequestDTO(pageRequestDTO).dtoList(roomDTOList).total((int)result.getTotalElements()).build();

        return roomDTOPageResponseDTO;
    }

    public List<RoomDTO> searchRead(Long hotel_num) {

        Hotel hotel = hotelRepository.findById(hotel_num)
                .orElseThrow(()->new EntityNotFoundException("호텔을 찾을 수 없습니다."));

        List<Room> rooms = roomRepository.findByHotel(hotel);

        List<RoomDTO> roomDTOS = rooms.stream()
                .map(room -> {
                    RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);

                    List<Image> imageList = imageRepository.findByTarget("room", room.getRoom_num());
                    log.info("룸번호:{}, 조회된 이미지 개수:{}", room.getRoom_num(), imageList.size());
                    if (imageList != null && !imageList.isEmpty()) {
                        List<ImageDTO> imageDTOList = imageList.stream()
                                .map(image -> modelMapper.map(image, ImageDTO.class))
                                .collect(Collectors.toList());
                        roomDTO.setImageDTOList(imageDTOList);

                        ImageDTO mainImage = imageDTOList.stream()
                                .filter(image -> "Y".equalsIgnoreCase(image.getRepimg_yn()))
                                .findFirst()
                                .orElse(imageDTOList.get(0));
                        roomDTO.setMainImage(mainImage);
                    }else {
                        log.warn("룸번호:{}에 이미지가 없습니다.", room.getRoom_num());
                    }

                    return roomDTO;
                })
                .collect(Collectors.toList());

        return roomDTOS;
    }

    public List<RoomDTO> findmyRoom(String email) {
        Member member = memberRepository.findByEmail(email);

        List<Room> roomList = roomRepository.findByHotel(member.getHotel());
        List<RoomDTO> roomDTOList = roomList.stream().
                map(room -> modelMapper.map(room, RoomDTO.class)
                        .setHotelDTO(modelMapper.map(room.getHotel(), HotelDTO.class)))
                .collect(Collectors.toList());

        return roomDTOList;
    }

    public ImageDTO getRoomMainImage(Long room_num) {
        List<Image> imageList = imageRepository.findByTarget("room", room_num);
        if (imageList != null && !imageList.isEmpty()) {
            return modelMapper.map(imageList.get(0), ImageDTO.class);
        }
        return null;
    }
}
