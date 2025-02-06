package com.lookatme.smartstay.service;

import com.lookatme.smartstay.Util.FileUpload;
import com.lookatme.smartstay.dto.NoticeDTO;

import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.dto.PageResponseDTO;
import com.lookatme.smartstay.entity.Notice;
import com.lookatme.smartstay.repository.ImageRepository;
import com.lookatme.smartstay.repository.NoticeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final ModelMapper modelMapper;
    private final FileUpload fileUpload;
    private final ImageService imageService;

    //공지 사항 등록
    @Override
    public void noticeRegister(NoticeDTO noticeDTO){
        Notice notice = modelMapper.map(noticeDTO, Notice.class);

        noticeRepository.save(notice);
    }

    //사진을 추가한 등록
    @Override
    public void register(NoticeDTO noticeDTO, List<MultipartFile> multipartFileList) throws Exception {

        log.info("등록 서비스 들어온값: "+noticeDTO);
        log.info("등록 서비스 들어온값: "+multipartFileList);
        //글을 컨트롤러로부터 받아 entity변환헤서 저장
        Notice notice = modelMapper.map(noticeDTO, Notice.class);
        log.info("저장전에 noticeDTO를 notice로 변경한" + notice);

        notice = noticeRepository.save(notice);
        log.info("저장후에 결과를 가지고 있는notice" + notice);
        //본문을 저장하고 나서
        //사진 등록
        if (multipartFileList != null) {
            for (MultipartFile file : multipartFileList) {
                if ( !multipartFileList.isEmpty()) {
                    log.info("사진이 등록되었습니다.");
                    log.info("사진이 등록되었습니다.");
                    log.info("사진이 등록되었습니다.");
                    // 이미지 등록
                    imageService.saveImage(multipartFileList,"notice", notice.getNotice_num());
                }

            }
        }

    }

    //공지 사항 상세보기
    @Override
    public NoticeDTO noticeRead(Long id){
        Optional<Notice> notice = noticeRepository.findById(id);

        NoticeDTO noticeDTO = modelMapper.map(notice, NoticeDTO.class);
        return noticeDTO;
    }

    //공지 사항 목록
    @Override
    public List<NoticeDTO> noticeList(){
        List<Notice> notices = noticeRepository.findAll(); //모두 조회

        List<NoticeDTO> noticeDTOS = Arrays.asList(modelMapper.map(notices, NoticeDTO[].class));

        return noticeDTOS;
    }

    //페이징처리된 목록
    @Override
    public PageResponseDTO<NoticeDTO> pagelist(PageRequestDTO pageRequestDTO){

        log.info(pageRequestDTO);
        Page<Notice> noticePage = null;
        Pageable pageable = pageRequestDTO.getPageable("notice_num");
        log.info(pageRequestDTO);

        if (pageRequestDTO.getType() == null || pageRequestDTO.getKeyword()==null || pageRequestDTO.getKeyword().equals("")) {
            noticePage = noticeRepository.findAll(pageable);

        }else if (pageRequestDTO.getKeyword().equals("t")) {
            log.info("제목으로 검색 검색키워드는" + pageRequestDTO.getKeyword());
            List<Notice> noticePage1 = noticeRepository.searchByTitle(pageRequestDTO.getKeyword());

        }else if (pageRequestDTO.getKeyword().equals("h")) {
            log.info("호텔명으로 검색 검색키워드는" + pageRequestDTO.getKeyword());
            List<Notice> noticePage1 = noticeRepository.searchByHotel(pageRequestDTO.getKeyword());

        }else if (pageRequestDTO.getKeyword().equals("w")) {
            log.info("작성자로 검색 검색키워드는" + pageRequestDTO.getKeyword());
            List<Notice> noticePage1 = noticeRepository.searchByWriter(pageRequestDTO.getKeyword());

        }else if (pageRequestDTO.getType().equals("thw")){
            log.info("제목 또는 호텔명 또는 작성자 작성일로 검색 검색키워드는" + pageRequestDTO.getKeyword());
            List<Notice> noticePage1 = noticeRepository.searchByHotelOrWriter(pageRequestDTO.getKeyword());
        }

        List<Notice> noticeList = noticePage.getContent();

        List<NoticeDTO> noticeDTOList =
                noticeList.stream().map(notice -> modelMapper.map(notice, NoticeDTO.class))
                        .collect(Collectors.toList());

        PageResponseDTO<NoticeDTO> noticeDTOPageResponseDTO
                = PageResponseDTO.<NoticeDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(noticeDTOList)
                .total((int)noticePage.getTotalElements())
                .build();

        return noticeDTOPageResponseDTO;
    }

    //페이징처리 ok 검색 동적 처리
    @Override
    public PageResponseDTO<NoticeDTO> pageListsearchdsl(PageRequestDTO pageRequestDTO){
        log.info(pageRequestDTO);

        Pageable pageable = pageRequestDTO.getPageable("notice_num");
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String searchDateType = pageRequestDTO.getSearchDateType();

        Page<Notice> noticePage = noticeRepository.searchAll(types, keyword, searchDateType, pageable);

        //변환
        List<Notice> noticeList = noticePage.getContent();

        //dto 변환
        List<NoticeDTO> noticeDTOList=
                noticeList.stream().map(notice -> modelMapper.map(notice, NoticeDTO.class))
                .collect(Collectors.toList());

        PageResponseDTO<NoticeDTO> noticeDTOPageResponseDTO
                = PageResponseDTO.<NoticeDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(noticeDTOList)
                .total((int)noticePage.getTotalElements())
                .build();

        return noticeDTOPageResponseDTO;
    }

    //공지 사항 수정
    @Override
    public void noticeModify(NoticeDTO noticeDTO, List<MultipartFile> multipartFileList){

        Optional<Notice> notice = noticeRepository.findById(noticeDTO.getNotice_num());

        if (notice.isPresent()) {
            Notice notices = modelMapper.map(noticeDTO, Notice.class);

            noticeRepository.save(notices);
        }

        //파일 삭제
    }

    //공지 사항 삭제
    @Override
    public void noticeDelete(Long notice_num){
        noticeRepository.deleteById(notice_num);
    }


}
