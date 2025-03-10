package com.lookatme.smartstay.service;

import com.lookatme.smartstay.constant.OrderState;
import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.entity.*;
import com.lookatme.smartstay.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class OrderReserveService {
    private final OrderReserveRepository orderReserveRepository;
    private final OrderReserveItemRepository orderReserveItemRepository;
    private final MenuRepository menuRepository;
    private final MenuReserveItemRepository menuReserveItemRepository;
    private final CareRepository careRepository;
    private final CareReserveItemRepository careReserveItemRepository;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;

    //내 주문이 맞는지 확인
    public boolean validateorderResereve(Long order_num, String email) {

        Member member = memberRepository.findByEmail(email);
        OrderReserve orderReserve =
                orderReserveRepository.findById(order_num)
                        .orElseThrow(EntityNotFoundException::new);

        //주문id의 회원 email과 현재 로그인한 사람 정보 비교
        if (!StringUtils.equals(member.getEmail(), orderReserve.getMember().getEmail())) {
            return false;
        }

        return true;

    }

    //룸으로 룸서비스 조회
    //관리자 호텔로 룸서비스 조회
    public PageResponseDTO<OrderReserveItemDTO> findOrderReservePage (String email, PageRequestDTO pageRequestDTO) {

        Member member = memberRepository.findByEmail(email);
        log.info("현재 로그인한 회원의 호텔 번호: {}", member.getHotel().getHotel_num());

        Pageable pageable = pageRequestDTO.getPageable("serviceitem_num");

        Page<OrderReserveItem> result = orderReserveItemRepository.findByHotelPage(member.getHotel().getHotel_num(), pageable);

        List<OrderReserveItemDTO> orderReserveItemDTOList = result.stream()
                .map(orderReserveItem -> modelMapper.map(orderReserveItem, OrderReserveItemDTO.class)
                        .setOrderReserveDTO(modelMapper.map(orderReserveItem.getOrderReserve(), OrderReserveDTO.class)
                                .setMemberDTO(modelMapper.map(orderReserveItem.getOrderReserve().getMember(), MemberDTO.class)))
                        .setPayDTO(modelMapper.map(orderReserveItem.getPay(), PayDTO.class))
                        .setRoomReserveItemDTO(modelMapper.map(orderReserveItem.getRoomReserveItem(), RoomReserveItemDTO.class)
                                .setRoomDTO(modelMapper.map(orderReserveItem.getRoomReserveItem().getRoom(), RoomDTO.class)
                                        .setHotelDTO(modelMapper.map(orderReserveItem.getRoomReserveItem().getRoom().getHotel(),HotelDTO.class)
                                        )
                                )
                        )
                ).collect(Collectors.toList());

        List<MenuReserveItemDTO> menuReserveItemDTOList = new ArrayList<>();
        List<CareReserveItemDTO> careReserveItemDTOList = new ArrayList<>();

        for (OrderReserveItemDTO orderReserveItemDTO : orderReserveItemDTOList) {
            List<MenuReserveItem> menuReserveItemList = menuReserveItemRepository.findByOrderReserveItemServiceitem_num(orderReserveItemDTO.getServiceitem_num());
            menuReserveItemDTOList = menuReserveItemList.stream()
                    .map(menuReserveItem -> modelMapper.map(menuReserveItem, MenuReserveItemDTO.class)
                            .setMenuDTO(modelMapper.map(menuReserveItem.getMenu(), MenuDTO.class))
                    ).collect(Collectors.toList());
            for (MenuReserveItemDTO menuReserveItemDTO : menuReserveItemDTOList) {
                List<Image> imageList = imageRepository.findByTarget("menu", menuReserveItemDTO.getMenuDTO().getMenu_num());
                if (imageList != null && !imageList.isEmpty()) {
                    List<ImageDTO> imageDTOList = imageList.stream()
                            .map(image -> modelMapper.map(image, ImageDTO.class))
                            .collect(Collectors.toList());
                    menuReserveItemDTO.getMenuDTO().setImageDTOList(imageDTOList);

                    ImageDTO mainImage = imageDTOList.stream()
                            .filter(image -> "Y".equalsIgnoreCase(image.getRepimg_yn()))
                            .findFirst()
                            .orElse(imageDTOList.get(0));
                    menuReserveItemDTO.getMenuDTO().setMainImage(mainImage);
                }else {
                    log.warn("메뉴 번호:{}에 이미지가 없습니다.", menuReserveItemDTO.getMenuDTO().getMenu_num());
                }
            }

            List<CareReserveItem> careReserveItemList = careReserveItemRepository.findByOrderReserveItemServiceitem_num(orderReserveItemDTO.getServiceitem_num());
            careReserveItemDTOList = careReserveItemList.stream()
                    .map(careReserveItem -> modelMapper.map(careReserveItem, CareReserveItemDTO.class)
                            .setCareDTO(modelMapper.map(careReserveItem.getCare(), CareDTO.class))
                    ).collect(Collectors.toList());
            for (CareReserveItemDTO careReserveItemDTO : careReserveItemDTOList) {
                List<Image> imageList = imageRepository.findByTarget("care", careReserveItemDTO.getCareDTO().getCare_num());
                if (imageList != null && !imageList.isEmpty()) {
                    List<ImageDTO> imageDTOList = imageList.stream()
                            .map(image -> modelMapper.map(image, ImageDTO.class))
                            .collect(Collectors.toList());
                    careReserveItemDTO.getCareDTO().setImageDTOList(imageDTOList);

                    ImageDTO mainImage = imageDTOList.stream()
                            .filter(image -> "Y".equalsIgnoreCase(image.getRepimg_yn()))
                            .findFirst()
                            .orElse(imageDTOList.get(0));
                    careReserveItemDTO.getCareDTO().setMainImage(mainImage);
                }else {
                    log.warn("메뉴 번호:{}에 이미지가 없습니다.", careReserveItemDTO.getCareDTO().getCare_num());
                }
            }

            orderReserveItemDTO.setMenuReserveItemDTOList(menuReserveItemDTOList);
            orderReserveItemDTO.setCareReserveItemDTOList(careReserveItemDTOList);
        }

        if (orderReserveItemDTOList.isEmpty()) {
            orderReserveItemDTOList = Collections.emptyList();
        }

        PageResponseDTO<OrderReserveItemDTO> orderReserveItemDTOPageResponseDTO = PageResponseDTO.<OrderReserveItemDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(orderReserveItemDTOList)
                .total((int) result.getTotalElements())
                .build();

        return orderReserveItemDTOPageResponseDTO;
    }

    //관리자 룸서비스 주문 내역 조회 검색 추가
    public PageResponseDTO<OrderReserveItemDTO> findOrderReservePageSearch (String email, PageRequestDTO pageRequestDTO, ReserveSearchDTO reserveSearchDTO) {

        Pageable pageable = pageRequestDTO.getPageable("serviceitem_num");
        Member member = memberRepository.findByEmail(email);
        log.info("현재 로그인한 회원의 호텔 번호: {}", member.getHotel().getHotel_num());
        LocalDateTime sdate = null;
        LocalDateTime edate = null;
        OrderState orderState = null;

        if (reserveSearchDTO.getSdate() != null && !reserveSearchDTO.getSdate().toString().isEmpty()){
            sdate = reserveSearchDTO.getSdateAsLocalDateTime();
            log.info(sdate.toString());
        }
        if (reserveSearchDTO.getEdate() != null && !reserveSearchDTO.getEdate().toString().isEmpty()){
            edate = reserveSearchDTO.getEdateAsLocalDateTime();
            log.info(edate.toString());
        }
        if (reserveSearchDTO.getState() != null && !reserveSearchDTO.getState().isEmpty()) {
            orderState = OrderState.valueOf(reserveSearchDTO.getState());
        }


        Page<OrderReserveItem> result = orderReserveItemRepository.findOrderReserveBySearch(
                member.getHotel().getHotel_num(),
                reserveSearchDTO.getReserve_id(),
                reserveSearchDTO.getRoom_name(),
                reserveSearchDTO.getReserve_name(),
                orderState,
                sdate, edate, pageable);

        List<OrderReserveItemDTO> orderReserveItemDTOList = result.stream()
                .map(orderReserveItem -> modelMapper.map(orderReserveItem, OrderReserveItemDTO.class)
                        .setOrderReserveDTO(modelMapper.map(orderReserveItem.getOrderReserve(), OrderReserveDTO.class)
                                .setMemberDTO(modelMapper.map(orderReserveItem.getOrderReserve().getMember(), MemberDTO.class)))
                        .setPayDTO(modelMapper.map(orderReserveItem.getPay(), PayDTO.class))
                        .setRoomReserveItemDTO(modelMapper.map(orderReserveItem.getRoomReserveItem(), RoomReserveItemDTO.class)
                                .setRoomDTO(modelMapper.map(orderReserveItem.getRoomReserveItem().getRoom(), RoomDTO.class)
                                        .setHotelDTO(modelMapper.map(orderReserveItem.getRoomReserveItem().getRoom().getHotel(),HotelDTO.class)
                                        )
                                )
                        )
                ).collect(Collectors.toList());

        List<MenuReserveItemDTO> menuReserveItemDTOList = new ArrayList<>();
        List<CareReserveItemDTO> careReserveItemDTOList = new ArrayList<>();

        for (OrderReserveItemDTO orderReserveItemDTO : orderReserveItemDTOList) {
            List<MenuReserveItem> menuReserveItemList = menuReserveItemRepository.findByOrderReserveItemServiceitem_num(orderReserveItemDTO.getServiceitem_num());
            menuReserveItemDTOList = menuReserveItemList.stream()
                    .map(menuReserveItem -> modelMapper.map(menuReserveItem, MenuReserveItemDTO.class)
                            .setMenuDTO(modelMapper.map(menuReserveItem.getMenu(), MenuDTO.class))
                    ).collect(Collectors.toList());
            for (MenuReserveItemDTO menuReserveItemDTO : menuReserveItemDTOList) {
                List<Image> imageList = imageRepository.findByTarget("menu", menuReserveItemDTO.getMenuDTO().getMenu_num());
                if (imageList != null && !imageList.isEmpty()) {
                    List<ImageDTO> imageDTOList = imageList.stream()
                            .map(image -> modelMapper.map(image, ImageDTO.class))
                            .collect(Collectors.toList());
                    menuReserveItemDTO.getMenuDTO().setImageDTOList(imageDTOList);

                    ImageDTO mainImage = imageDTOList.stream()
                            .filter(image -> "Y".equalsIgnoreCase(image.getRepimg_yn()))
                            .findFirst()
                            .orElse(imageDTOList.get(0));
                    menuReserveItemDTO.getMenuDTO().setMainImage(mainImage);
                }else {
                    log.warn("메뉴 번호:{}에 이미지가 없습니다.", menuReserveItemDTO.getMenuDTO().getMenu_num());
                }
            }

            List<CareReserveItem> careReserveItemList = careReserveItemRepository.findByOrderReserveItemServiceitem_num(orderReserveItemDTO.getServiceitem_num());
            careReserveItemDTOList = careReserveItemList.stream()
                    .map(careReserveItem -> modelMapper.map(careReserveItem, CareReserveItemDTO.class)
                            .setCareDTO(modelMapper.map(careReserveItem.getCare(), CareDTO.class))
                    ).collect(Collectors.toList());
            for (CareReserveItemDTO careReserveItemDTO : careReserveItemDTOList) {
                List<Image> imageList = imageRepository.findByTarget("care", careReserveItemDTO.getCareDTO().getCare_num());
                if (imageList != null && !imageList.isEmpty()) {
                    List<ImageDTO> imageDTOList = imageList.stream()
                            .map(image -> modelMapper.map(image, ImageDTO.class))
                            .collect(Collectors.toList());
                    careReserveItemDTO.getCareDTO().setImageDTOList(imageDTOList);

                    ImageDTO mainImage = imageDTOList.stream()
                            .filter(image -> "Y".equalsIgnoreCase(image.getRepimg_yn()))
                            .findFirst()
                            .orElse(imageDTOList.get(0));
                    careReserveItemDTO.getCareDTO().setMainImage(mainImage);
                }else {
                    log.warn("메뉴 번호:{}에 이미지가 없습니다.", careReserveItemDTO.getCareDTO().getCare_num());
                }
            }

            orderReserveItemDTO.setMenuReserveItemDTOList(menuReserveItemDTOList);
            orderReserveItemDTO.setCareReserveItemDTOList(careReserveItemDTOList);
        }

        if (orderReserveItemDTOList.isEmpty()) {
            orderReserveItemDTOList = Collections.emptyList();
        }

        PageResponseDTO<OrderReserveItemDTO> orderReserveItemDTOPageResponseDTO = PageResponseDTO.<OrderReserveItemDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(orderReserveItemDTOList)
                .total((int) result.getTotalElements())
                .build();

        return orderReserveItemDTOPageResponseDTO;
    }

    //회원의 룸서비스 주문 내역 조회
    public PageResponseDTO<OrderReserveItemDTO> findMyOrderPage (String email, PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("serviceitem_num");

        Page<OrderReserveItem> result = orderReserveItemRepository.findByEmailPage(email, pageable);

        List<OrderReserveItemDTO> orderReserveItemDTOList = result.stream()
                .map(orderReserveItem -> modelMapper.map(orderReserveItem, OrderReserveItemDTO.class)
                        .setOrderReserveDTO(modelMapper.map(orderReserveItem.getOrderReserve(), OrderReserveDTO.class)
                                .setMemberDTO(modelMapper.map(orderReserveItem.getOrderReserve().getMember(), MemberDTO.class)))
                        .setPayDTO(modelMapper.map(orderReserveItem.getPay(), PayDTO.class))
                        .setRoomReserveItemDTO(modelMapper.map(orderReserveItem.getRoomReserveItem(), RoomReserveItemDTO.class)
                                .setRoomDTO(modelMapper.map(orderReserveItem.getRoomReserveItem().getRoom(), RoomDTO.class)
                                        .setHotelDTO(modelMapper.map(orderReserveItem.getRoomReserveItem().getRoom().getHotel(),HotelDTO.class)
                                        )
                                )
                        )
                ).collect(Collectors.toList());

        List<MenuReserveItemDTO> menuReserveItemDTOList = new ArrayList<>();
        List<CareReserveItemDTO> careReserveItemDTOList = new ArrayList<>();

        for (OrderReserveItemDTO orderReserveItemDTO : orderReserveItemDTOList) {
            List<MenuReserveItem> menuReserveItemList = menuReserveItemRepository.findByOrderReserveItemServiceitem_num(orderReserveItemDTO.getServiceitem_num());
            menuReserveItemDTOList = menuReserveItemList.stream()
                    .map(menuReserveItem -> modelMapper.map(menuReserveItem, MenuReserveItemDTO.class)
                    .setMenuDTO(modelMapper.map(menuReserveItem.getMenu(), MenuDTO.class))
                    ).collect(Collectors.toList());
            for (MenuReserveItemDTO menuReserveItemDTO : menuReserveItemDTOList) {
                List<Image> imageList = imageRepository.findByTarget("menu", menuReserveItemDTO.getMenuDTO().getMenu_num());
                if (imageList != null && !imageList.isEmpty()) {
                    List<ImageDTO> imageDTOList = imageList.stream()
                            .map(image -> modelMapper.map(image, ImageDTO.class))
                            .collect(Collectors.toList());
                    menuReserveItemDTO.getMenuDTO().setImageDTOList(imageDTOList);

                    ImageDTO mainImage = imageDTOList.stream()
                            .filter(image -> "Y".equalsIgnoreCase(image.getRepimg_yn()))
                            .findFirst()
                            .orElse(imageDTOList.get(0));
                    menuReserveItemDTO.getMenuDTO().setMainImage(mainImage);
                }else {
                    log.warn("메뉴 번호:{}에 이미지가 없습니다.", menuReserveItemDTO.getMenuDTO().getMenu_num());
                }
            }

            List<CareReserveItem> careReserveItemList = careReserveItemRepository.findByOrderReserveItemServiceitem_num(orderReserveItemDTO.getServiceitem_num());
            careReserveItemDTOList = careReserveItemList.stream()
                    .map(careReserveItem -> modelMapper.map(careReserveItem, CareReserveItemDTO.class)
                            .setCareDTO(modelMapper.map(careReserveItem.getCare(), CareDTO.class))
                    ).collect(Collectors.toList());
            for (CareReserveItemDTO careReserveItemDTO : careReserveItemDTOList) {
                List<Image> imageList = imageRepository.findByTarget("care", careReserveItemDTO.getCareDTO().getCare_num());
                if (imageList != null && !imageList.isEmpty()) {
                    List<ImageDTO> imageDTOList = imageList.stream()
                            .map(image -> modelMapper.map(image, ImageDTO.class))
                            .collect(Collectors.toList());
                    careReserveItemDTO.getCareDTO().setImageDTOList(imageDTOList);

                    ImageDTO mainImage = imageDTOList.stream()
                            .filter(image -> "Y".equalsIgnoreCase(image.getRepimg_yn()))
                            .findFirst()
                            .orElse(imageDTOList.get(0));
                    careReserveItemDTO.getCareDTO().setMainImage(mainImage);
                }else {
                    log.warn("메뉴 번호:{}에 이미지가 없습니다.", careReserveItemDTO.getCareDTO().getCare_num());
                }
            }

            orderReserveItemDTO.setMenuReserveItemDTOList(menuReserveItemDTOList);
            orderReserveItemDTO.setCareReserveItemDTOList(careReserveItemDTOList);
        }

        if (orderReserveItemDTOList.isEmpty()) {
            orderReserveItemDTOList = Collections.emptyList();
        }

        PageResponseDTO<OrderReserveItemDTO> orderReserveItemDTOPageResponseDTO = PageResponseDTO.<OrderReserveItemDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(orderReserveItemDTOList)
                .total((int) result.getTotalElements())
                .build();

        return orderReserveItemDTOPageResponseDTO;
    }

    //회원의 룸서비스 주문 내역 상세보기
    public OrderReserveItemDTO findOrderReserveItem(Long serviceitem_num, String email) {
        OrderReserveItem orderReserveItem = orderReserveItemRepository.findByServiceitemNumAndEmail(serviceitem_num, email);

        OrderReserveItemDTO orderReserveItemDTO = modelMapper.map(orderReserveItem, OrderReserveItemDTO.class)
                .setOrderReserveDTO(modelMapper.map(orderReserveItem.getOrderReserve(), OrderReserveDTO.class)
                        .setMemberDTO(modelMapper.map(orderReserveItem.getOrderReserve().getMember(), MemberDTO.class)))
                .setPayDTO(modelMapper.map(orderReserveItem.getPay(), PayDTO.class))
                .setRoomReserveItemDTO(modelMapper.map(orderReserveItem.getRoomReserveItem(), RoomReserveItemDTO.class)
                        .setRoomDTO(modelMapper.map(orderReserveItem.getRoomReserveItem().getRoom(), RoomDTO.class)
                                .setHotelDTO(modelMapper.map(orderReserveItem.getRoomReserveItem().getRoom().getHotel(),HotelDTO.class)
                                )
                        )
                );

        List<MenuReserveItem> menuReserveItemList = menuReserveItemRepository.findByOrderReserveItemServiceitem_num(orderReserveItemDTO.getServiceitem_num());
        List<MenuReserveItemDTO> menuReserveItemDTOList = menuReserveItemList.stream()
                .map(menuReserveItem -> modelMapper.map(menuReserveItem, MenuReserveItemDTO.class)
                        .setMenuDTO(modelMapper.map(menuReserveItem.getMenu(), MenuDTO.class))
                ).collect(Collectors.toList());
        for (MenuReserveItemDTO menuReserveItemDTO : menuReserveItemDTOList) {
            List<Image> imageList = imageRepository.findByTarget("menu", menuReserveItemDTO.getMenuDTO().getMenu_num());
            if (imageList != null && !imageList.isEmpty()) {
                List<ImageDTO> imageDTOList = imageList.stream()
                        .map(image -> modelMapper.map(image, ImageDTO.class))
                        .collect(Collectors.toList());
                menuReserveItemDTO.getMenuDTO().setImageDTOList(imageDTOList);

                ImageDTO mainImage = imageDTOList.stream()
                        .filter(image -> "Y".equalsIgnoreCase(image.getRepimg_yn()))
                        .findFirst()
                        .orElse(imageDTOList.get(0));
                menuReserveItemDTO.getMenuDTO().setMainImage(mainImage);
            }else {
                log.warn("메뉴 번호:{}에 이미지가 없습니다.", menuReserveItemDTO.getMenuDTO().getMenu_num());
            }
        }

        List<CareReserveItem> careReserveItemList = careReserveItemRepository.findByOrderReserveItemServiceitem_num(orderReserveItemDTO.getServiceitem_num());
        List<CareReserveItemDTO> careReserveItemDTOList = careReserveItemList.stream()
                .map(careReserveItem -> modelMapper.map(careReserveItem, CareReserveItemDTO.class)
                        .setCareDTO(modelMapper.map(careReserveItem.getCare(), CareDTO.class))
                ).collect(Collectors.toList());
        for (CareReserveItemDTO careReserveItemDTO : careReserveItemDTOList) {
            List<Image> imageList = imageRepository.findByTarget("care", careReserveItemDTO.getCareDTO().getCare_num());
            if (imageList != null && !imageList.isEmpty()) {
                List<ImageDTO> imageDTOList = imageList.stream()
                        .map(image -> modelMapper.map(image, ImageDTO.class))
                        .collect(Collectors.toList());
                careReserveItemDTO.getCareDTO().setImageDTOList(imageDTOList);

                ImageDTO mainImage = imageDTOList.stream()
                        .filter(image -> "Y".equalsIgnoreCase(image.getRepimg_yn()))
                        .findFirst()
                        .orElse(imageDTOList.get(0));
                careReserveItemDTO.getCareDTO().setMainImage(mainImage);
            }else {
                log.warn("메뉴 번호:{}에 이미지가 없습니다.", careReserveItemDTO.getCareDTO().getCare_num());
            }
        }

        orderReserveItemDTO.setMenuReserveItemDTOList(menuReserveItemDTOList);
        orderReserveItemDTO.setCareReserveItemDTOList(careReserveItemDTOList);

        return orderReserveItemDTO;
    }

    //관리자 룸서비스 주문 내역 상세보기
    public OrderReserveItemDTO findOrderReserveItemAd(Long serviceitem_num) {
        OrderReserveItem orderReserveItem = orderReserveItemRepository.findById(serviceitem_num)
                .orElseThrow(EntityNotFoundException::new);

        OrderReserveItemDTO orderReserveItemDTO = modelMapper.map(orderReserveItem, OrderReserveItemDTO.class)
                .setOrderReserveDTO(modelMapper.map(orderReserveItem.getOrderReserve(), OrderReserveDTO.class)
                        .setMemberDTO(modelMapper.map(orderReserveItem.getOrderReserve().getMember(), MemberDTO.class)))
                .setPayDTO(modelMapper.map(orderReserveItem.getPay(), PayDTO.class))
                .setRoomReserveItemDTO(modelMapper.map(orderReserveItem.getRoomReserveItem(), RoomReserveItemDTO.class)
                        .setRoomDTO(modelMapper.map(orderReserveItem.getRoomReserveItem().getRoom(), RoomDTO.class)
                                .setHotelDTO(modelMapper.map(orderReserveItem.getRoomReserveItem().getRoom().getHotel(),HotelDTO.class)
                                )
                        )
                );

        List<MenuReserveItem> menuReserveItemList = menuReserveItemRepository.findByOrderReserveItemServiceitem_num(orderReserveItemDTO.getServiceitem_num());
        List<MenuReserveItemDTO> menuReserveItemDTOList = menuReserveItemList.stream()
                .map(menuReserveItem -> modelMapper.map(menuReserveItem, MenuReserveItemDTO.class)
                        .setMenuDTO(modelMapper.map(menuReserveItem.getMenu(), MenuDTO.class))
                ).collect(Collectors.toList());
        for (MenuReserveItemDTO menuReserveItemDTO : menuReserveItemDTOList) {
            List<Image> imageList = imageRepository.findByTarget("menu", menuReserveItemDTO.getMenuDTO().getMenu_num());
            if (imageList != null && !imageList.isEmpty()) {
                List<ImageDTO> imageDTOList = imageList.stream()
                        .map(image -> modelMapper.map(image, ImageDTO.class))
                        .collect(Collectors.toList());
                menuReserveItemDTO.getMenuDTO().setImageDTOList(imageDTOList);

                ImageDTO mainImage = imageDTOList.stream()
                        .filter(image -> "Y".equalsIgnoreCase(image.getRepimg_yn()))
                        .findFirst()
                        .orElse(imageDTOList.get(0));
                menuReserveItemDTO.getMenuDTO().setMainImage(mainImage);
            }else {
                log.warn("메뉴 번호:{}에 이미지가 없습니다.", menuReserveItemDTO.getMenuDTO().getMenu_num());
            }
        }

        List<CareReserveItem> careReserveItemList = careReserveItemRepository.findByOrderReserveItemServiceitem_num(orderReserveItemDTO.getServiceitem_num());
        List<CareReserveItemDTO> careReserveItemDTOList = careReserveItemList.stream()
                .map(careReserveItem -> modelMapper.map(careReserveItem, CareReserveItemDTO.class)
                        .setCareDTO(modelMapper.map(careReserveItem.getCare(), CareDTO.class))
                ).collect(Collectors.toList());
        for (CareReserveItemDTO careReserveItemDTO : careReserveItemDTOList) {
            List<Image> imageList = imageRepository.findByTarget("care", careReserveItemDTO.getCareDTO().getCare_num());
            if (imageList != null && !imageList.isEmpty()) {
                List<ImageDTO> imageDTOList = imageList.stream()
                        .map(image -> modelMapper.map(image, ImageDTO.class))
                        .collect(Collectors.toList());
                careReserveItemDTO.getCareDTO().setImageDTOList(imageDTOList);

                ImageDTO mainImage = imageDTOList.stream()
                        .filter(image -> "Y".equalsIgnoreCase(image.getRepimg_yn()))
                        .findFirst()
                        .orElse(imageDTOList.get(0));
                careReserveItemDTO.getCareDTO().setMainImage(mainImage);
            }else {
                log.warn("메뉴 번호:{}에 이미지가 없습니다.", careReserveItemDTO.getCareDTO().getCare_num());
            }
        }

        orderReserveItemDTO.setMenuReserveItemDTOList(menuReserveItemDTOList);
        orderReserveItemDTO.setCareReserveItemDTOList(careReserveItemDTOList);

        return orderReserveItemDTO;
    }

    //주문 상태 변경
    public OrderReserveDTO stateChange(OrderReserveDTO orderReserveDTO) {
        OrderReserve orderReserve = orderReserveRepository.findById(orderReserveDTO.getOrder_num())
                .orElseThrow(EntityNotFoundException::new);

        orderReserve.setOrder_state(orderReserveDTO.getOrder_state());

        orderReserve = orderReserveRepository.save(orderReserve);
        return modelMapper.map(orderReserve, OrderReserveDTO.class);
    }
}
