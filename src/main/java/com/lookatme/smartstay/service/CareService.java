package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.CareDTO;
import com.lookatme.smartstay.entity.Care;
import com.lookatme.smartstay.repository.CareRepository;
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
@Transactional
@RequiredArgsConstructor
@Log4j2

public class CareService {

    private final CareRepository careRepository;
    private final ModelMapper modelMapper;
    private final ImageService imageService;

    public Long careRegister(CareDTO careDTO, List<MultipartFile> multipartFiles) {

        Care care = modelMapper.map(careDTO, Care.class);

        care = careRepository.save(care);

        return care.getCare_num();
    }

    public CareDTO careRead(Long care_num) {

        Care care = careRepository.findById(care_num).orElseThrow(EntityNotFoundException::new);

        CareDTO careDTO = modelMapper.map(care, CareDTO.class);

        return careDTO;
    }

    public List<CareDTO> careList() {

        List<Care> careList = careRepository.findAll();
        List<CareDTO> careDTOList = careList.stream()
                .map(care -> modelMapper.map(care, CareDTO.class))
                .collect(Collectors.toList());

        return careDTOList;
    }

    public CareDTO careModify(CareDTO careDTO, Long care_num, List<MultipartFile> multipartFiles) {

        Care care = careRepository.findById(careDTO.getCare_num()).orElseThrow(EntityNotFoundException::new);

        care.setCare_name(careDTO.getCare_name());
        care.setCare_detail(careDTO.getCare_detail());
        care.setCare_price(careDTO.getCare_price());

        care = careRepository.save(care);

        return modelMapper.map(care, CareDTO.class);
    }

    public void careDelete(Long id) {

        careRepository.deleteById(id);
    }

}
