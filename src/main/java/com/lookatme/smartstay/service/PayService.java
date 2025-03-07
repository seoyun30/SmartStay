package com.lookatme.smartstay.service;

import com.lookatme.smartstay.constant.CheckState;
import com.lookatme.smartstay.constant.OrderState;
import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.entity.*;
import com.lookatme.smartstay.repository.*;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
@Transactional
public class PayService {

    @Autowired
    private PayRepository payRepository;

    @Autowired
    private PrePayRepository prePayRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomItemRepository roomItemRepository;

    @Autowired
    private RoomReserveItemRepository roomReserveItemRepository;

    @Autowired
    private RoomReserveRepository roomReserveRepository;

    @Autowired
    private OrderReserveRepository orderReserveRepository;

    @Autowired
    private OrderReserveItemRepository orderReserveItemRepository;

    @Autowired
    private CareRepository careRepository;

    @Autowired
    private CareItemRepository careItemRepository;

    @Autowired
    private CareReserveItemRepository careReserveItemRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private MenuReserveItemRepository menuReserveItemRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ModelMapper modelMapper;

    private IamportClient api;

    public PayService(PayRepository payRepository) {
        this.api = new IamportClient("5837026262078121", "b2FLX18d3ZFdM2vX6RNbEMUGOfi3QAV6WJ27bn4nX4uYQgLFWD28CW1xDLxDOaDOetyij4RXmkfkDhi1");
    }

    //결제 사전 검증
    public void postPrepare(PrePayDTO prePayDTO) throws IamportResponseException, IOException {

        PrepareData prepareData = new PrepareData(prePayDTO.getMerchant_uid(), prePayDTO.getAmount());
        api.postPrepare(prepareData);  // 사전 등록 API

        PrePay prePay = modelMapper.map(prePayDTO, PrePay.class);
        prePayRepository.save(prePay); // 주문번호와 결제예정 금액 DB 저장
    }

    public Payment validatePay(PayDTO payDTO) throws IamportResponseException, IOException {
        PrePay prePay = prePayRepository.findById(payDTO.getMerchant_uid()).orElseThrow(EntityNotFoundException::new);
        BigDecimal preAmount = prePay.getAmount(); // DB에 저장된 결제요청 금액

        IamportResponse<Payment> iamportResponse = api.paymentByImpUid(payDTO.getImp_uid());
        BigDecimal paidAmount = iamportResponse.getResponse().getAmount(); // 사용자가 실제 결제한 금액

        if (!preAmount.equals(paidAmount)) { //결제 금액이 같지 않다면 결제 취소
            CancelData cancelData = cancelPayment(iamportResponse);
            api.cancelPaymentByImpUid(cancelData);
        } else {
            // 결제 검증 성공 시, 사전 결제 데이터 삭제
            prePayRepository.delete(prePay);
        }

        return iamportResponse.getResponse();
    }

    //결제 취소 메소드
    public CancelData cancelPayment(IamportResponse<Payment> response) {
        return new CancelData(response.getResponse().getImpUid(), true);
    }

