package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.BrandDTO;
import com.lookatme.smartstay.entity.Brand;
import com.lookatme.smartstay.repository.BrandRepository;
import groovy.util.logging.Log4j2;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
public class BrandService {

    private static final Logger log = LogManager.getLogger(BrandService.class);
    private final BrandRepository BrandRepository;
    private final ModelMapper modelMapper;
    private final ImageService imageService;

    //hotel 등록
    public void insert(BrandDTO brandDTO,
                       List<MultipartFile> multipartFiles) throws Exception {
        Brand brand = modelMapper.map(brandDTO, Brand.class);
        Brand brand1 = BrandRepository.save(brand);

        //이미지
        if(multipartFiles != null && multipartFiles.size() > 0) {
            imageService.saveImage(multipartFiles, "brand", brand1.getBrand_num());
        }

    }

    //chief 목록
    public List<BrandDTO> brandList(){
        List<Brand> brands = BrandRepository.findAll();
        List<BrandDTO> brandDTOS = brands.stream()
                .map(chief -> modelMapper.map(chief, BrandDTO.class)).collect(Collectors.toList());
        return brandDTOS;
    }

    //chief 상세보기
    public BrandDTO read(Long id) {
        Brand brand = BrandRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        BrandDTO brandDTO = modelMapper.map(brand, BrandDTO.class);
                    //.setItemImgDTOList(item.getItemImgList());
        return brandDTO;}


        //Optional<Chief> chief = chiefRepository.findById(id);
        //ChiefDTO chiefDTO = modelMapper.map(chief, ChiefDTO.class);
        //return chiefDTO;

    //chief 수정
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

       /* Optional<Chief> chief = chiefRepository.findById(chiefDTOList.getChief_num());
        if(chief.isPresent()){
            Chief chiefs = modelMapper.map(chiefDTOList, Chief.class);
            chiefRepository.save(chiefs);
        } */
    }
    //chief 삭제
    public void delete(Long id){
        log.info("서비스로 들어온 삭제할 번호 :"+id);

        BrandRepository.deleteById(id);
    }


}
