package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.CareItemDTO;
import com.lookatme.smartstay.entity.CareItem;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.repository.CareItemRepository;
import com.lookatme.smartstay.repository.MemberRepository;
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
public class CareItemService {

    private final CareItemRepository careItemRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    //자신의 케어아이템이 맞는지 확인
    public boolean validateCareItem(Long careitem_num, String email) {
        log.info("서비스 careitem_num : " + careitem_num);
        log.info("서비스 email : " + email);

        Member member = memberRepository.findByEmail(email);
        CareItem careItem = careItemRepository.findById(careitem_num).orElseThrow(EntityNotFoundException::new);

        if (member != null && careItem != null) {

            if ( !member.getEmail().equals(careItem.getOrderItem().getCart().getMember().getEmail()) ) {
                return false; //다르다
            }
        }

        return true; //같다
    }

    @Transactional
    public void deleteCareItem(Long careitem_num) {
        careItemRepository.deleteById(careitem_num);
    }

    public CareItemDTO modifyCareItemCount(CareItemDTO careItemDTO) {
        CareItem careItem = careItemRepository.findById(careItemDTO.getCareitem_num())
                .orElseThrow(EntityNotFoundException::new);

        careItem.setCare_count(careItemDTO.getCare_count());

        careItem = careItemRepository.save(careItem);
        return modelMapper.map(careItem, CareItemDTO.class);
    }
}
