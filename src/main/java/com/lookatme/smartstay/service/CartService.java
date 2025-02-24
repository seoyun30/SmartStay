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

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final RoomItemRepository roomItemRepository;
    private final RoomReserveItemRepository roomReserveItemRepository;
    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;
    private final CareRepository careRepository;
    private final CareItemRepository careItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;


    //룸예약 장바구니 등록
    public Long addCartRoomItem(RoomItemDTO roomItemDTO, String email) {

        log.info("장바구니 서비스 email : " + email);
        log.info("장바구니 서비스 roomItemDTO : " + roomItemDTO);

        Member member = memberRepository.findByEmail(email);
        log.info("장바구니 서비스 member : " + member);

        Room room = roomRepository.findById(roomItemDTO.getRoom_num())
                .orElseThrow(EntityNotFoundException::new);
        log.info("장바구니 서비스 room : " + room);

        Cart cart = cartRepository.findByMemberEmail(email);

        if (cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }
        log.info("장바구니 서비스 cart : " + cart);

//        List<RoomItem> savedRoomItems =
//                roomItemRepository.findByCart_Cart_numAndRoom_num(cart.getCart_num(), room.getRoom_num());
//
//        RoomItem savedRoomItem = savedRoomItems.get(0);

        //장바구니에 이미 있다면
//        if (savedRoomItem != null) {
//            return savedRoomItem.getRoomitem_num(); // 이미 있으면 기존 roomItem 반환
//        } else {
//
//        }

        RoomItem roomItem =
                RoomItem.createRoomItem(cart, room, roomItemDTO.getIn_date(),
                        roomItemDTO.getOut_date(), roomItemDTO.getDay(), roomItemDTO.getReserve_request(), roomItemDTO.getCount()
                );

        roomItemRepository.save(roomItem);

        return roomItem.getRoomitem_num();
    }

    //룸서비스 장바구니 등록
    public Long addCartOrderItem(OrderItemDTO orderItemDTO, String email) {
        log.info("장바구니 서비스 email : " + email);
        log.info("장바구니 서비스 orderItemDTO : " + orderItemDTO);

        // 회원 정보 조회
        Member member = memberRepository.findByEmail(email);
        log.info("장바구니 서비스 member : " + member);

        RoomReserveItem roomReserveItem =
                roomReserveItemRepository.findByReserveItemNumAndEmail(orderItemDTO.getRoomreserveitem_num(), email);

        // 장바구니 조회 (없으면 생성)
        Cart cart = cartRepository.findByMemberEmail(email);
        if (cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }
        log.info("장바구니 서비스 cart : " + cart);

        // OrderItem 생성
        OrderItem orderItem = OrderItem.builder()
                .cart(cart)
                .menu_request(orderItemDTO.getMenu_request())
                .roomReserveItem(roomReserveItem)
                .build();

        // 메뉴 추가
        if (orderItemDTO.getMenuItemDTOList() != null && !orderItemDTO.getMenuItemDTOList().isEmpty()) {
            for (MenuItemDTO menuItemDTO : orderItemDTO.getMenuItemDTOList()) {
                Menu menu = menuRepository.findById(menuItemDTO.getMenuDTO().getMenu_num())
                        .orElseThrow(() -> new EntityNotFoundException("메뉴가 존재하지 않습니다."));
                MenuItem menuItem = new MenuItem();
                menuItem.setMenu(menu);
                menuItem.setOrderItem(orderItem);
                menuItem.setMenu_count(menuItemDTO.getMenu_count());
                menuItemRepository.save(menuItem);
                orderItem.getMenuItemList().add(menuItem);
            }
        }

        // 케어 서비스 추가
        if (orderItemDTO.getCareItemDTOList() != null && !orderItemDTO.getCareItemDTOList().isEmpty()) {
            for (CareItemDTO careItemDTO : orderItemDTO.getCareItemDTOList()) {
                Care care = careRepository.findById(careItemDTO.getCareDTO().getCare_num())
                        .orElseThrow(() -> new EntityNotFoundException("케어 서비스가 존재하지 않습니다."));
                CareItem careItem = new CareItem();
                careItem.setCare(care);
                careItem.setOrderItem(orderItem);
                careItem.setCare_count(careItemDTO.getCare_count());
                careItemRepository.save(careItem);
                orderItem.getCareItemList().add(careItem);
            }
        }

        orderItem = orderItemRepository.save(orderItem);
        return orderItem.getService_num();

    }

    //룸예약 장바구니 조회
    public List<RoomItemDTO> getCartRoomItemList (String email) {
        List<RoomItemDTO> roomItemDTOList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);

        Cart cart = cartRepository.findByMemberEmail(email);

        log.info(cart);

        if (cart == null) {
            return roomItemDTOList;
        }

        List<RoomItem> roomItemList = roomItemRepository.findByCart_Cart_num(cart.getCart_num());

        roomItemList.forEach(roomItem -> {log.info("서비스 roomItem : " + roomItem);});

        roomItemDTOList = roomItemList.stream()
                .map(roomItem -> modelMapper.map(roomItem, RoomItemDTO.class)
                        .setRoomDTO(modelMapper.map(roomItem.getRoom(), RoomDTO.class)
                                .setHotelDTO(modelMapper.map(roomItem.getRoom().getHotel(), HotelDTO.class))))
                .collect(Collectors.toList());

        for (RoomItemDTO roomItemDTO : roomItemDTOList) {
            List<Image> imageList = imageRepository.findByTarget("room", roomItemDTO.getRoomDTO().getRoom_num());

            if (imageList != null && !imageList.isEmpty()) {
                List<ImageDTO> imageDTOList = imageList.stream()
                        .map(image -> modelMapper.map(image, ImageDTO.class))
                        .collect(Collectors.toList());

                List<Long> imageIdList = imageDTOList.stream()
                        .map(ImageDTO::getImage_id)
                        .collect(Collectors.toList());

                roomItemDTO.setImageDTOList(imageDTOList);
                roomItemDTO.setImageIdList(imageIdList);
            }
        }

        roomItemDTOList.forEach(roomItemDTO -> {log.info("서비스 roomItemDTO : " + roomItemDTO);});

        return roomItemDTOList;
    }

    //룸서비스 장바구니 조회
    public List<OrderItemDTO> getCartOrderItemList (String email) {
        List<OrderItemDTO> orderItemDTOList = new ArrayList<>();
        Map<Long, OrderItemDTO> orderItemDTOMap = new HashMap<>();

        Member member = memberRepository.findByEmail(email);

        Cart cart = cartRepository.findByMemberEmail(email);

        log.info(cart);

        if (cart == null) {
            return orderItemDTOList;
        }

        List<OrderItem> orderItemList = orderItemRepository.findByCart_Cart_num(cart.getCart_num());
        orderItemList.forEach(orderItem -> {
            log.info("서비스 orderItem : " + orderItem);
        });

        for (OrderItem orderItem : orderItemList) {
            Long serviceNum = orderItem.getService_num();
            OrderItemDTO orderItemDTO = orderItemDTOMap.getOrDefault(serviceNum,
                    modelMapper.map(orderItem, OrderItemDTO.class)
                    .setRoomReserveItemDTO(modelMapper.map(orderItem.getRoomReserveItem(), RoomReserveItemDTO.class)
                            .setRoomDTO(modelMapper.map(orderItem.getRoomReserveItem().getRoom(), RoomDTO.class))
                    )
            );
            log.info("매핑된 orderItemDTO: {}", orderItemDTO);

            // 케어 아이템 처리
            List<CareItem> careItemList = careItemRepository.findByOrderItemService_num(serviceNum);
            if (careItemList != null && !careItemList.isEmpty()) {
                careItemList.forEach(careItem -> log.info(careItem));
                List<CareItemDTO> careItemDTOList = careItemList.stream()
                        .map(careItem -> modelMapper.map(careItem, CareItemDTO.class)
                                .setCareDTO(modelMapper.map(careItem.getCare(), CareDTO.class))
                                .setHotelDTO(modelMapper.map(careItem.getCare().getHotel(), HotelDTO.class)))
                        .collect(Collectors.toList());

                careItemDTOList.forEach(careItemDTO -> {
                    List<Image> imageList = imageRepository.findByTarget("care", careItemDTO.getCareDTO().getCare_num());
                    if (imageList != null && !imageList.isEmpty()) {
                        List<ImageDTO> imageDTOList = imageList.stream()
                                .map(image -> modelMapper.map(image, ImageDTO.class))
                                .collect(Collectors.toList());

                        List<Long> imageIdList = imageDTOList.stream()
                                .map(ImageDTO::getImage_id)
                                .collect(Collectors.toList());

                        careItemDTO.setImageDTOList(imageDTOList);
                        careItemDTO.setImageIdList(imageIdList);
                    }
                });

                orderItemDTO.getCareItemDTOList().addAll(careItemDTOList);
                log.info(orderItemDTO);
            }

            // 메뉴 아이템 처리
            List<MenuItem> menuItemList = menuItemRepository.findByOrderItemService_num(serviceNum);
            if (menuItemList != null && !menuItemList.isEmpty()) {
                menuItemList.forEach(menuItem -> log.info(menuItem));
                List<MenuItemDTO> menuItemDTOList = menuItemList.stream()
                        .map(menuItem -> modelMapper.map(menuItem, MenuItemDTO.class)
                                .setMenuDTO(modelMapper.map(menuItem.getMenu(), MenuDTO.class))
                                .setHotelDTO(modelMapper.map(menuItem.getMenu().getHotel(), HotelDTO.class)))
                        .collect(Collectors.toList());

                menuItemDTOList.forEach(menuItemDTO -> {
                    List<Image> imageList = imageRepository.findByTarget("menu", menuItemDTO.getMenuDTO().getMenu_num());
                    if (imageList != null && !imageList.isEmpty()) {
                        List<ImageDTO> imageDTOList = imageList.stream()
                                .map(image -> modelMapper.map(image, ImageDTO.class))
                                .collect(Collectors.toList());

                        List<Long> imageIdList = imageDTOList.stream()
                                .map(ImageDTO::getImage_id)
                                .collect(Collectors.toList());

                        menuItemDTO.setImageDTOList(imageDTOList);
                        menuItemDTO.setImageIdList(imageIdList);
                    }
                });

                orderItemDTO.getMenuItemDTOList().addAll(menuItemDTOList);
                log.info(orderItemDTO);
            }
            orderItemDTOMap.put(serviceNum, orderItemDTO);
        }
        orderItemDTOList.addAll(orderItemDTOMap.values());
        orderItemDTOList.forEach(orderItemDTO -> log.info("서비스 최종 : " + orderItemDTO));

        return orderItemDTOList;
    }

    //자신의 룸예약이 맞는지 확인
    public boolean validateCartRoomItem(Long roomitem_num, String email) {
        log.info("서비스 roomitem_num : " + roomitem_num);
        log.info("서비스 email : " + email);

        Member member = memberRepository.findByEmail(email);
        RoomItem roomItem = roomItemRepository.findById(roomitem_num).orElseThrow(EntityNotFoundException::new);

        if (member != null && roomItem != null) {

            if ( !member.getEmail().equals(roomItem.getCart().getMember().getEmail()) ) {
                return false; //다르다
            }
        }

        return true; //같다
    }

    //자신의 룸서비스가 맞는지 확인
    public boolean validateCartOrderItem(Long service_num, String email) {
        log.info("서비스 service_num : " + service_num);
        log.info("서비스 email : " + email);

        Member member = memberRepository.findByEmail(email);
        OrderItem orderItem = orderItemRepository.findById(service_num).orElseThrow(EntityNotFoundException::new);

        if (member != null && orderItem != null) {

            if ( !member.getEmail().equals(orderItem.getCart().getMember().getEmail()) ) {
                return false; //다르다
            }
        }

        return true; //같다
    }

    //장바구니의 룸예약 삭제
    public void deleteCartRoomItem(Long roomitem_num) {

        RoomItem roomItem = roomItemRepository.findById(roomitem_num).orElseThrow(EntityNotFoundException::new);

        roomItemRepository.delete(roomItem);

    }

    @Transactional
    //장바구니의 룸서비스 삭제
    public void deleteCartOrderItem(Long service_num) {

        OrderItem orderItem = orderItemRepository.findById(service_num).orElseThrow(EntityNotFoundException::new);

        // 연관된 MenuItem, CareItem만 삭제
        orderItem.getMenuItemList().forEach(menuItem -> {
            menuItem.setOrderItem(null);  // menuItem에서 orderItem 참조 끊기
            menuItemRepository.delete(menuItem);  // MenuItem 삭제
        });
        orderItem.getCareItemList().forEach(careItem -> {
            careItem.setOrderItem(null);  // careItem에서 orderItem 참조 끊기
            careItemRepository.delete(careItem);  // CareItem 삭제
        });

        orderItemRepository.delete(orderItem);

    }



    //장바구니의 룸예약 상품 찾기
    public List<RoomItemDTO> findCartOrderRoomItem(Long[] roomitem_nums) {

        //현재 룸예약만 진행중 차후 수정 필요
        List<RoomItemDTO> roomItemDTOList = new ArrayList<>();

        for (Long roomitem_num : roomitem_nums) {
           RoomItem roomItem = roomItemRepository.findById(roomitem_num)
                   .orElseThrow(EntityNotFoundException::new);
           roomItemDTOList.add(
                   modelMapper.map(roomItem, RoomItemDTO.class)
                           .setRoomDTO(modelMapper.map(roomItem.getRoom(), RoomDTO.class)
                                   .setHotelDTO(modelMapper.map(roomItem.getRoom().getHotel(), HotelDTO.class)))
           );
        }

        for (RoomItemDTO roomItemDTO : roomItemDTOList) {
            List<Image> imageList = imageRepository.findByTarget("room", roomItemDTO.getRoomDTO().getRoom_num());

            if (imageList != null && !imageList.isEmpty()) {
                List<ImageDTO> imageDTOList = imageList.stream()
                        .map(image -> modelMapper.map(image, ImageDTO.class))
                        .collect(Collectors.toList());

                List<Long> imageIdList = imageDTOList.stream()
                        .map(ImageDTO::getImage_id)
                        .collect(Collectors.toList());

                roomItemDTO.setImageDTOList(imageDTOList);
                roomItemDTO.setImageIdList(imageIdList);
            }

            if (roomItemDTO.getIn_date() != null && roomItemDTO.getOut_date() != null) {
                long dayDifference = ChronoUnit.DAYS.between(roomItemDTO.getIn_date(), roomItemDTO.getOut_date());
                roomItemDTO.setDay((Long) dayDifference);
            }
        }

        return roomItemDTOList;
    }

}
