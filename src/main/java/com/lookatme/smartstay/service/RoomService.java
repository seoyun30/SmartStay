package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.entity.Hotel;
import com.lookatme.smartstay.entity.Image;
import com.lookatme.smartstay.entity.Room;
import com.lookatme.smartstay.repository.HotelRepository;
import com.lookatme.smartstay.repository.ImageRepository;
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
    private final ImageRepository imageRepository;
    private final HotelRepository hotelRepository;

    public void roomRegister(RoomDTO roomDTO, Long hotel_num, List<MultipartFile> multipartFiles, Long mainImageIndex) throws Exception {

        Hotel hotel = hotelRepository.findById(hotel_num)
                .orElseThrow(() -> new IllegalArgumentException("호텔을 찾을 수 없습니다."));

        Room room = modelMapper.map(roomDTO, Room.class);
        room.setHotel(hotel);
        roomRepository.save(room);

        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            if (mainImageIndex < 0 || mainImageIndex >= multipartFiles.size()) {
                throw new IllegalArgumentException("유효하지 않은 대표 이미지입니다.");
            }

            for (int i = 0; i < multipartFiles.size(); i++) {
                MultipartFile file = multipartFiles.get(i);
                Image imageEntity = new Image();
                imageEntity.setRoom(room);
                if (i == mainImageIndex) {
                    imageEntity.setRepimg_yn("Y");
                } else {
                    imageEntity.setRepimg_yn("N");
                }
                imageService.saveImage(List.of(file), "room", room.getRoom_num());
            }
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

    public PageResponseDTO<RoomDTO> getRoomsByHotel(Hotel hotel, PageRequestDTO pageRequestDTO) {

        Pageable pageable = pageRequestDTO.getPageable("room_num");
        log.info("Pageable created with room_num sort: {}", pageable);

        Page<Room> result = roomRepository.findByHotel(hotel, pageable);
        log.info("PageResult: " + result);

        List<RoomDTO> roomDTOList = result.stream()
                .map(room -> {
                    RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);
                    return roomDTO;
                })
                .collect(Collectors.toList());

        HotelDTO hotelDTO = modelMapper.map(hotel, HotelDTO.class);

        PageResponseDTO<RoomDTO> roomDTOPageResponseDTO =
                PageResponseDTO.<RoomDTO>withAll().pageRequestDTO(pageRequestDTO)
                        .dtoList(roomDTOList).total((int)result.getTotalElements()).build();

        log.info("PageResponseDTO: " + roomDTOPageResponseDTO);

        return roomDTOPageResponseDTO;
    }

    public void roomModify(RoomDTO roomDTO, List<MultipartFile> multipartFiles, Long[] del_num, Long main_num, Hotel hotel) throws Exception {

        log.info("룸 수정 시작 : {}", roomDTO);

        Room room = roomRepository.findById(roomDTO.getRoom_num())
                        .orElseThrow(() -> new EntityNotFoundException("해당 룸 정보를 찾을 수 없습니다."));

        if (!room.getHotel().getHotel_num().equals(hotel.getHotel_num())) {
            throw new SecurityException("권한이 없습니다.");
        }

        room.setRoom_name(roomDTO.getRoom_name());
        room.setRoom_info(roomDTO.getRoom_info());
        room.setRoom_type(roomDTO.getRoom_type());
        room.setRoom_bed(roomDTO.getRoom_bed());
        room.setRoom_price(roomDTO.getRoom_price());
        room.setRoom_state(roomDTO.getRoom_state());

        roomRepository.save(room);

        if (del_num != null && del_num.length > 0) {
            for (Long imageId : del_num) {
                imageService.deleteImage(imageId);
                imageRepository.deleteById(imageId);
            }
        }

        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            for (int i = 0; i < multipartFiles.size(); i++) {
                MultipartFile file = multipartFiles.get(i);
                Image imageEntity = new Image();
                imageEntity.setRoom(room);
                imageEntity.setRepimg_yn("N");
            }
            imageService.updateImage(multipartFiles, null, "room", room.getRoom_num());
        }

        if (main_num != null) {
            Image mainImage = imageRepository.findById(main_num)
                    .orElseThrow(()->new IllegalArgumentException("대표 이미지를 찾을 수 없습니다."));

            List<Image> allImages = imageRepository.findByTarget("room", room.getRoom_num());
            for (Image image : allImages) {
                if ("N".equals(image.getRepimg_yn())) {
                    image.setRepimg_yn("N");
                    imageRepository.save(image);
                }
            }
            mainImage.setRepimg_yn("Y");
            imageRepository.save(mainImage);
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

}
