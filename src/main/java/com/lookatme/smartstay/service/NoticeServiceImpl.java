package com.lookatme.smartstay.service;

import com.lookatme.smartstay.dto.ImageDTO;
import com.lookatme.smartstay.dto.NoticeDTO;

import com.lookatme.smartstay.entity.Hotel;
import com.lookatme.smartstay.entity.Image;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.Notice;
import com.lookatme.smartstay.repository.HotelRepository;
import com.lookatme.smartstay.repository.ImageRepository;
import com.lookatme.smartstay.repository.MemberRepository;
import com.lookatme.smartstay.repository.NoticeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.engine.internal.Collections;
import org.modelmapper.ModelMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class NoticeServiceImpl  {

    private final NoticeRepository noticeRepository;
    private final ModelMapper modelMapper;
    //권한 설정 시 필요
    private final MemberRepository memberRepository;
    private final HotelRepository hotelRepository;

    //이미지 구현 시 필요
    private final ImageRepository imageRepository;
    private final ImageService imageService;


    //공지 사항 등록
    public void noticeRegister(NoticeDTO noticeDTO, String email, List<MultipartFile> multipartFiles) throws Exception {

        // Controller registerget에서 로그인 확인을 하고 있음
        log.info(email);

        Member member = memberRepository.findByEmail(email);
        //이메일 유효성 검증
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("이메일이 비어 있습니다.");
        }

        //데이터베이스에서 회원 일치여부 확인
        log.info("찾아온 email: " + email);
        if (member == null) {
            throw new AccessDeniedException("해당 이메일로 회원정보를 찾을 수 없습니다.");
        }

        //권한 확인
        if (!member.getRole().name().equals("CHIEF") && !member.getRole().name().equals("MANAGER")) {
            throw new AccessDeniedException("공지사항을 작성할 권한이 없습니다.");
        }

        // 공지사항 생성 및 정보 세팅
        Notice notice = modelMapper.map(noticeDTO, Notice.class);  //DTO-> Entity 변환

        // 호텔은 직접 넣어줘야한다..
        notice.setMember(member);  // member 객체 설정
        notice.setCreate_by(member.getEmail());  //작성자 설정(로그인한 사용자의 email)
        notice.setReg_date(LocalDateTime.now()); //작성일을 현재 시간으로 설정


        // 이미지가 없다면 저장
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            imageService.saveImage(multipartFiles, "notice", notice.getNotice_num());
        }


        //저장
        noticeRepository.save(notice);
    }

