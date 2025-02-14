package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.BrandDTO;
import com.lookatme.smartstay.dto.HotelDTO;
import com.lookatme.smartstay.dto.ImageDTO;
import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.entity.Brand;
import com.lookatme.smartstay.entity.Hotel;
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

    private final BrandRepository BrandRepository;
    private final ModelMapper modelMapper;
    private final ImageService imageService;
    private final MemberRepository memberRepository; //추가
    private final HotelRepository hotelRepository;

    //brand 등록
    public void insert(BrandDTO brandDTO, String email,
                       List<MultipartFile> multipartFiles) throws Exception {
        Brand brand = modelMapper.map(brandDTO, Brand.class);

        Member member = memberRepository.findByEmail(email); // 추가

        Brand brand1 = BrandRepository.save(brand);

        member.setBrand(brand1); //추가
        //이미지
        if(multipartFiles != null && multipartFiles.size() > 0) {
            imageService.saveImage(multipartFiles, "brand", brand1.getBrand_num());
        }

    }

    // 모든 브랜드 목록을 가져오는 메소드
    public List<BrandDTO> brandList() {
        List<Brand> brands = BrandRepository.findAll();  // 모든 브랜드를 가져옴
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
            brands = BrandRepository.findAll();
        } else {
            // 슈퍼어드민이 아니라면, 이메일에 해당하는 브랜드만 조회
            brands = BrandRepository.findByEmail(email);
        }

        // 브랜드 리스트를 BrandDTO로 변환
        List<BrandDTO> brandDTOS = brands.stream()
                .map(brand -> modelMapper.map(brand, BrandDTO.class))
                .collect(Collectors.toList());

        return brandDTOS;
    }

    /*//brand 목록
    public List<BrandDTO> brandList(){
        List<Brand> brands = BrandRepository.findAll();
        List<BrandDTO> brandDTOS = brands.stream()
                .map(brand -> modelMapper.map(brand, BrandDTO.class)).collect(Collectors.toList());
        return brandDTOS;
    }

    //목록에서 내가 속한 브랜드만 보기
    public List<BrandDTO> myBrand(String email, Member member) {
        List<Brand> brands;

        // member에서 role을 확인하여 슈퍼어드민 여부를 체크
        if (member != null && "SUPERADMIN".equals(member.getRole())) {
            // 슈퍼어드민일 경우 모든 브랜드를 조회
            brands = BrandRepository.findAll();
        } else {
            // 슈퍼어드민이 아니라면, 이메일에 해당하는 브랜드만 조회
            brands = BrandRepository.findByEmail(email);
        }

        // 브랜드 리스트를 BrandDTO로 변환
        List<BrandDTO> brandDTOS = brands.stream()
                .map(brand -> modelMapper.map(brand, BrandDTO.class))
                .collect(Collectors.toList());

        return brandDTOS;
    }*/


    //brand 상세보기
    public BrandDTO read(Long id) {
        Brand brand = BrandRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        BrandDTO brandDTO = modelMapper.map(brand, BrandDTO.class);

        List<Image> imageList = imageService.findImagesByTarget("brand", brand.getBrand_num());
        List<ImageDTO> imageDTOList = imageList.stream().map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList());
        brandDTO.setImageDTOList(imageDTOList);

        return brandDTO;}


        //Optional<Brand> brand = brandRepository.findById(id);
        //BrandDTO brandDTO = modelMapper.map(brand, BrandDTO.class);
        //return brandDTO;


    //brand 수정
    public void update(BrandDTO brandDTO,
                       List<MultipartFile> multipartFiles) throws Exception{
        Brand brand = BrandRepository.findById(brandDTO.getBrand_num())
                .orElseThrow(EntityNotFoundException::new);
        //set
        brand.setBrand_num(brandDTO.getBrand_num());
        brand.setBrand_name(brandDTO.getBrand_name());
        brand.setBusiness_num(brandDTO.getBusiness_num());
        brand.setOwner(brandDTO.getOwner());
        brand.setTel(brandDTO.getTel());

        BrandRepository.save(brand);

        // 이미지 업로드 처리
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            // 기존 이미지를 업데이트 또는 새로운 이미지를 업로드
            imageService.saveImage(multipartFiles, "brand", brand.getBrand_num());
        }

       /* Optional<Brand> brand = brandRepository.findById(brandDTOList.getBrand_num());
        if(brand.isPresent()){
            Brand brands = modelMapper.map(brandDTOList, Brand.class);
            brandRepository.save(brands);
        } */
    }
    //brand 삭제
    public void delete(Long id){
        log.info("서비스로 들어온 삭제할 번호 :"+id);

        BrandRepository.deleteById(id);
    }


}
