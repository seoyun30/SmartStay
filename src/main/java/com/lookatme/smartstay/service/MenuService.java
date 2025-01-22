package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.MenuDTO;
import com.lookatme.smartstay.entity.Menu;
import com.lookatme.smartstay.repository.MenuRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2

public class MenuService {

    private final MenuRepository menuRepository;
    private final ModelMapper modelMapper;
    private final ImageService imageService;

    public Long menuRegister(MenuDTO menuDTO) {

        Menu menu = modelMapper.map(menuDTO, Menu.class);

        menu = menuRepository.save(menu);

        return menu.getMenu_num();
    }

    public MenuDTO menuRead(Long menu_num) {

        Menu menu = menuRepository.findById(menu_num).orElseThrow(EntityNotFoundException::new);

        MenuDTO menuDTO = modelMapper.map(menu, MenuDTO.class);

        return menuDTO;
    }

    public List<MenuDTO> menuList() {

        List<Menu> menuList = menuRepository.findAll();
        List<MenuDTO> menuDTOList = menuList.stream()
                .map(menu -> modelMapper.map(menu, MenuDTO.class))
                .collect(Collectors.toList());

        return menuDTOList;
    }

    public MenuDTO menuModify(MenuDTO menuDTO) {

        Menu menu = menuRepository.findById(menuDTO.getMenu_num()).orElseThrow(EntityNotFoundException::new);

        menu.setMenu_name(menuDTO.getMenu_name());
        menu.setService_info(menuDTO.getService_info());
        menu.setMenu_detail(menuDTO.getMenu_detail());
        menu.setMenu_price(menuDTO.getMenu_price());

        menu = menuRepository.save(menu);

        return modelMapper.map(menu, MenuDTO.class);
    }
}