//    //사진을 추가한 등록
//    public void register(NoticeDTO noticeDTO, List<MultipartFile> multipartFileList) throws Exception {
//
//        log.info("등록 서비스 들어온값: "+noticeDTO);
//        log.info("등록 서비스 들어온값: "+multipartFileList);
//        //글을 컨트롤러로부터 받아 entity변환헤서 저장
//        Notice notice = modelMapper.map(noticeDTO, Notice.class);
//        log.info("저장전에 noticeDTO를 notice로 변경한" + notice);
//
//        notice = noticeRepository.save(notice);
//        log.info("저장후에 결과를 가지고 있는notice" + notice);
//        //본문을 저장하고 나서
//        //사진 등록
//        if (multipartFileList != null) {
//            for (MultipartFile file : multipartFileList) {
//                if ( !multipartFileList.isEmpty()) {
//                    log.info("사진이 등록되었습니다.");
//
//                    // 이미지 등록
//                    imageService.saveImage(multipartFileList,"notice", notice.getNotice_num());
//                }
//
//            }
//        }
//
//    }

    //공지 사항 상세보기
    public NoticeDTO noticeRead(Long id){

        log.info("읽기로 들어온 값: "+id);

//        if (id == null || id <= 0) {
//            log.error("값이 유효하지 않습니다." + id);
//            throw new EntityNotFoundException("잘못된 게시글");
//        }
        //번호에 해당하는 내용을 조회
        Optional<Notice> noticeList = noticeRepository.findById(id);

        NoticeDTO noticeDTO = modelMapper.map(noticeList, NoticeDTO.class);



//        List<Image> imageList = imageRepository.findByTarget("notice", id);
//
//        if (imageList != null && imageList.isEmpty()) {
//            List<ImageDTO> imageDTOList = imageList.stream()
//                    .map(image -> modelMapper.map(image, ImageDTO.class))
//                    .collect(Collectors.toList());
//
//            List<Long> imageIdList = imageDTOList.stream()
//                    .map(ImageDTO::getImage_id).
//                    collect(Collectors.toList());
//
//            noticeDTO.setImageDTOList(imageDTOList);
//            noticeDTO.setImageIdList(imageIdList);
//
//        }

        System.out.println("나오니?" + noticeDTO);
        return noticeDTO;
    }



    //공지 사항 목록
    public List<NoticeDTO> noticeList(){

        //공지사항 전체 조회
        List<Notice> noticeList = noticeRepository.findAll();  //모든 공지사항 조회

        List<NoticeDTO> noticeDTOS = modelMapper.map(noticeList, List.class);



//        //화면페이지번호 1,2,3,4 .. 데이터베이스에서 페이지번호 0,1,2,3...
//        int currentPage = page.getPageNumber()-1; // 화면에 출력할 페이지번호를 데이터베이스 페이지번호
//        int blockLimit = 10; //한페이지를 구성하는 레코드의 수(페이지 번호의 수)
//
//        //지정된 내용으로 페이지정보를 재생산(정렬 생략시 기본키로 오름차순(ASC), 내림차순(DESC))
//        //해당페이지에서 10개의 레코드를 기본키로 내림차순해서 페이지 구성(최신순)
//        Pageable pageable = PageRequest.of(currentPage, blockLimit,
//               Sort.by(Sort.Direction.DESC, "title"));
//
//        //페이지 정보에 해당하는 모든 데이터를 읽어온다
//        Page<Notice> notices = noticeRepository.findAll(pageable);
//
//        //List<Notice> notices = noticeRepository.findAll();
//
//        System.out.println("khjkhjkj:"+notices);
//        Page<NoticeDTO> noticeDTOPage = notices.map(data->modelMapper.map(data, NoticeDTO.class));

        return noticeDTOS;
    }


    //공지 사항 수정
    public void noticeModify(NoticeDTO noticeDTO, String email, List<MultipartFile> multipartFileList, List<Long> delnumList)   {

        Member member = memberRepository.findByEmail(email);
        //이메일 유효성 검증
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("이메일이 비어 있습니다.");
        }

        //데이터베이스에서 회원 일치여부 확인
        log.info("찾아온 email: " + email);
        if (member == null) {
            throw new AccessDeniedException("해당 이메일로 회원정보를 찾을 수 없습니다.");
        }

        //권한 확인
        if (!member.getRole().name().equals("CHIEF") && !member.getRole().name().equals("MANAGER")) {
            throw new AccessDeniedException("공지사항을 작성할 권한이 없습니다.");
        }


        //DTO->Entity변환
        Notice notice = modelMapper.map(noticeDTO, Notice.class);

        //데이터베이스에서 기존 공지사항을 조회
        Optional<Notice> noticeRead = noticeRepository.findById(noticeDTO.getNotice_num());
        log.info("있니?");

        if (noticeRead.isPresent()) { //전달받은 레코드에 내용(수정사항)이 있으면
            Notice existingNotice = noticeRead.get();
            boolean hasChanges = false;

            // 제목 변경 여부 확인
            if (!existingNotice.getTitle().equals(noticeDTO.getTitle())) {
                existingNotice.setTitle(noticeDTO.getTitle());
                hasChanges = true;
            }

            // 내용 변경 여부 확인
            if (!existingNotice.getContent().equals(noticeDTO.getContent())) {
                existingNotice.setContent(noticeDTO.getContent());
                hasChanges = true;
            }

//            //이미지 변경 확인
//            boolean hasNewImages = multipartFileList != null && multipartFileList.stream().anyMatch(file -> !file.isEmpty());
//            boolean hasDeletedImages = delnumList != null && !delnumList.isEmpty();
//
//            if (hasNewImages || hasDeletedImages) {
//                log.info("이미지 업데이트 실행");
//                hasChanges = true;
//                try {
//                    // 이미지 업데이트
//                    imageService.updateImage(
//                            hasNewImages ? multipartFileList : null,
//                            hasDeletedImages ? delnumList : null,
//                            "notice",
//                            notice.getNotice_num()
//                    );
//                } catch (IndexOutOfBoundsException e) {
//                    log.error("이미지 업데이트 중 인덱스 오류 발생: {}", e.getMessage());
//                    throw new IllegalArgumentException("업로드된 파일이나 삭제 요청이 잘못되었습니다.");
//                } catch (Exception e) {
//                    throw new RuntimeException("이미지 업데이트 중 오류 발생", e);
//                }
//            } else {
//                log.info("이미지 업데이트 없이 텍스트 정보만 수정");
//            }
//
//            if (!multipartFileList.isEmpty()) { //수정할 이미지파일이 존재하면
//                // 기존에 존재하는 이미지파일이 있는지 확인 후 삭제
//            }

            // 변경 사항이 없으면 예외 발생
            if (!hasChanges) {
                throw new IllegalArgumentException("변경된 내용이 없습니다.");
            }

            //변경 사항이 있으면 저장
            noticeRepository.save(existingNotice);

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

