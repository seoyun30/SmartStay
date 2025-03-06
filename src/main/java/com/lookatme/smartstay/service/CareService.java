package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.entity.Care;
import com.lookatme.smartstay.entity.Hotel;
import com.lookatme.smartstay.entity.Image;
import com.lookatme.smartstay.repository.CareRepository;
import com.lookatme.smartstay.repository.HotelRepository;
import com.lookatme.smartstay.repository.ImageRepository;
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
public class CareService {

    private final CareRepository careRepository;
    private final ModelMapper modelMapper;
    private final ImageService imageService;
    private final ImageRepository imageRepository;
    private final HotelRepository hotelRepository;

    public void careRegister(CareDTO careDTO, Long hotel_num, List<MultipartFile> multipartFiles, Long mainImageIndex) throws Exception {

        Hotel hotel = hotelRepository.findById(hotel_num).orElseThrow(EntityNotFoundException::new);

        Care care = modelMapper.map(careDTO, Care.class);
        care.setHotel(hotel);
        care = careRepository.save(care);

        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            imageService.saveImage(multipartFiles, "care", care.getCare_num());
        }
    }

    public CareDTO careRead(Long care_num) {

        Care care = careRepository.findById(care_num).orElseThrow(EntityNotFoundException::new);

        CareDTO careDTO = modelMapper.map(care, CareDTO.class);

        Hotel hotel = care.getHotel();
        if (hotel != null) {
            HotelDTO hotelDTO = modelMapper.map(hotel, HotelDTO.class);
            careDTO.setHotelDTO(hotelDTO);
        }

        List<Image> imageList = imageRepository.findByTarget("care", care_num);

        if (imageList != null && !imageList.isEmpty()) {
            List<ImageDTO> imageDTOList = imageList.stream()
                    .map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList());

            List<Long> imageIdList = imageDTOList.stream()
                    .map(ImageDTO::getImage_id).collect(Collectors.toList());

            careDTO.setImageDTOList(imageDTOList);
            careDTO.setImageIdList(imageIdList);
        }

        return careDTO;
    }

    public PageResponseDTO<CareDTO> careList(HotelDTO hotelDTO, PageRequestDTO pageRequestDTO, String sortField, String sortDir) {

        Hotel hotel = modelMapper.map(hotelDTO, Hotel.class);

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), sort);

        Page<Care> result = careRepository.findByHotel(hotel, pageable);

        List<CareDTO> careDTOList = result.stream()
                .map(care -> modelMapper.map(care, CareDTO.class)
                        .setHotelDTO(modelMapper.map(care.getHotel(), HotelDTO.class)))
                .collect(Collectors.toList());

        for (CareDTO careDTO : careDTOList) {
            List<Image> careImageList = imageRepository.findByTarget("care", careDTO.getCare_num());
            if (!careImageList.isEmpty()) {
                List<ImageDTO> careImageDTOList = careImageList.stream()
                        .map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList());
                careDTO.setImageDTOList(careImageDTOList);
            } else {
                careDTO.setImageDTOList(null);
            }
        }

        if (careDTOList == null) {
            careDTOList = Collections.emptyList();
        }

        PageResponseDTO<CareDTO> careDTOPageResponseDTO = PageResponseDTO.<CareDTO>withAll()
                .pageRequestDTO(pageRequestDTO).dtoList(careDTOList).total((int) result.getTotalElements()).build();

        return careDTOPageResponseDTO;
    }

    public void careModify(CareDTO careDTO, List<MultipartFile> multipartFiles, HotelDTO hotelDTO, List<Long> delnumList) throws Exception {

        Care care = careRepository.findById(careDTO.getCare_num()).orElseThrow(EntityNotFoundException::new);

        if (!care.getHotel().getHotel_num().equals(hotelDTO.getHotel_num())) {
            throw new Exception();
        }

        Hotel hotel = hotelRepository.findById(hotelDTO.getHotel_num()).orElseThrow(EntityNotFoundException::new);
        care.setHotel(hotel);

        care.setCare_name(careDTO.getCare_name());
        care.setCare_detail(careDTO.getCare_detail());
        care.setCare_price(careDTO.getCare_price());

        care = careRepository.save(care);

        boolean hasNewImages = multipartFiles != null && multipartFiles.stream().anyMatch(file -> !file.isEmpty());
        boolean hasDeletedImages = delnumList != null && !delnumList.isEmpty();

        if (hasNewImages || hasDeletedImages) {
            log.info("이미지 업데이트 실행");
            try {
                imageService.updateImage(
                        hasNewImages ? multipartFiles : null,
                        hasDeletedImages ? delnumList : null,
                        "care",
                        care.getCare_num()
                );
            } catch (IndexOutOfBoundsException e) {
                log.error("이미지 업데이트 중 인덱스 오류 발생: {}", e.getMessage());
                throw new IllegalArgumentException("업로드된 파일이나 삭제 요청이 잘못되었습니다.");
            }
        } else {
            log.info("이미지 업데이트 없이 텍스트 정보만 수정");
        }

    }

    public void careDelete(Long id) {

        List<Image> images = imageRepository.findByTarget("care", id);

        if (images == null || images.isEmpty()) {
            log.info("삭제하려는 케어 이미지가 없습니다. care_num:" + id);
        }else {
            for (Image image : images) {
                try {
                    imageService.deleteImage(image.getImage_id());
                    imageRepository.deleteById(image.getImage_id());
                }catch (Exception e) {
                    log.info("이미지 삭제 실패 : " + e.getMessage());
                }
            }
        }
        careRepository.deleteById(id);
        log.info("케어가 삭제되었습니다. care_num:" + id);
    }

    public PageResponseDTO<CareDTO> searchList(String searchType, String searchKeyword, String sortField, String sortDir, PageRequestDTO pageRequestDTO) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Page<Care> result;

        switch (searchType) {
            case "keyword":
                result = careRepository.findByCare_nameContainingIgnoreCaseOrCare_detailContainingIgnoreCase(searchKeyword, PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), sort));
                break;
            case "careName":
                result = careRepository.findByCare_nameContainingIgnoreCase(searchKeyword, PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), sort));
                break;
            case "careDetail":
                result = careRepository.findByCare_detailContainingIgnoreCase(searchKeyword, PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), sort));
                break;
            default:
                result = careRepository.findAll(PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(), sort));
        }

        List<CareDTO> careDTOList = result.stream()
                .map(care -> modelMapper.map(care, CareDTO.class)
                        .setHotelDTO(modelMapper.map(care.getHotel(), HotelDTO.class)))
                .collect(Collectors.toList());

        for (CareDTO careDTO : careDTOList) {
            List<Image> careImageList = imageRepository.findByTarget("care", careDTO.getCare_num());
            if (!careImageList.isEmpty()) {
                List<ImageDTO> careImageDTOList = careImageList.stream()
                        .map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList());
                careDTO.setImageDTOList(careImageDTOList);
            } else {
                careDTO.setImageDTOList(null);
            }
        }

        PageResponseDTO<CareDTO> careDTOPageResponseDTO = PageResponseDTO.<CareDTO>withAll()
                .pageRequestDTO(pageRequestDTO).dtoList(careDTOList).total((int) result.getTotalElements()).build();

        return careDTOPageResponseDTO;
    }

    //룸서비스 목록에서 케어 서비스 조회용
    //기타 메뉴만
    public PageResponseDTO<CareDTO> findCareList(Long hotel_num, PageRequestDTO pageRequestDTO) {

        Hotel hotel = hotelRepository.findById(hotel_num)
                .orElseThrow(EntityNotFoundException::new);

        Pageable pageable = pageRequestDTO.getPageable("care_num");

        Page<Care> result = careRepository.findByHotel(hotel, pageable);

        List<CareDTO> careDTOList = result.stream()
                .map(care -> modelMapper.map(care, CareDTO.class)
                        .setHotelDTO(modelMapper.map(care.getHotel(), HotelDTO.class)))
                .collect(Collectors.toList());

        for (CareDTO careDTO : careDTOList) {
            List<Image> careImageList = imageRepository.findByTarget("care", careDTO.getCare_num());
            if (!careImageList.isEmpty()) {
                List<ImageDTO> menuImageDTOList = careImageList.stream()
                        .map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList());
                careDTO.setImageDTOList(menuImageDTOList);
            } else {
                careDTO.setImageDTOList(null);
            }
        }

        if (careDTOList == null) {
            careDTOList = Collections.emptyList();
        }

        PageResponseDTO<CareDTO> careDTOPageResponseDTO = PageResponseDTO.<CareDTO>withAll()
                .pageRequestDTO(pageRequestDTO).dtoList(careDTOList).total((int) result.getTotalElements()).build();

        return careDTOPageResponseDTO;
    }
}
