package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.BrandDTO;
import com.lookatme.smartstay.entity.Brand;
import com.lookatme.smartstay.repository.ChiefRepository;
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
public class ChiefService {

    private static final Logger log = LogManager.getLogger(ChiefService.class);
    private final ChiefRepository chiefRepository;
    private final ModelMapper modelMapper;
    private final ImageService imageService;

    //chief 등록
    public void insert(BrandDTO brandDTO,
                       List<MultipartFile> multipartFiles) throws Exception {
        Brand brand = modelMapper.map(brandDTO, Brand.class);
        Brand brand1 = chiefRepository.save(brand);

        //이미지
        if(multipartFiles != null && multipartFiles.size() > 0) {
            imageService.saveImage(multipartFiles, "chief", brand1.getChief_num());
        }

    }

    //chief 목록
    public List<BrandDTO> chiefList(){
        List<Brand> brands = chiefRepository.findAll();
        List<BrandDTO> brandDTOS = brands.stream()
                .map(chief -> modelMapper.map(chief, BrandDTO.class)).collect(Collectors.toList());
        return brandDTOS;
    }

    //chief 상세보기
    public BrandDTO read(Long id) {
        Brand brand =chiefRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        BrandDTO brandDTO = modelMapper.map(brand, BrandDTO.class);
                    //.setItemImgDTOList(item.getItemImgList());
        return brandDTO;}


        //Optional<Chief> chief = chiefRepository.findById(id);
        //ChiefDTO chiefDTO = modelMapper.map(chief, ChiefDTO.class);
        //return chiefDTO;

    //chief 수정
    public void update(BrandDTO brandDTO,
                       List<MultipartFile> multipartFiles) throws Exception{
        Brand brand = chiefRepository.findById(brandDTO.getChief_num())
                .orElseThrow(EntityNotFoundException::new);
        //set
        brand.setChief_num(brandDTO.getChief_num());
        brand.setHotel_name(brandDTO.getHotel_name());
        brand.setBusiness_num(brandDTO.getBusiness_num());
        brand.setOwner(brandDTO.getOwner());
        brand.setAddress(brandDTO.getAddress());
        brand.setTel(brandDTO.getTel());
        brand.setScore(brandDTO.getScore());

        chiefRepository.save(brand);

       /* Optional<Chief> chief = chiefRepository.findById(chiefDTOList.getChief_num());
        if(chief.isPresent()){
            Chief chiefs = modelMapper.map(chiefDTOList, Chief.class);
            chiefRepository.save(chiefs);
        } */
    }
    //chief 삭제
    public void delete(Long id){
        log.info("서비스로 들어온 삭제할 번호 :"+id);

        chiefRepository.deleteById(id);
    }


}
