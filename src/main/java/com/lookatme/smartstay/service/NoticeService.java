package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.NoticeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface NoticeService {

    //등록
    public void noticeRegister(NoticeDTO noticeDTO);

    //사진을 추가한 등록
    public void register(NoticeDTO noticeDTO,List<MultipartFile> multipartFileList) throws Exception;

    //상세보기
    public NoticeDTO noticeRead(Long notice_num);

    //목록
   // public List<NoticeDTO> noticeList();

    //목록 , 페이징(전체 조회)
    public Page<NoticeDTO> noticeList(Pageable pageable);
//    public PageResponseDTO<NoticeDTO> pagelist(PageRequestDTO pageRequestDTO);

    //페이징처리 검색 동적처리
//    public PageResponseDTO<NoticeDTO> pageListsearchdsl(PageRequestDTO pageRequestDTO);

    //수정
    public void noticeModify(NoticeDTO noticeDTO, List<MultipartFile> multipartFileList, List<Long> delnumList);

    //삭제
    public void noticeDelete(Long notice_num);

}



