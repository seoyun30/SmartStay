package com.lookatme.smartstay.service;

import com.lookatme.smartstay.Util.PagenationUtil;
import com.lookatme.smartstay.dto.ChiefDTO;
import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.dto.PageResponseDTO;
import com.lookatme.smartstay.entity.Chief;
import com.lookatme.smartstay.repository.ChiefRepository;
import groovy.util.logging.Log4j2;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.dynamic.loading.ByteArrayClassLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public void insert(ChiefDTO chiefDTO,
                       List<MultipartFile> multipartFiles) throws Exception {
        Chief chief = modelMapper.map(chiefDTO, Chief.class);
        Chief chief1 = chiefRepository.save(chief);

        //이미지
        if(multipartFiles != null && multipartFiles.size() > 0) {
            imageService.saveImage(multipartFiles, "chief", chief1.getChief_num());
        }

    }

    //chief 목록
    public List<ChiefDTO> chiefList(){
        List<Chief> chiefs = chiefRepository.findAll();
        List<ChiefDTO> chiefDTOS = chiefs.stream()
                .map(chief -> modelMapper.map(chief, ChiefDTO.class)).collect(Collectors.toList());
        return chiefDTOS;
    }

    //chief 상세보기
    public ChiefDTO read(Long id) {
        Chief chief=chiefRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        ChiefDTO chiefDTO = modelMapper.map(chief, ChiefDTO.class);
                    //.setItemImgDTOList(item.getItemImgList());
        return chiefDTO;}


        //Optional<Chief> chief = chiefRepository.findById(id);
        //ChiefDTO chiefDTO = modelMapper.map(chief, ChiefDTO.class);
        //return chiefDTO;

    //chief 수정
    public void update(ChiefDTO chiefDTO,
                       List<MultipartFile> multipartFiles) throws Exception{
        Chief chief = chiefRepository.findById(chiefDTO.getChief_num())
                .orElseThrow(EntityNotFoundException::new);
        //set
        chief.setChief_num(chiefDTO.getChief_num());
        chief.setHotel_name(chiefDTO.getHotel_name());
        chief.setBusiness_num(chiefDTO.getBusiness_num());
        chief.setOwner(chiefDTO.getOwner());
        chief.setAddress(chiefDTO.getAddress());
        chief.setTel(chiefDTO.getTel());
        chief.setScore(chiefDTO.getScore());

        chiefRepository.save(chief);

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



    public List<ChiefDTO> mainHotel() {
        List<Chief> chiefList = chiefRepository.findAll();
        List<ChiefDTO> chiefDTOList = chiefList.stream()
                .map(chief -> modelMapper.map(chief, ChiefDTO.class))
                .collect(Collectors.toList());

        return chiefDTOList;
    }







}
