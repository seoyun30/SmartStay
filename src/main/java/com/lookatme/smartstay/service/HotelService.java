package com.lookatme.smartstay.service;

import com.lookatme.smartstay.constant.ActiveState;
import com.lookatme.smartstay.dto.BrandDTO;
import com.lookatme.smartstay.dto.HotelDTO;
import com.lookatme.smartstay.dto.ImageDTO;
import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.entity.*;
import com.lookatme.smartstay.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
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
    private final RoomRepository roomRepository;
    private final EmailService emailService;
    private final ImageRepository imageRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;

    //hotel 등록
    public void insert(HotelDTO hotelDTO, String email,
                       List<MultipartFile> multipartFiles) throws Exception {
        Hotel hotel = modelMapper.map(hotelDTO, Hotel.class);
        hotel.setActive_state(ActiveState.ACTIVE);

        Member member = memberRepository.findByEmail(email); //추가
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
                .map(hotel -> {
                    HotelDTO hotelDTO = modelMapper.map(hotel, HotelDTO.class);
                    BrandDTO brandDTO = modelMapper.map(hotel.getBrand(), BrandDTO.class);
                    hotelDTO.setBrandDTO(brandDTO);
                    Long lowestPrice = getHotelLowestPrice(hotel.getHotel_num());
                    hotelDTO.setLowestPrice(lowestPrice);
                    ImageDTO mainImage = getHotelMainImage(hotel.getHotel_num());
                    hotelDTO.setMainImage(mainImage);
                    return hotelDTO;
                })
                .collect(Collectors.toList());

        return hotelDTOS;
    }

    //활성된 호텔만 유저 보이기
    public List<HotelDTO> activeHotelList() {
        List<Hotel> hotels = hotelRepository.findActiveHotel();
        List<HotelDTO> hotelDTOS = hotels.stream()
                .map(hotel -> {
                    HotelDTO hotelDTO = modelMapper.map(hotel, HotelDTO.class);
                    BrandDTO brandDTO = modelMapper.map(hotel.getBrand(), BrandDTO.class);
                    hotelDTO.setBrandDTO(brandDTO);
                    Long lowestPrice = getHotelLowestPrice(hotel.getHotel_num());
                    hotelDTO.setLowestPrice(lowestPrice);
                    ImageDTO mainImage = getHotelMainImage(hotel.getHotel_num());
                    hotelDTO.setMainImage(mainImage);
                    Double averageScore = calculateAverageScore(hotel.getHotel_num());
                    if (averageScore != null) {
                        hotelDTO.setScore(String.format("%.2f", averageScore));
                    } else {
                        hotelDTO.setScore("(0)");
                    }
                    int reviewCount = getReviewCount(hotel.getHotel_num());
                    hotelDTO.setReview_count(reviewCount);
                    return hotelDTO;
                })
                .sorted(Comparator.comparingInt(HotelDTO::getReview_count).reversed())
                .collect(Collectors.toList());

        return hotelDTOS;
    }

    //목록에서 내가 속한 호텔만 보기
    public List<HotelDTO> myHotelList(String email){
        Member member = memberRepository.findByEmail(email);
        if (member == null || member.getBrand() == null) {
            List<HotelDTO> hotelDTOList = new ArrayList<>();
            return hotelDTOList;
        }

        Brand brand = member.getBrand();

        List<Hotel> hotels = hotelRepository.findByMyBrand(brand);
        return hotels.stream()
                .map(hotel -> {
                    HotelDTO hotelDTO = modelMapper.map(hotel, HotelDTO.class);
                    Long lowestPrice = getHotelLowestPrice(hotel.getHotel_num());
                    hotelDTO.setLowestPrice(lowestPrice);
                    return hotelDTO;
                })
                .collect(Collectors.toList());
       /* List<Hotel> hotels = hotelRepository.findByMyBrand(brand);
        List<HotelDTO> hotelDTOS = hotels.stream()
                .map(brand -> modelMapper.map(hotels, HotelDTO.class)).collect(Collectors.toList());
        return hotelDTOS;*/
    }

    //chief 상세보기
    public HotelDTO read(Long id) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        HotelDTO hotelDTO = modelMapper.map(hotel, HotelDTO.class);

        Long lowestPrice = getHotelLowestPrice(hotel.getHotel_num());
        hotelDTO.setLowestPrice(lowestPrice);

        List<Image> imageList = imageService.findImagesByTarget("hotel", hotel.getHotel_num());
        List<ImageDTO> imageDTOList = imageList.stream().map(image -> {ImageDTO imageDTO = modelMapper.map(image, ImageDTO.class);
        imageDTO.setThumbnail_url(image.getThumbnail_url());
        return imageDTO;
        }).collect(Collectors.toList());

        hotelDTO.setImageDTOList(imageDTOList);

        ImageDTO mainImage = getHotelMainImage(hotel.getHotel_num());
        if (mainImage != null) {
            hotelDTO.setMainImage(mainImage);
        }

        Double averageScore = calculateAverageScore(hotel.getHotel_num());
        if (averageScore != null) {
            hotelDTO.setScore(String.format("%.2f", averageScore));
        }

        int reviewCount = reviewService.getReviewCountByHotel(hotel.getHotel_num());
        hotelDTO.setReview_count(reviewCount);

        return hotelDTO;
    }

    private Double calculateAverageScore(Long hotel_num) {
        List<Review> reviews = reviewRepository.findByHotel(hotel_num);
        if (reviews.isEmpty()) {
            return null;
        }
        return reviews.stream()
                .filter(review -> {
                    try {
                        Double.parseDouble(review.getScore());
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .mapToDouble(review -> Double.parseDouble(review.getScore()))
                .average()
                .orElse(0.0);
    }

    private int getReviewCount(Long hotel_num) {
        return reviewRepository.countByHotelNum(hotel_num);
    }

    public HotelDTO myHotel(String email) {
        Member member = memberRepository.findByEmail(email);
        if (member == null || member.getHotel() == null) {
            throw new IllegalArgumentException("호텔 정보를 찾을 수 없습니다.");
            }
        Hotel hotel = hotelRepository.findById(member.getHotel().getHotel_num()).orElseThrow(EntityNotFoundException::new);
        HotelDTO hotelDTO = modelMapper.map(hotel, HotelDTO.class);

        ImageDTO mainImage = getHotelMainImage(hotel.getHotel_num());
        if (mainImage != null) {
            hotelDTO.setMainImage(mainImage);
        }

        return hotelDTO;
    }


    //chief 수정
    public void update(HotelDTO hotelDTO,
                       List<MultipartFile> multipartFiles, List<Long> delnumList) throws Exception {
        Hotel hotel = hotelRepository.findById(hotelDTO.getHotel_num())
                .orElseThrow(EntityNotFoundException::new);
        //set
        hotel.setHotel_num(hotelDTO.getHotel_num());
        hotel.setHotel_name(hotelDTO.getHotel_name());
        hotel.setBusiness_num(hotelDTO.getBusiness_num());
        hotel.setOwner(hotelDTO.getOwner());
        hotel.setTel(hotelDTO.getTel());

        hotelRepository.save(hotel);

        boolean hasNewImages = multipartFiles != null && multipartFiles.stream().anyMatch(file -> !file.isEmpty());
        boolean hasDeletedImages = delnumList != null && !delnumList.isEmpty();

        if (hasNewImages || hasDeletedImages) {
            log.info("이미지 업데이트 실행");
            try {
                imageService.updateImage(
                        hasNewImages ? multipartFiles : null,
                        hasDeletedImages ? delnumList : null,
                        "hotel",
                        hotel.getHotel_num()
                );
            } catch (IndexOutOfBoundsException e) {
                log.error("이미지 업데이트 중 인덱스 오류 발생: {}", e.getMessage());
                throw new IllegalArgumentException("업로드된 파일이나 삭제 요청이 잘못되었습니다.");
            }
        } else {
            log.info("이미지 업데이트 없이 텍스트 정보만 수정");
        }
    }

    //chief 삭제
    public void delete(Long id) {
        log.info("서비스로 들어온 삭제할 번호 :" + id);

        List<Image> images = imageRepository.findByTarget("hotel", id);

        if (images == null || images.isEmpty()) {
            log.error("삭제하려는 호텔의 이미지가 없습니다. hotel_num : " +id);
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

        memberRepository.deleteByHotelHotel_num(id);
        hotelRepository.updateHotelBrandToNull(id); //호텔이 참조하는 브랜드번호를 null로 설정
        hotelRepository.deleteById(id);
    }

    //hotel 상태 변경
    public HotelDTO stateUpdate (Long Hotel_num) {
        Hotel hotel = hotelRepository.findById(Hotel_num)
                .orElseThrow(EntityNotFoundException::new);

        if (hotel != null) {
            hotel.setActive_state(hotel.getActive_state() == ActiveState.ACTIVE ? ActiveState.INACTIVE : ActiveState.ACTIVE);
            hotelRepository.save(hotel);
        }

        hotel = hotelRepository.findById(Hotel_num)
                .orElseThrow(EntityNotFoundException::new);
        HotelDTO hotelDTO = modelMapper.map(hotel, HotelDTO.class);

        return hotelDTO;
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

    public List<HotelDTO> searchList(String query) {

        List<Hotel> hotels = hotelRepository.findByHotel_nameIgnoreCaseOrAddressContainingIgnoreCase(query);

        List<HotelDTO> hotelDTOS = hotels.stream()
                        .map(hotel -> {
                                    HotelDTO hotelDTO = modelMapper.map(hotel, HotelDTO.class);
                                    Long lowestPrice = getHotelLowestPrice(hotel.getHotel_num());
                                    hotelDTO.setLowestPrice(lowestPrice);
                                    ImageDTO mainImage = getHotelMainImage(hotel.getHotel_num());
                                    hotelDTO.setMainImage(mainImage);
                                    return hotelDTO;
                                })
                .collect(Collectors.toList());

        return hotelDTOS;
    }

    public Long getHotelLowestPrice(Long hotel_num) {
        Long lowestPrice = roomRepository.findLowestRoomPriceByHotelNum(hotel_num);

        return lowestPrice != null ? lowestPrice : 0L;
    }

    public ImageDTO getHotelMainImage(Long hotel_num) {

        List<Image> imageList = imageService.findImagesByTarget("hotel", hotel_num);

        if (imageList != null && !imageList.isEmpty()) {
            return modelMapper.map(imageList.get(0), ImageDTO.class);
        }
        return null;
    }

    public List<HotelDTO> getHotelsSortedByPrice(String order) {
        Sort sort = Sort.by(order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "lowestPrice");
        List<Hotel> hotels = hotelRepository.findAll(sort);

        return hotels.stream().map(hotel -> modelMapper.map(hotel, HotelDTO.class)).collect(Collectors.toList());
    }
}
