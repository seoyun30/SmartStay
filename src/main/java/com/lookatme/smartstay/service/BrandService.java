package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.BrandDTO;
import com.lookatme.smartstay.dto.HotelDTO;
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

    //brand 목록
    public List<BrandDTO> brandList(){
        List<Brand> brands = BrandRepository.findAll();
        List<BrandDTO> brandDTOS = brands.stream()
                .map(brand -> modelMapper.map(brand, BrandDTO.class)).collect(Collectors.toList());
        return brandDTOS;
    }

    public List<BrandDTO> myBrand(String email){
        List<Brand> brands = BrandRepository.findByEmail(email);
        List<BrandDTO> brandDTOS = brands.stream()
                .map(brand -> modelMapper.map(brand, BrandDTO.class)).collect(Collectors.toList());
        return brandDTOS;
    }

    //brand 상세보기
    public BrandDTO read(Long id) {
        Brand brand = BrandRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        BrandDTO brandDTO = modelMapper.map(brand, BrandDTO.class);
                    //.setItemImgDTOList(item.getItemImgList());
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
