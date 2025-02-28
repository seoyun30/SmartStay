package com.lookatme.smartstay.service;


import com.lookatme.smartstay.dto.QnaDTO;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

public interface QnaService {

    //등록
    public void register(QnaDTO qnaDTO, MultipartFile[] multipartFiles);

    //읽기
    public QnaDTO read(Long qna_num);

    //목록
    public List<QnaDTO> list();

    //페이징처리된 목록 나중에
    //public PageResponseDTO<QnaDTO> pagelist(PageRequestDTO pageRequestDTO);

    //페이징처리 ok 검색 동적처리 ok 나중에
    //public PageResponseDTO<QnaDTO> pageListsearchdsl(PageRequestDTO pageRequestDTO);

    //페이지 처리 참고
    //pagelist와 pageListsearchdsl 메서드에서 반환 타입이 PageResponseDTO<QnaDTO>로 되어 있는데,
    // 페이징 처리를 위해서는 PageRequestDTO나 PageResponseDTO를 잘 설계해야 합니다.
    // 이 DTO들이 어떻게 정의되고 처리될지에 대한 정의가 필요합니다.

    //수정
    public void modify(QnaDTO qnaDTO);
    //, MultipartFile[] multipartFiles, Long[] delino==>나중에 이미지 추가시 메소드추가

    //삭제
    public void del(Long qna_num);

    //조회수
    public void incrementViewCount(Long qna_num);


}
