package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.CareItemDTO;
import com.lookatme.smartstay.dto.MenuItemDTO;
import com.lookatme.smartstay.dto.OrderItemDTO;
import com.lookatme.smartstay.entity.CareItem;
import com.lookatme.smartstay.entity.MenuItem;
import com.lookatme.smartstay.entity.OrderItem;
import com.lookatme.smartstay.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final RoomReserveItemRepository roomReserveItemRepository;
    private final MenuRepository menuRepository;
    private final MenuItemRepository MenuItemRepository;
    private final CareRepository careRepository;
    private final CareItemRepository careItemRepository;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final CareItemService careItemService;
    private final MenuItemService menuItemService;
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

}
