package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.HotelDTO;
import com.lookatme.smartstay.dto.ImageDTO;
import com.lookatme.smartstay.dto.MenuDTO;
import com.lookatme.smartstay.entity.Care;
import com.lookatme.smartstay.entity.Image;
import com.lookatme.smartstay.entity.Menu;
import com.lookatme.smartstay.entity.OrderReserve;
import com.lookatme.smartstay.repository.CareRepository;
import com.lookatme.smartstay.repository.ImageRepository;
import com.lookatme.smartstay.repository.MenuRepository;
import com.lookatme.smartstay.repository.OrderReserveRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class OrderReserveService {
    private final OrderReserveRepository orderReserveRepository;
    private final MenuRepository menuRepository;
    private final CareRepository careRepository;
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;

    public List<MenuDTO> getAllMenus() {
        List<Menu> menuList = menuRepository.findAllMenus();
        List<MenuDTO> menuDTOList = menuList.stream()
                .map(menu -> modelMapper.map(menu, MenuDTO.class)
                        .setHotelDTO(modelMapper.map(menu.getHotel(), HotelDTO.class)))
                .collect(Collectors.toList());

        for (MenuDTO menuDTO : menuDTOList) {
            List<Image> menuImageList = imageRepository.findByTarget("menu", menuDTO.getMenu_num());
            if (!menuImageList.isEmpty()) {
                List<ImageDTO> menuImageDTOList = menuImageList.stream()
                        .map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList());
                menuDTO.setImageDTOList(menuImageDTOList);
            } else {
                menuDTO.setImageDTOList(null);
            }
        }

        return menuDTOList;
    }

    public List<Care> getAllCares() {
        return careRepository.findAllCares();
    }

    public List<OrderReserve> getOrdersByMemberEmail(String email) {
        return orderReserveRepository.findOrdersByMemberEmail(email);
    }

}
