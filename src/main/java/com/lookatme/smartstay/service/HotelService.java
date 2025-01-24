package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.HotelDTO;
import com.lookatme.smartstay.entity.Hotel;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class HotelService {

    private static final Logger log = LogManager.getLogger(HotelService.class);
    private final com.lookatme.smartstay.repository.HotelRepository HotelRepository;
    private final ModelMapper modelMapper;
    private final ImageService imageService;

    //hotel 등록
    public void insert(HotelDTO hotelDTO,
                       List<MultipartFile> multipartFiles) throws Exception {
        Hotel hotel = modelMapper.map(hotelDTO, Hotel.class);
        Hotel hotel1 = HotelRepository.save(hotel);

        //이미지
        if (multipartFiles != null && multipartFiles.size() > 0) {
            imageService.saveImage(multipartFiles, "hotel", hotel1.getHotel_num());
        }

    }

    //chief 목록
    public List<HotelDTO> chiefList() {
        List<Hotel> hotels = HotelRepository.findAll();
        List<HotelDTO> hotelDTOS = hotels.stream()
                .map(chief -> modelMapper.map(chief, HotelDTO.class)).collect(Collectors.toList());
        return hotelDTOS;
    }

    //chief 상세보기
    public HotelDTO read(Long id) {
        Hotel hotel = HotelRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        HotelDTO hotelDTO = modelMapper.map(hotel, HotelDTO.class);
        //.setItemImgDTOList(item.getItemImgList());
        return hotelDTO;
    }


    //Optional<Chief> chief = chiefRepository.findById(id);
    //ChiefDTO chiefDTO = modelMapper.map(chief, ChiefDTO.class);
    //return chiefDTO;

    //chief 수정
    public void update(HotelDTO hotelDTO,
                       List<MultipartFile> multipartFiles) throws Exception {
        Hotel hotel = HotelRepository.findById(hotelDTO.getHotel_num())
                .orElseThrow(EntityNotFoundException::new);
        //set
        hotel.setHotel_num(hotelDTO.getHotel_num());
        hotel.setHotel_name(hotelDTO.getHotel_name());
        hotel.setBusiness_num(hotelDTO.getBusiness_num());
        hotel.setOwner(hotelDTO.getOwner());
        hotel.setTel(hotelDTO.getTel());

        HotelRepository.save(hotel);

       /* Optional<Chief> chief = chiefRepository.findById(chiefDTOList.getChief_num());
        if(chief.isPresent()){
            Chief chiefs = modelMapper.map(chiefDTOList, Chief.class);
            chiefRepository.save(chiefs);
        } */
    }

    //chief 삭제
    public void delete(Long id) {
        log.info("서비스로 들어온 삭제할 번호 :" + id);

        HotelRepository.deleteById(id);
    }
}
