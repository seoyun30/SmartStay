package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.MenuItemDTO;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.MenuItem;
import com.lookatme.smartstay.repository.MemberRepository;
import com.lookatme.smartstay.repository.MenuItemRepository;
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
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    //자신의 케어아이템이 맞는지 확인
    public boolean validateMenuItem(Long menuitem_num, String email) {
        log.info("서비스 menuitem_num : " + menuitem_num);
        log.info("서비스 email : " + email);

        Member member = memberRepository.findByEmail(email);
        MenuItem menuItem = menuItemRepository.findById(menuitem_num).orElseThrow(EntityNotFoundException::new);

        if (member != null && menuItem != null) {

            if ( !member.getEmail().equals(menuItem.getOrderItem().getCart().getMember().getEmail()) ) {
                return false; //다르다
            }
        }

        return true; //같다
    }

    @Transactional
    //케어 서비스 삭제
    public void deleteMenuItem(Long menuitem_num) {

        menuItemRepository.deleteById(menuitem_num);

    }

    public MenuItemDTO modifyMenuItemCount(MenuItemDTO menuItemDTO) {
        MenuItem menuItem = menuItemRepository.findById(menuItemDTO.getMenuitem_num())
                .orElseThrow(EntityNotFoundException::new);

        menuItem.setMenu_count(menuItemDTO.getMenu_count());

        menuItem = menuItemRepository.save(menuItem);
        return modelMapper.map(menuItem, MenuItemDTO.class);
    }

}
