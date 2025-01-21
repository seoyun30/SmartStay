package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.NoticeDTO;
import com.lookatme.smartstay.entity.Notice;
import com.lookatme.smartstay.repository.NoticeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final ModelMapper modelMapper;

    //공지 사항 목록
    public List<NoticeDTO> noticeList(){
        List<Notice> notices = noticeRepository.findAll();

        List<NoticeDTO> noticeDTOS = Arrays.asList(modelMapper.map(notices, NoticeDTO[].class));

        return noticeDTOS;
    }
    //공지 사항 등록
    public void noticeRegister(NoticeDTO noticeDTO){
        Notice notice = modelMapper.map(noticeDTO, Notice.class);

        noticeRepository.save(notice);
    }

    //공지 사항 상세보기
    public NoticeDTO noticeRead(Long id){
        Optional<Notice> notice = noticeRepository.findById(id);

        NoticeDTO noticeDTO = modelMapper.map(notice, NoticeDTO.class);
        return noticeDTO;
    }

    //공지 사항 수정
    public void noticeModify(NoticeDTO noticeDTO){

        Optional<Notice> notice = noticeRepository.findById(noticeDTO.getNotice_num());

        if (notice.isPresent()) {
            Notice notices = modelMapper.map(noticeDTO, Notice.class);

            noticeRepository.save(notices);
        }
    }

    //공지 사항 삭제
    public void noticeDelete(Long id){
        noticeRepository.deleteById(id);
    }

    //공지 사항 이미지

}
