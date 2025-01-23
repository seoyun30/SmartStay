package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.ManagerDTO;
import com.lookatme.smartstay.entity.Manager;
import com.lookatme.smartstay.repository.ManagerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final ModelMapper modelMapper;
    private final ImageService imageService;

    //manager 등록
    public void managerInsert(ManagerDTO managerDTO,
                              List<MultipartFile> multipartFiles) throws Exception{
        Manager manager = modelMapper.map(managerDTO, Manager.class);
        Manager manager1 = managerRepository.save(manager);

        //이미지
        if(multipartFiles != null && multipartFiles.size() > 0) {
            imageService.saveImage(multipartFiles, "manager", manager1.getManager_num());
        }

    }

    //manager 목록
    public List<ManagerDTO> managerList(){
        List<Manager> managers = managerRepository.findAll();
        List<ManagerDTO> managerDTOS =managers.stream()
                .map(manager->modelMapper.map(manager, ManagerDTO.class)).collect(Collectors.toList());
        return managerDTOS;
    }

    //manager 상세보기
    public ManagerDTO managerRead(Long id) {
        Manager manager = managerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        ManagerDTO managerDTO = modelMapper.map(manager, ManagerDTO.class);

        return managerDTO;
    }

    //manager 수정
    public void managerUpdate(ManagerDTO managerDTO,
                              List<MultipartFile> multipartFiles) throws Exception{
        Manager manager = managerRepository.findById(managerDTO.getManager_num())
                .orElseThrow(EntityNotFoundException::new);

        //set
        manager.setManager_num(managerDTO.getManager_num());
        manager.setHotel_name(managerDTO.getHotel_name());
        manager.setBusiness_num(managerDTO.getBusiness_num());
        manager.setOwner(managerDTO.getOwner());
        manager.setAddress(managerDTO.getAddress());
        manager.setTel(managerDTO.getTel());
        manager.setScore(managerDTO.getScore());

        managerRepository.save(manager);
    }

    //manager 삭제
    public void managerDelete(Long id){
        log.info("서비스로 들어온 삭제될 번호: "+id);

        managerRepository.deleteById(id); }




    public List<ManagerDTO> mainHotel() {

        List<Manager> managerList = managerRepository.findAll();
        List<ManagerDTO> managerDTOList = managerList.stream()
                .map(manager -> modelMapper.map(manager, ManagerDTO.class))
                .collect(Collectors.toList());

        return managerDTOList;
    }

    public List<ManagerDTO> hotelList() {

        List<Manager> managers = managerRepository.findAll();
        List<ManagerDTO> managerDTOList = managers.stream()
                .map(manager -> modelMapper.map(manager, ManagerDTO.class))
                .collect(Collectors.toList());

        return managerDTOList;
    }

}
