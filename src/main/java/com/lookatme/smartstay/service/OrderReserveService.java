package com.lookatme.smartstay.service;

import com.lookatme.smartstay.entity.Care;
import com.lookatme.smartstay.entity.Menu;
import com.lookatme.smartstay.entity.OrderReserve;
import com.lookatme.smartstay.repository.CareRepository;
import com.lookatme.smartstay.repository.MenuRepository;
import com.lookatme.smartstay.repository.OrderReserveRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class OrderReserveService {
    private final OrderReserveRepository orderReserveRepository;
    private final MenuRepository menuRepository;
    private final CareRepository careRepository;

    public List<Menu> getAllMenus() {
        return menuRepository.findAllMenus();
    }

    public List<Care> getAllCares() {
        return careRepository.findAllCares();
    }

    public List<OrderReserve> getOrdersByMemberEmail(String email) {
        return orderReserveRepository.findOrdersByMemberEmail(email);
    }

}
