package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.ChiefDTO;
import com.lookatme.smartstay.dto.ManagerDTO;
import com.lookatme.smartstay.entity.Chief;
import com.lookatme.smartstay.entity.Manager;
import com.lookatme.smartstay.repository.ChiefRepository;
import com.lookatme.smartstay.repository.ManagerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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

    //manager 등록
    public void managerInsert(ManagerDTO managerDTO){
        Manager manager = modelMapper.map(managerDTO, Manager.class);

        managerRepository.save(manager);
    }

    //Manager 목록
    public List<ManagerDTO> managerList(){
        List<Manager> managers = managerRepository.findAll();
        List<ManagerDTO> managerDTOS =managers.stream().map(manager->modelMapper.map(manager, ManagerDTO.class)).collect(Collectors.toList());
        return managerDTOS;
    }

    //manager 수정
    public void managerUpdate(ManagerDTO managerDTO){
        Optional<Manager> manager = managerRepository.findById(managerDTO.getManager_num());
        if(manager.isPresent()) {
            Manager managers = modelMapper.map(managerDTO, Manager.class);
            managerRepository.save(managers);
        }
    }

    //chief 삭제
    public void managerDelete(Long manager_num){managerRepository.deleteById(manager_num); }

}
