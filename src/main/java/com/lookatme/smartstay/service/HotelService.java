package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.HotelDTO;
import com.lookatme.smartstay.entity.Hotel;
import com.lookatme.smartstay.repository.HotelRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final ImageService imageService;

    //manager 등록
    public void hotelInsert(HotelDTO hotelDTO,
                              List<MultipartFile> multipartFiles) throws Exception{
        Hotel hotel = modelMapper.map(hotelDTO, Hotel.class);
        Hotel hotel1 = hotelRepository.save(hotel);

        //이미지
        if(multipartFiles != null && multipartFiles.size() > 0) {
            imageService.saveImage(multipartFiles, "hotel", hotel1.getHotel_num());
        }

    }

    //manager 목록
    public List<HotelDTO> hotelList(){
        List<Hotel> hotels = hotelRepository.findAll();
        List<HotelDTO> hotelDTOS = hotels.stream()
                .map(manager->modelMapper.map(manager, HotelDTO.class)).collect(Collectors.toList());
        return hotelDTOS;
    }

    //manager 상세보기
    public HotelDTO hotelRead(Long id) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        HotelDTO hotelDTO = modelMapper.map(hotel, HotelDTO.class);

        return hotelDTO;
    }

    //manager 수정
    public void hotelUpdate(HotelDTO hotelDTO,
                              List<MultipartFile> multipartFiles) throws Exception{
        Hotel hotel = hotelRepository.findById(hotelDTO.getHotel_num())
                .orElseThrow(EntityNotFoundException::new);

        //set
        hotel.setHotel_num(hotelDTO.getHotel_num());
        hotel.setHotel_name(hotelDTO.getHotel_name());
        hotel.setBusiness_num(hotelDTO.getBusiness_num());
        hotel.setOwner(hotelDTO.getOwner());
        hotel.setAddress(hotelDTO.getAddress());
        hotel.setTel(hotelDTO.getTel());
        hotel.setScore(hotelDTO.getScore());

        hotelRepository.save(hotel);
    }

    //manager 삭제
    public void hotelDelete(Long id){
        log.info("서비스로 들어온 삭제될 번호: "+id);

        hotelRepository.deleteById(id); }




    public List<HotelDTO> mainHotel() {

        List<Hotel> hotelList = hotelRepository.findAll();
        List<HotelDTO> hotelDTOList = hotelList.stream()
                .map(manager -> modelMapper.map(manager, HotelDTO.class))
                .collect(Collectors.toList());

        return hotelDTOList;
    }

    public List<HotelDTO> searchList() {

        List<Hotel> hotels = hotelRepository.findAll();
        List<HotelDTO> hotelDTOList = hotels.stream()
                .map(manager -> modelMapper.map(manager, HotelDTO.class))
                .collect(Collectors.toList());

        return hotelDTOList;
    }

}
