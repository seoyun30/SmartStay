package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.NoticeDTO;

import com.lookatme.smartstay.entity.Notice;
import com.lookatme.smartstay.repository.NoticeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class NoticeServiceImpl  {

    private final NoticeRepository noticeRepository;
    private final ModelMapper modelMapper;
    private final ImageService imageService;


    //공지 사항 등록
    public void noticeRegister(NoticeDTO noticeDTO){
        //DTO-> Entity 변환
        Notice notice = modelMapper.map(noticeDTO, Notice.class);

        //저장
        noticeRepository.save(notice);
    }

    //사진을 추가한 등록
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

                    // 이미지 등록
                    imageService.saveImage(multipartFileList,"notice", notice.getNotice_num());
                }

            }
        }

    }

    //공지 사항 상세보기
    public NoticeDTO noticeRead(Long id){
        log.info("읽기로 들어온 값: "+id);

        if (id == null || id <= 0) {
            log.error("값이 유효하지 않습니다." + id);
        }
        //번호에 해당하는 내용을 조회
        Optional<Notice> notice = noticeRepository.findById(id);

        NoticeDTO noticeDTO = modelMapper.map(notice, NoticeDTO.class);

        return noticeDTO;

    }



    //공지 사항 목록
    public Page<NoticeDTO> noticeList(Pageable page){

        //화면페이지번호 1,2,3,4 .. 데이터베이스에서 페이지번호 0,1,2,3...
        int currentPage = page.getPageNumber()-1; // 화면에 출력할 페이지번호를 데이터베이스 페이지번호
        int pageLimit = 10; //한페이지를 구성하는 레코드의 수(페이지 번호의 수)

        //지정된 내용으로 페이지정보를 재생산(정렬 생략시 기본키로 오름차순(ASC), 내림차순(DESC))
        //해당페이지에서 10개의 레코드를 기본키로 내림차순해서 페이지 구성(최신순)
        Pageable pageable = PageRequest.of(currentPage, pageLimit,
               Sort.by(Sort.Direction.DESC, "title"));

        //페이지 정보에 해당하는 모든 데이터를 읽어온다
        Page<Notice> notices = noticeRepository.findAll(pageable);

        //List<Notice> notices = noticeRepository.findAll();

        System.out.println("khjkhjkj:"+notices);
        Page<NoticeDTO> noticeDTOPage = notices.map(data->modelMapper.map(data, NoticeDTO.class));

        return noticeDTOPage;
        //return null;
    }


    //공지 사항 수정
    public void noticeModify(NoticeDTO noticeDTO, List<MultipartFile> multipartFileList, List<Long> delnumList)   {

        //DTO->Entity변환
        Notice notice = modelMapper.map(noticeDTO, Notice.class);

        //유효성 검사 id값으로 테이블에서 조회 페이지수를 읽어온다.(있으면 페이지, 없으면 null)
        Optional<Notice> noticeRead = noticeRepository.findById(noticeDTO.getNotice_num());



        if (noticeRead.isPresent()) { //전달받은 레코드에 내용(수정사항)이 있으면
            //저장
            noticeRepository.save(notice);
        }

        if (!multipartFileList.isEmpty()) { //수정할 이미지파일이 존재하면
            // 기존에 존재하는 이미지파일이 있는지 확인 후 삭제


        }

    }

    //공지 사항 삭제
    public void noticeDelete(Long notice_num){
        noticeRepository.deleteById(notice_num);

//        //삭제된 레코드를 조회
//        Optional<Notice> read = noticeRepository.findById(notice_num);
//
//        if (read.isPresent()) {
//
//
//        }
    }

}

