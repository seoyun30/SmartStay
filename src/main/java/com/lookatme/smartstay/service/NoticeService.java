package com.lookatme.smartstay.service;

import com.lookatme.smartstay.constant.Role;
import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.entity.Brand;
import com.lookatme.smartstay.entity.Hotel;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.Notice;
import com.lookatme.smartstay.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final ModelMapper modelMapper;
    //권한 설정 시 필요
    private final MemberRepository memberRepository;
    private final BrandRepository brandRepository;
    private final HotelRepository hotelRepository;

    //이미지 구현 시 필요
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final FileService fileService;

    @Value("${imgUploadLocation}")
    private String imgUploadLocation;

    //공지 사항 등록
    public void noticeRegister(NoticeDTO noticeDTO , Principal principal){

        log.info("등록서비스 들어온값 " + noticeDTO);

        Notice notice = modelMapper.map(noticeDTO, Notice.class);
        Member member =
                memberRepository.findByEmail(principal.getName());
        notice.setBrand(member.getBrand());
        notice.setMember(member);

        noticeRepository.save(notice);

    }
    public void noticeRegister(NoticeDTO noticeDTO, List<MultipartFile> multipartFiles , Principal principal) throws Exception {
        log.info("등록 서비스 들어온 값 : " + noticeDTO);
        log.info("등록 서비스 들어온 값 : " + multipartFiles);




        Notice notice = modelMapper.map(noticeDTO, Notice.class);
        log.info("noticeDTO를 notice로 변경" + notice);

        Member member =
                memberRepository.findByEmail(principal.getName());
        notice.setBrand(member.getBrand());
        if(member.getRole() == Role.MANAGER){
            notice.setHotel(member.getHotel());
        }

        notice.setMember(member);


        notice = noticeRepository.save(notice);
        log.info("저장후 결과를 가지고 있는 notice" + notice);

        if(multipartFiles != null) {

            for(MultipartFile multipartFile : multipartFiles){
                if(!multipartFile.isEmpty()) {
                    //물리적인 저장
                    String savedFileName =
                    fileService.uploadFile(imgUploadLocation, multipartFile);
                    //db저장
                    imageService.saveImageOne(savedFileName, multipartFile, notice  );


                }
            }


        }


    }

    //공지 사항 상세보기
    public NoticeDTO noticeRead(Long id){

        return null;
    }

    //공지 사항 목록
    public PageResponseDTO<NoticeDTO> noticeList(PageRequestDTO pageRequestDTO, String email) {

        Member member = memberRepository.findByEmail(email);

        if (member == null || member.getBrand() == null) {
            log.info("사용자의 브랜드를 찾을수 없음" + email);
            return null;
        }

        Long brandNum = member.getBrand().getBrand_num();
        log.info("조회된 브랜드넘" + brandNum);

        Pageable pageable = pageRequestDTO.getPageable("notice_num");

        log.info(pageable);
        log.info("서비스진입");

        Page<Notice> noticePage;
        if (pageRequestDTO.getKeyword() != null && !pageRequestDTO.getKeyword().isEmpty()) {
            noticePage = noticeRepository.searchNotice(brandNum, pageRequestDTO.getKeyword(), pageable);
        } else {
            log.info("해당브랜드의 리스트 적용");
            noticePage = noticeRepository.noticeBrandList(brandNum, pageable);
        }
        List<Notice> noticeList = noticePage.getContent();

        noticeList.forEach(notice -> log.info(notice));

        if (noticeList == null  || noticeList.isEmpty()) {
            log.info("공지사항 없음");
            return null;
        } else {
            List<NoticeDTO> noticeDTOList = noticeList.stream()
                    .map(notice ->{
                                NoticeDTO noticeDTO = modelMapper.map(notice, NoticeDTO.class);

                                noticeDTO.setBrandDTO( modelMapper.map(notice.getBrand(), BrandDTO.class));

                                noticeDTO.setHotelDTO(modelMapper.map(notice.getHotel(), HotelDTO.class));

                                return  noticeDTO;
                            } )
                    .collect(Collectors.toList());

            log.info("DTO변환");

            noticeDTOList.forEach(dto -> log.info(dto));

            return PageResponseDTO.<NoticeDTO>withAll()
                    .pageRequestDTO(pageRequestDTO)
                    .dtoList(noticeDTOList)
                    .total((int) noticePage.getTotalElements())
                    .build();
        }
    }



    //공지 사항 수정
    public void noticeModify(NoticeDTO noticeDTO, String email, List<MultipartFile> multipartFileList, List<Long> delnumList)   {

        Member member = memberRepository.findByEmail(email);
        //이메일 유효성 검증
        if (email == null) {
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



