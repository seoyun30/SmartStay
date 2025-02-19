package com.lookatme.smartstay.service;

import com.lookatme.smartstay.constant.ActiveState;
import com.lookatme.smartstay.dto.BrandDTO;
import com.lookatme.smartstay.dto.ImageDTO;
import com.lookatme.smartstay.entity.Brand;
import com.lookatme.smartstay.entity.Image;
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
    private final HotelRepository hotelRepository;

    //brand 등록
    public void insert(BrandDTO brandDTO, String email,
                       List<MultipartFile> multipartFiles) throws Exception {
        Brand brand = modelMapper.map(brandDTO, Brand.class);
        brand.setActive_state(ActiveState.ACTIVE);

        Member member = memberRepository.findByEmail(email); // 추가

        Brand brand1 = brandRepository.save(brand);

        member.setBrand(brand1); //추가
        //이미지
        if(multipartFiles != null && multipartFiles.size() > 0) {
            imageService.saveImage(multipartFiles, "brand", brand1.getBrand_num());
        }

    }

    // 모든 브랜드 목록을 가져오는 메소드
    public List<BrandDTO> brandList() {
        List<Brand> brands = brandRepository.findAll();  // 모든 브랜드를 가져옴
        List<BrandDTO> brandDTOS = brands.stream()
                .map(brand -> modelMapper.map(brand, BrandDTO.class))
                .collect(Collectors.toList());
        return brandDTOS;
    }

    // 로그인한 사용자의 브랜드만 조회 (슈퍼어드민은 모든 브랜드 조회)
    public List<BrandDTO> myBrand(String email, Member member) {
        List<Brand> brands;

        // member에서 role을 확인하여 슈퍼어드민 여부를 체크
        if (member != null && member.getRole().name().equals("SUPERADMIN")) {
            // 슈퍼어드민일 경우 모든 브랜드를 조회
            brands = brandRepository.findAll();
        } else {
            // 슈퍼어드민이 아니라면, 이메일에 해당하는 브랜드만 조회
            brands = brandRepository.findByEmail(email);
        }

        // 브랜드 리스트를 BrandDTO로 변환
        List<BrandDTO> brandDTOS = brands.stream()
                .map(brand -> modelMapper.map(brand, BrandDTO.class))
                .collect(Collectors.toList());

        return brandDTOS;
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
                       List<MultipartFile> multipartFiles) throws Exception{
        Brand brand = brandRepository.findById(brandDTO.getBrand_num())
                .orElseThrow(EntityNotFoundException::new);
        //set
        brand.setBrand_num(brandDTO.getBrand_num());
        brand.setBrand_name(brandDTO.getBrand_name());
        brand.setBusiness_num(brandDTO.getBusiness_num());
        brand.setOwner(brandDTO.getOwner());
        brand.setTel(brandDTO.getTel());

        brandRepository.save(brand);

        // 이미지 업로드 처리
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            // 기존 이미지를 업데이트 또는 새로운 이미지를 업로드
            imageService.saveImage(multipartFiles, "brand", brand.getBrand_num());
        }

    }

    //brand 삭제
    public void delete(Long id){
        log.info("서비스로 들어온 삭제할 번호 :"+id);

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
