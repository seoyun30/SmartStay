package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.NoticeDTO;

import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.dto.PageResponseDTO;
import com.lookatme.smartstay.entity.Image;
import com.lookatme.smartstay.entity.Notice;
import com.lookatme.smartstay.repository.ImageRepository;
import com.lookatme.smartstay.repository.NoticeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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
    private final ImageService imageService;
    private final ImageRepository imageRepository;


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
        List<Notice> noticeList = noticeRepository.findAll(); //모두 조회
        noticeList.forEach(notice -> log.info(notice.toString()));

        List<NoticeDTO> NoticeDTOList =
                noticeList.stream().map(notice -> modelMapper.map(notice, NoticeDTO.class))
                        .collect(Collectors.toList());
        NoticeDTOList.forEach(noticeDTO -> log.info(noticeDTO.toString()));

//        List<NoticeDTO> noticeDTOS = Arrays.asList(modelMapper.map(notices, NoticeDTO[].class));

        return NoticeDTOList;
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
            log.info("제목으로 검색 검색키워드는" ,pageRequestDTO.getKeyword());
            List<Notice> noticePage1 = noticeRepository.searchByTitle(pageRequestDTO.getKeyword());

        }else if (pageRequestDTO.getKeyword().equals("h")) {
            log.info("호텔명으로 검색 검색키워드는" , pageRequestDTO.getKeyword());
            List<Notice> noticePage1 = noticeRepository.searchByHotel(pageRequestDTO.getKeyword());

        }else if (pageRequestDTO.getKeyword().equals("w")) {
            log.info("작성자로 검색 검색키워드는" , pageRequestDTO.getKeyword());
            List<Notice> noticePage1 = noticeRepository.searchByWriter(pageRequestDTO.getKeyword());

        }else if (pageRequestDTO.getType().equals("thw")){
            log.info("제목 또는 호텔명 또는 작성자 작성일로 검색 검색키워드는" , pageRequestDTO.getKeyword());
            List<Notice> noticePage1 = noticeRepository.searchByHotelOrWriter(pageRequestDTO.getKeyword());
        }

        List<NoticeDTO> noticeDTOList =
                noticeList().stream().map(notice -> modelMapper.map(notice, NoticeDTO.class))
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
    public void noticeModify(NoticeDTO noticeDTO, List<MultipartFile> multipartFileList, List<Long> delnumList)   {

//        log.info("수정할 데이터 들어온 값 : " + noticeDTO);
        Optional<Notice> notice = noticeRepository.findById(noticeDTO.getNotice_num());

        if (notice.isPresent()) {
            Notice existingNotice = notice.get();

            // 수정된 사항이 있을 경우(기존 데이터와 변경된 데이터 비교 후) 저장
            Notice updateNotice = modelMapper.map(noticeDTO, Notice.class);
            if (!existingNotice.equals(updateNotice)) {
                noticeRepository.save(updateNotice);
            }
        }

        //이미지 삭제 처리
        if (delnumList != null && !delnumList.isEmpty()) {
            for (long imageId : delnumList) {
                imageService.deleteImage(imageId);
            }
        }

//        //이미지 추가/변경 처리
//        if (multipartFileList != null && !multipartFileList.isEmpty()) {
//
//            //기존 이미지 조회
//            List<Image> existingImages = imageRepository.findByTarget("notice", noticeDTO.getNotice_num());
//
//            for (MultipartFile file : multipartFileList) {
//
//                if (file.isEmpty()) {
//                    log.info("새 파일이 업로드되었습니다.", file.getOriginalFilename());
//
//                }

//                // 기존 이미지들 중 대표 이미지가 있는지 체크하고, 새 이미지가 대표 이미지로 설정
//                if (!existingImages.isEmpty()) {
//                    if (multipartFileList.indexOf(file) == 0) {
//                        // 기존 대표 이미지 삭제 후 새 대표 이미지 설정
//                        existingImages.stream()
//                                .filter(img -> "Y".equals(img.getRepimg_yn()))
//                                .forEach(img -> imageService.deleteImage(img.getImage_id()));  // 기존 대표 이미지 삭제
//                        imag.setRepimg_yn("Y");  // 새 대표 이미지로 설정
//                    } else {
//                        image.setRepimg_yn("N");
//                    }
//                } else {
//                    // 처음 이미지가 추가될 때
//                    image.setRepimg_yn(multipartFileList.indexOf(file) == 0 ? "Y" : "N");
//                }
//
//                // 공지사항에 연결된 이미지 설정
//                Notice notice = noticeRepository.findById(noticeDTO.getNotice_num())
//                        .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다."));
//                image.setNotice(notice);
//
//                // DB에 이미지 저장
//                imageRepository.save(image);
//
//            }
//        }
    }

    //공지 사항 삭제
    @Override
    public void noticeDelete(Long notice_num){
        noticeRepository.deleteById(notice_num);
    }


}
