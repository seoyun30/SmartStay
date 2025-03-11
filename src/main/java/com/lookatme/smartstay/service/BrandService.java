package com.lookatme.smartstay.service;

import com.lookatme.smartstay.constant.ActiveState;
import com.lookatme.smartstay.constant.Role;
import com.lookatme.smartstay.dto.BrandDTO;
import com.lookatme.smartstay.dto.ImageDTO;
import com.lookatme.smartstay.entity.Brand;
import com.lookatme.smartstay.entity.Image;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.RoomReserve;
import com.lookatme.smartstay.repository.BrandRepository;
import com.lookatme.smartstay.repository.HotelRepository;
import com.lookatme.smartstay.repository.ImageRepository;
import com.lookatme.smartstay.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class BrandService {

    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;
    private final ImageService imageService;
    private final MemberRepository memberRepository; //추가
    private final ImageRepository imageRepository;

    //brand 등록
    public void insert(BrandDTO brandDTO, String email,
                       List<MultipartFile> multipartFiles) throws Exception {

        Member member = memberRepository.findByEmail(email);

        if(member.getRole() == Role.CHIEF && member.getBrand() != null) {
            throw new IllegalStateException("이미 등록된 브랜드가 있습니다. 추가등록 불가");
        }

        Brand brand = modelMapper.map(brandDTO, Brand.class);
        brand.setActive_state(ActiveState.ACTIVE);
        Brand brand1 = brandRepository.save(brand);
        member.setBrand(brand1);
        //이미지
        if(multipartFiles != null && multipartFiles.size() > 0) {
            imageService.saveImage(multipartFiles, "brand", brand1.getBrand_num());
        }

    }

    // 모든 브랜드 목록을 가져오는 메소드
    public List<BrandDTO> brandList() {
        List<Brand> brands = brandRepository.findAll();  // 모든 브랜드를 가져옴
        if (brands.isEmpty()) { return new ArrayList<>(); }
        List<BrandDTO> brandDTOS = brands.stream()
                .map(brand -> modelMapper.map(brand, BrandDTO.class))
                .collect(Collectors.toList());
        return brandDTOS;
    }

    public List<BrandDTO> myBrand(Long brand_num, Member member) {
        List<Brand> brands;

        if (member != null && member.getRole().name().equals("SUPERADMIN")) {
            // 슈퍼어드민이면 모든 브랜드 조회
            brands = brandRepository.findAll();
        } else {
            // CHIEF가 등록한 브랜드가 아니라도, 같은 brand_num을 가진 브랜드를 가져오기
            if (member.getBrand() != null) {
                brands = brandRepository.findByBrandNum(member.getBrand().getBrand_num());
            } else {
                brands = new ArrayList<>(); // 브랜드가 없는 경우 빈 리스트 반환
            }
        }

        // 브랜드 리스트를 BrandDTO로 변환
        return brands.stream()
                .map(brand -> modelMapper.map(brand, BrandDTO.class))
                .collect(Collectors.toList());
    }


    //brand 상세보기
    public BrandDTO read(Long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        BrandDTO brandDTO = modelMapper.map(brand, BrandDTO.class);

        List<Image> imageList = imageService.findImagesByTarget("brand", brand.getBrand_num());
        List<ImageDTO> imageDTOList = imageList.stream().map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList());
        brandDTO.setImageDTOList(imageDTOList);

        ImageDTO mainImage = getBrandMainImage(brand.getBrand_num());
        if (mainImage != null) {
            brandDTO.setMainImage(mainImage);
        }

        return brandDTO;
    }


    //brand 수정
    public void update(BrandDTO brandDTO,
                       List<MultipartFile> multipartFiles, List<Long> delnumList) throws Exception{
        Brand brand = brandRepository.findById(brandDTO.getBrand_num())
                .orElseThrow(EntityNotFoundException::new);
        //set
        brand.setBrand_num(brandDTO.getBrand_num());
        brand.setBrand_name(brandDTO.getBrand_name());
        brand.setBusiness_num(brandDTO.getBusiness_num());
        brand.setOwner(brandDTO.getOwner());
        brand.setTel(brandDTO.getTel());

        brandRepository.save(brand);

        boolean hasNewImages = multipartFiles != null && multipartFiles.stream().anyMatch(file -> !file.isEmpty());
        boolean hasDeletedImages = delnumList != null && !delnumList.isEmpty();

        if (hasNewImages || hasDeletedImages) {
            log.info("이미지 업데이트 실행");
            try {
                imageService.updateImage(
                        hasNewImages ? multipartFiles : null,
                        hasDeletedImages ? delnumList : null,
                        "brand",
                        brand.getBrand_num()
                );
            } catch (IndexOutOfBoundsException e) {
                log.error("이미지 업데이트 중 인덱스 오류 발생: {}", e.getMessage());
                throw new IllegalArgumentException("업로드된 파일이나 삭제 요청이 잘못되었습니다.");
            }
        } else {
            log.info("이미지 업데이트 없이 텍스트 정보만 수정");
        }

    }

    //brand 삭제
    public void delete(Long id){
        log.info("서비스로 들어온 삭제할 번호 :"+id);

        List<Image> images = imageRepository.findByTarget("brand", id);

        if (images == null || images.isEmpty()) {
            log.error("삭제하려는 브랜드의 이미지가 없습니다. brand_num : " +id);
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

        brandRepository.deleteById(id);
    }

    //brand 상태 변경
    public BrandDTO stateUpdate (Long brand_num) {
        Brand brand = brandRepository.findById(brand_num)
                .orElseThrow(EntityNotFoundException::new);

        if (brand != null) {
            brand.setActive_state(brand.getActive_state() == ActiveState.ACTIVE ? ActiveState.INACTIVE : ActiveState.ACTIVE);
            brandRepository.save(brand);
        }

        brand = brandRepository.findById(brand_num)
                .orElseThrow(EntityNotFoundException::new);
        BrandDTO brandDTO = modelMapper.map(brand, BrandDTO.class);

        return brandDTO;
    }

    public ImageDTO getBrandMainImage (Long brand_num) {

        List<Image> imageList = imageService.findImagesByTarget("brand", brand_num);
        if (imageList != null && !imageList.isEmpty()) {
            return modelMapper.map(imageList.get(0), ImageDTO.class);
        }
        return null;
    }

}