    @Transactional
    public void savePayInfo(PayDTO payDTO) {
        // 사전 검증 데이터 삭제
        prePayRepository.findById(payDTO.getMerchant_uid()).ifPresent(prePayRepository::delete);

        //결제 정보를 Pay 엔티티로 변환
        Member member = memberRepository.findByEmail(payDTO.getMemberDTO().getEmail());
        Pay pay = modelMapper.map(payDTO, Pay.class);
        pay.setMember(member);

        //예약 정보 생성
        List<RoomReserveItem> roomReserveItems = new ArrayList<>();
        List<OrderReserveItem> orderReserveItems = new ArrayList<>();

        log.info("룸 예약 정보 저장 시작");
        for (RoomItemDTO roomItemDTO : payDTO.getRoomItemDTOList()) {
            Room room = roomRepository.findById(roomItemDTO.getRoom_num())
                    .orElseThrow(() -> new IllegalArgumentException("해당 방이 존재하지 않습니다. room_num: " + roomItemDTO.getRoom_num()));

            // 해당 방이 장바구니(RoomItem)에 있는지 확인하고 삭제 준비
            RoomItem roomItem = roomItemRepository.findByRoomRoom_num(roomItemDTO.getRoom_num());
            log.info(roomItem);
            if (roomItem != null) {
                roomItemRepository.delete(roomItem);
                roomItemRepository.flush();  //즉시 반영
            }

            // RoomReserve(예약) 생성 및 설정
            RoomReserve roomReserve = new RoomReserve();
            roomReserve.setMember(pay.getMember()); // 결제한 회원 정보와 연결
            roomReserve.setCheck_state(CheckState.RESERVE); // 예약 상태 설정
            roomReserve.setReg_date(LocalDateTime.now());
            roomReserve.setReserve_id(UUID.randomUUID().toString()); // 예약 ID 생성 (UUID 사용 36자 고정)

            log.info(roomReserve);
            // RoomReserve 저장
            roomReserve = roomReserveRepository.save(roomReserve);

            // RoomReserveItem 생성 및 설정
            RoomReserveItem roomReserveItem = RoomReserveItem.createRoomItem(
                    room,
                    roomItemDTO.getIn_date(),
                    roomItemDTO.getOut_date(),
                    roomItemDTO.getDay(),
                    roomItemDTO.getReserve_request(),
                    roomItemDTO.getCount()
            );
            roomReserveItem.setRoomReserve(roomReserve); // 예약 정보 연결
            roomReserveItem.setPay(pay); // 결제 정보 연결

            log.info(roomReserveItem);

            // 리스트에 추가 후 저장
            roomReserveItems.add(roomReserveItem);
        }
        log.info("룸 예약 정보 저장 완료");

        log.info("룸 서비스 정보 저장 시작");
        // 룸서비스 주문 이력 생성 및 설정
        List<MenuReserveItem> allMenuReserveItems = new ArrayList<>();
        List<CareReserveItem> allCareReserveItems = new ArrayList<>();

        for (OrderItemDTO orderItemDTO : payDTO.getOrderItemDTOList()) {

            if (orderItemDTO.getService_num() != null){
                cartService.deleteCartOrderItem(orderItemDTO.getService_num());
            }

            OrderReserve orderReserve = new OrderReserve();
            orderReserve.setMember(pay.getMember()); // 결제한 회원 정보와 연결
            orderReserve.setOrder_state(OrderState.ORDER); // 예약 상태 설정
            orderReserve.setOrder_id(UUID.randomUUID().toString()); // 예약 ID 생성 (UUID 사용 36자 고정)

            orderReserve = orderReserveRepository.save(orderReserve);

            OrderReserveItem orderReserveItem = new OrderReserveItem();
            orderReserveItem.setMenu_request(orderItemDTO.getMenu_request());

            RoomReserveItem roomReserveItem = roomReserveItemRepository.findById(orderItemDTO.getRoomreserveitem_num())
                    .orElseThrow(EntityNotFoundException::new);

            orderReserveItem.setRoomReserveItem(roomReserveItem);
            orderReserveItem.setPay(pay);
            orderReserveItem.setOrderReserve(orderReserve);
            orderReserveItems.add(orderReserveItem);

            orderReserveItemRepository.save(orderReserveItem);

            Long totalPrice = 0L;

                List<MenuReserveItem> menuReserveItems = new ArrayList<>();
                for (MenuItemDTO menuItemDTO : orderItemDTO.getMenuItemDTOList()) {
                    Menu menu = menuRepository.findById(menuItemDTO.getMenuDTO().getMenu_num())
                            .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 존재하지 않습니다. menu_num: " + menuItemDTO.getMenuDTO().getMenu_num()));


                    MenuReserveItem menuReserveItem = new MenuReserveItem();
                    menuReserveItem.setMenu(menu);
                    menuReserveItem.setMenu_count(menuItemDTO.getMenu_count());
                    menuReserveItem.setOrderReserveItem(orderReserveItem);
                    menuReserveItems.add(menuReserveItem);

                    totalPrice += menu.getMenu_price() * menuItemDTO.getMenu_count();
                }

                allMenuReserveItems.addAll(menuReserveItems);

                List<CareReserveItem> careReserveItems = new ArrayList<>();
                for (CareItemDTO careItemDTO : orderItemDTO.getCareItemDTOList()) {
                    Care care = careRepository.findById(careItemDTO.getCareDTO().getCare_num())
                            .orElseThrow(() -> new IllegalArgumentException("해당 케어 서비스가 존재하지 않습니다. care_num: " + careItemDTO.getCareDTO().getCare_num()));


                    CareReserveItem careReserveItem = new CareReserveItem();
                    careReserveItem.setCare(care);
                    careReserveItem.setCare_count(careItemDTO.getCare_count());
                    careReserveItem.setOrderReserveItem(orderReserveItem);
                    careReserveItems.add(careReserveItem);

                    totalPrice += care.getCare_price() * careItemDTO.getCare_count();
                }
                allCareReserveItems.addAll(careReserveItems);

                orderReserveItem.getMenuReserveItemList().clear();
                orderReserveItem.getMenuReserveItemList().addAll(menuReserveItems);
                orderReserveItem.getCareReserveItemList().clear();
                orderReserveItem.getCareReserveItemList().addAll(careReserveItems);

            orderReserve.setTotal_price(totalPrice);
            orderReserve.getOrderReserveItemList().clear();
            orderReserve.getOrderReserveItemList().add(orderReserveItem);
            orderReserveRepository.save(orderReserve);
        }

            menuReserveItemRepository.saveAll(allMenuReserveItems);
            careReserveItemRepository.saveAll(allCareReserveItems);

        roomReserveItemRepository.saveAll(roomReserveItems);
        orderReserveItemRepository.saveAll(orderReserveItems);

        // 결제 정보에 예약 정보 추가 후 저장
        pay.getRoomReserveItemList().clear();
        pay.getRoomReserveItemList().addAll(roomReserveItems);
        pay.getOrderReserveItemList().clear();
        pay.getOrderReserveItemList().addAll(orderReserveItems);
        payRepository.save(pay);

    }

}
