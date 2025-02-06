package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.NoticeDTO;
import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.dto.PageResponseDTO;
import com.lookatme.smartstay.entity.Notice;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface NoticeService {

    //등록
    public void noticeRegister(NoticeDTO noticeDTO);

    //사진을 추가한 등록
    public void register(NoticeDTO noticeDTO,List<MultipartFile> multipartFileList) throws Exception;

    //상세보기
    public NoticeDTO noticeRead(Long id);

    //목록
    public List<NoticeDTO> noticeList();

    //페이징처리된 목록
    public PageResponseDTO<NoticeDTO> pagelist(PageRequestDTO pageRequestDTO);

    //페이징처리 검색 동적처리
    public PageResponseDTO<NoticeDTO> pageListsearchdsl(PageRequestDTO pageRequestDTO);

    //수정
    public void noticeModify(NoticeDTO noticeDTO, List<MultipartFile> multipartFileList);

    //삭제
    public void noticeDelete(Long notice_num);

}



