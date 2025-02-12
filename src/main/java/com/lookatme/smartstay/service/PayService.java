package com.lookatme.smartstay.service;

import com.lookatme.smartstay.constant.CheckState;
import com.lookatme.smartstay.dto.PayDTO;
import com.lookatme.smartstay.dto.PrePayDTO;
import com.lookatme.smartstay.dto.RoomItemDTO;
import com.lookatme.smartstay.entity.Pay;
import com.lookatme.smartstay.entity.PrePay;
import com.lookatme.smartstay.entity.RoomItem;
import com.lookatme.smartstay.entity.RoomReserve;
import com.lookatme.smartstay.repository.PayRepository;
import com.lookatme.smartstay.repository.PrePayRepository;
import com.lookatme.smartstay.repository.RoomItemRepository;
import com.lookatme.smartstay.repository.RoomReserveRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional
public class PayService {

    @Autowired
    private PayRepository payRepository;

    @Autowired
    private PrePayRepository prePayRepository;

    @Autowired
    private RoomItemRepository roomItemRepository;

    @Autowired
    private RoomReserveRepository roomReserveRepository;

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

    public void savePayInfo(PayDTO payDTO) {

        List<RoomItemDTO> roomItemDTOList = payDTO.getRoomItemDTOList();
        List<RoomItem> roomItemList = roomItemDTOList.stream()
                .map(roomItemDTO -> modelMapper.map(roomItemDTO, RoomItem.class))
                .collect(Collectors.toList());
        Pay pay = modelMapper.map(payDTO, Pay.class);
        pay.setRoomItemList(roomItemList);

        //결제 완료시 장바구니의 룸예약 삭제
        for (RoomItemDTO roomItemDTO : roomItemDTOList) {
            RoomItem roomItem = roomItemRepository.findByRoomRoom_num(roomItemDTO.getRoom_num());
            //예약 방식 수정 필요
            RoomReserve roomReserve = new RoomReserve();
            roomReserve.setMember(pay.getMember());

            String date = LocalDate.now().toString().replace("-","");
            String num = roomItem.getRoom().getRoom_num().toString();
            String reserveId = date + "-" + num + "-" + pay.getMember().getMember_num().toString();
            roomReserve.setReserve_id(reserveId); //오늘날짜-방번호-회원번호

            roomReserve.setRoomItemList(roomItem);
            roomReserve.setCheck_state(CheckState.RESERVE);
            roomReserve.setReg_date(LocalDateTime.now());


            roomItemRepository.delete(roomItem);
        }

        //차후 장바구니의 룸서비스 예약도 삭제

        payRepository.save(pay);

    }
}
