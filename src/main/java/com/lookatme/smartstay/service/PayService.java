package com.lookatme.smartstay.service;

import com.lookatme.smartstay.constant.CheckState;
import com.lookatme.smartstay.dto.PayDTO;
import com.lookatme.smartstay.dto.PrePayDTO;
import com.lookatme.smartstay.dto.RoomItemDTO;
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
    private MemberRepository memberRepository;

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
        }

        return iamportResponse.getResponse();
    }

    //결제 취소 메소드
    public CancelData cancelPayment(IamportResponse<Payment> response) {
        return new CancelData(response.getResponse().getImpUid(), true);
    }

    @Transactional
    public void savePayInfo(PayDTO payDTO) {

        // 1. 결제 정보를 Pay 엔티티로 변환
        Member member = memberRepository.findByEmail(payDTO.getMemberDTO().getEmail());
        Pay pay = modelMapper.map(payDTO, Pay.class);
        pay.setMember(member);

        // 2. 예약 정보 생성
        List<RoomReserveItem> roomReserveItems = new ArrayList<>();

        log.info("for문 진행");
        for (RoomItemDTO roomItemDTO : payDTO.getRoomItemDTOList()) {

            // 예약할 방 찾기
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

        // 예약 항목을 RoomReserve에 추가 : roomReserveItems.forEach(roomReserveItemRepository::save);
        //roomReserveItemRepository::save = item -> roomReserveItemRepository.save(item)
        roomReserveItemRepository.saveAll(roomReserveItems);

        // 결제 정보에 예약 정보 추가 후 저장
        pay.setRoomReserveItemList(roomReserveItems);
        payRepository.save(pay);

    }

}
