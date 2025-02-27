package com.lookatme.smartstay.service;

import com.lookatme.smartstay.repository.CareRepository;
import com.lookatme.smartstay.repository.ImageRepository;
import com.lookatme.smartstay.repository.MenuRepository;
import com.lookatme.smartstay.repository.OrderReserveRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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



}
