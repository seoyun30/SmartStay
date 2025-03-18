package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.entity.*;
import com.lookatme.smartstay.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final RoomReserveItemRepository roomReserveItemRepository;
    private final MenuRepository menuRepository;
    private final CareRepository careRepository;
    private final CareItemRepository careItemRepository;
    private final ImageRepository imageRepository;
    private final MenuItemRepository menuItemRepository;
    private final ModelMapper modelMapper;

    public OrderItemDTO modifyOrderItem(OrderItemDTO orderItemDTO) {

        OrderItem orderItem = orderItemRepository.findById(orderItemDTO.getService_num())
                .orElseThrow(EntityNotFoundException::new);

        orderItem.setMenu_request(orderItemDTO.getMenu_request());

        if (orderItemDTO.getCareItemDTOList() != null) {
            for (CareItemDTO careItemDTO : orderItemDTO.getCareItemDTOList()) {
                CareItem careItem = careItemRepository.findById(careItemDTO.getCareitem_num())
                        .orElseThrow(EntityNotFoundException::new);
                careItem.setCare_count(careItemDTO.getCare_count());
                careItemRepository.save(careItem);
            }
        }

        if (orderItemDTO.getMenuItemDTOList() != null) {
            for (MenuItemDTO menuItemDTO : orderItemDTO.getMenuItemDTOList()) {
                MenuItem menuItem = menuItemRepository.findById(menuItemDTO.getMenuitem_num())
                        .orElseThrow(EntityNotFoundException::new);
                menuItem.setMenu_count(menuItemDTO.getMenu_count());
                menuItemRepository.save(menuItem);
            }
        }

        orderItem = orderItemRepository.save(orderItem);
        return modelMapper.map(orderItem, OrderItemDTO.class);
    }

    public OrderItemDTO findOrderItemDTO (OrderItemDTO orderItemDTO) {

        OrderItemDTO resultDTO = new OrderItemDTO();
        resultDTO.setRoomreserveitem_num(orderItemDTO.getRoomreserveitem_num());
        resultDTO.setMenu_request(orderItemDTO.getMenu_request());

        RoomReserveItem roomReserveItem =
                roomReserveItemRepository.findById(orderItemDTO.getRoomreserveitem_num())
                .orElseThrow(EntityNotFoundException::new);

        RoomReserveItemDTO roomReserveItemDTO =
                modelMapper.map(roomReserveItem, RoomReserveItemDTO.class)
                    .setRoomDTO(modelMapper.map(roomReserveItem.getRoom(), RoomDTO.class)
                        .setHotelDTO(modelMapper.map(roomReserveItem.getRoom().getHotel(), HotelDTO.class))
                    );

       resultDTO.setRoomReserveItemDTO(roomReserveItemDTO);

        // 메뉴 목록 설정
        if (orderItemDTO.getMenuItemDTOList() != null && !orderItemDTO.getMenuItemDTOList().isEmpty()) {
            List<MenuItemDTO> menuItemDTOList = new ArrayList<>();

            for (MenuItemDTO menuItemDTO : orderItemDTO.getMenuItemDTOList()) {
                Menu menu = menuRepository.findById(menuItemDTO.getMenuDTO().getMenu_num())
                        .orElseThrow(() -> new EntityNotFoundException("메뉴가 존재하지 않습니다."));

                MenuItemDTO newMenuItemDTO = new MenuItemDTO();
                newMenuItemDTO.setMenuDTO(modelMapper.map(menu, MenuDTO.class));
                newMenuItemDTO.setMenu_count(menuItemDTO.getMenu_count());

                // 메뉴 이미지 추가
                List<Image> imageList = imageRepository.findByTarget("menu", menu.getMenu_num());
                if (!imageList.isEmpty()) {
                    List<ImageDTO> imageDTOList = imageList.stream()
                            .map(image -> modelMapper.map(image, ImageDTO.class))
                            .collect(Collectors.toList());
                    newMenuItemDTO.setImageDTOList(imageDTOList);
                }

                menuItemDTOList.add(newMenuItemDTO);
            }

            resultDTO.setMenuItemDTOList(menuItemDTOList);
        }

        // 케어 서비스 목록 설정
        if (orderItemDTO.getCareItemDTOList() != null && !orderItemDTO.getCareItemDTOList().isEmpty()) {
            List<CareItemDTO> careItemDTOList = new ArrayList<>();

            for (CareItemDTO careItemDTO : orderItemDTO.getCareItemDTOList()) {
                Care care = careRepository.findById(careItemDTO.getCareDTO().getCare_num())
                        .orElseThrow(() -> new EntityNotFoundException("케어 서비스가 존재하지 않습니다."));

                CareItemDTO newCareItemDTO = new CareItemDTO();
                newCareItemDTO.setCareDTO(modelMapper.map(care, CareDTO.class));
                newCareItemDTO.setCare_count(careItemDTO.getCare_count());

                // 케어 서비스 이미지 추가
                List<Image> imageList = imageRepository.findByTarget("care", care.getCare_num());
                if (!imageList.isEmpty()) {
                    List<ImageDTO> imageDTOList = imageList.stream()
                            .map(image -> modelMapper.map(image, ImageDTO.class))
                            .collect(Collectors.toList());
                    newCareItemDTO.setImageDTOList(imageDTOList);
                }

                careItemDTOList.add(newCareItemDTO);
            }

            resultDTO.setCareItemDTOList(careItemDTOList);
        }

        return resultDTO;
    }

}
