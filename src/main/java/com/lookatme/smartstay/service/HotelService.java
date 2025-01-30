package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.BrandDTO;
import com.lookatme.smartstay.dto.HotelDTO;
import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.entity.Brand;
import com.lookatme.smartstay.entity.Hotel;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.repository.BrandRepository;
import com.lookatme.smartstay.repository.HotelRepository;
import com.lookatme.smartstay.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class HotelService {

    private final HotelRepository hotelRepository;
    private final BrandRepository brandRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final ImageService imageService;
    //hotel 등록
    public void insert(HotelDTO hotelDTO, Principal principal,
                       List<MultipartFile> multipartFiles) throws Exception {
        Hotel hotel = modelMapper.map(hotelDTO, Hotel.class);
        Member member = memberRepository.findByEmail(principal.getName()); //추가
        Brand brand =  member.getBrand(); //추가
        hotel.setBrand(brand); //추가
        Hotel hotel1 = hotelRepository.save(hotel);

        //이미지
        if (multipartFiles != null && multipartFiles.size() > 0) {
            imageService.saveImage(multipartFiles, "hotel", hotel1.getHotel_num());
        }

    }

    //chief 목록
    public List<HotelDTO> hotelList() {
        List<Hotel> hotels = hotelRepository.findAll();
        hotels.forEach(hotel -> log.info(hotel));
        List<HotelDTO> hotelDTOS = hotels.stream()
                .map(hotel -> modelMapper.map(hotel, HotelDTO.class).setBrandDTO(
                        modelMapper.map(hotel.getBrand(), BrandDTO.class)
                ) ).collect(Collectors.toList());
        return hotelDTOS;
    }

    //chief 상세보기
    public HotelDTO read(Long id) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow(EntityNotFoundException::new);
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
        Hotel hotel = hotelRepository.findById(hotelDTO.getHotel_num())
                .orElseThrow(EntityNotFoundException::new);
        //set
        hotel.setHotel_num(hotelDTO.getHotel_num());
        hotel.setHotel_name(hotelDTO.getHotel_name());
        hotel.setBusiness_num(hotelDTO.getBusiness_num());
        hotel.setOwner(hotelDTO.getOwner());
        hotel.setTel(hotelDTO.getTel());

        hotelRepository.save(hotel);

       /* Optional<Chief> chief = chiefRepository.findById(chiefDTOList.getChief_num());
        if(chief.isPresent()){
            Chief chiefs = modelMapper.map(chiefDTOList, Chief.class);
            chiefRepository.save(chiefs);
        } */
    }

    //chief 삭제
    public void delete(Long id) {
        log.info("서비스로 들어온 삭제할 번호 :" + id);

        hotelRepository.deleteById(id);
    }


    public List<HotelDTO> getHotelByMember(MemberDTO memberDTO) {

        Member member = memberRepository.findByEmail(memberDTO.getEmail());

        log.info("멤버 : " + member);

        List<Hotel> hotels = hotelRepository.findByEmail(member.getEmail());

        if (hotels.isEmpty()) {
            log.info("해당 사용자의 호텔 정보가 없습니다.");
        } else {
            log.info("호텔 목록 크기: {}", hotels.size());
        }

        // 호텔 리스트를 HotelDTO로 변환
        List<HotelDTO> hotelDTOS = hotels.stream()
                .map(hotel -> modelMapper.map(hotel, HotelDTO.class)
                ).collect(Collectors.toList());

        return hotelDTOS;
    }
}
