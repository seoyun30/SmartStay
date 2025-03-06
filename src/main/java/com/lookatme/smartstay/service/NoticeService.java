package com.lookatme.smartstay.service;

import com.lookatme.smartstay.constant.Role;
import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.entity.Image;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.Notice;
import com.lookatme.smartstay.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
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

        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            imageService.saveImage(multipartFiles, "notice", notice.getNotice_num());
        }

    }

    //공지 사항 상세보기
    public NoticeDTO noticeRead(Long notice_num){

        Notice notice = noticeRepository.findById(notice_num).orElseThrow(EntityNotFoundException::new);

        NoticeDTO noticeDTO = modelMapper.map(notice, NoticeDTO.class);

        noticeDTO.setBrandDTO( modelMapper.map(notice.getBrand(), BrandDTO.class));

        noticeDTO.setHotelDTO(modelMapper.map(notice.getHotel(), HotelDTO.class));

        List<Image> imageList = imageRepository.findByTarget("notice", notice_num);

        if (imageList != null && !imageList.isEmpty()) {
            List<ImageDTO> imageDTOList = imageList.stream()
                    .map(image -> modelMapper.map(image, ImageDTO.class))
                    .collect(Collectors.toList());
            List<Long> imageIdList = imageDTOList.stream()
                    .map(ImageDTO::getImage_id)
                    .collect(Collectors.toList());

            noticeDTO.setImageDTOList(imageDTOList);
            noticeDTO.setImageIdList(imageIdList);
        }



        log.info("서비스에서 컨트롤러로 나간 값  : " + noticeDTO);

        return noticeDTO;
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
        List<NoticeDTO> noticeDTOList;

        noticeList.forEach(notice -> log.info(notice));

        if (!noticeList.isEmpty()) {
            noticeDTOList = noticeList.stream()
                    .map(notice ->{
                        NoticeDTO noticeDTO = modelMapper.map(notice, NoticeDTO.class);

                        noticeDTO.setBrandDTO( modelMapper.map(notice.getBrand(), BrandDTO.class));

                        noticeDTO.setHotelDTO(modelMapper.map(notice.getHotel(), HotelDTO.class));

                        return  noticeDTO;
                    } )
                    .collect(Collectors.toList());

            log.info("DTO변환");

        } else {
            log.info("공지사항 없음");
            noticeDTOList = Collections.emptyList();
        }


        noticeDTOList.forEach(dto -> log.info(dto));

            return PageResponseDTO.<NoticeDTO>withAll()
                    .pageRequestDTO(pageRequestDTO)
                    .dtoList(noticeDTOList)
                    .total((int) noticePage.getTotalElements())
                    .build();

    }

    public PageResponseDTO<NoticeDTO> userNoticeList(PageRequestDTO pageRequestDTO) {

        log.info(pageRequestDTO);


        Pageable pageable = pageRequestDTO.getPageable("notice_num");

        Page<Notice> noticePage;
        if (pageRequestDTO.getKeyword() != null && !pageRequestDTO.getKeyword().isEmpty()) {
            noticePage = noticeRepository.userSearchNotice(pageRequestDTO.getKeyword(), pageable);
        } else {
            noticePage = noticeRepository.AllNotice(pageable);
        }



        List<Notice> noticeList = noticePage.getContent();

        log.info("엔티티 리스트");
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
    public void noticeModify(NoticeDTO noticeDTO, String email, List<MultipartFile> multipartFileList, List<Long> delnumList) throws Exception {

       Notice notice = noticeRepository.findById(noticeDTO.getNotice_num()).orElseThrow(EntityNotFoundException::new);

        Member member =
                memberRepository.findByEmail(email);


        if (  (notice.getBrand().getBrand_num() != member.getBrand().getBrand_num() )  ||

                notice.getHotel() != null  && member.getRole().name().equals("MANAGER") &&
                        notice.getHotel().getHotel_num() != member.getHotel().getHotel_num()
        ) {
            throw new SecurityException("수정할 수 있는 권한이 없습니다.");
        }

        notice.setTitle(noticeDTO.getTitle());
        notice.setContent(noticeDTO.getContent());
        notice.setModi_date(noticeDTO.getModi_date());

        noticeRepository.save(notice);

        //파일등록 리스트 > 반복해서 하나씩 저장
        if (multipartFileList != null && !multipartFileList.isEmpty()) {
            imageService.saveImage(multipartFileList, "notice", notice.getNotice_num());
        }



        //파일삭제
        if(delnumList != null) {
            for (Long delnum : delnumList){

                if(delnum != null ){
                    log.info("삭제 " + delnum);
                    imageService.deleteImage(delnum);
                }

            }
        }


    }

    //공지 사항 삭제
    public void noticeDelete(Long notice_num, String email) {

        Notice notice = noticeRepository.findById(notice_num).orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 존재하지 않습니다."));

        Member member = memberRepository.findByEmail(email);

        if (member.getRole() != Role.MANAGER && member.getRole() != Role.CHIEF) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        if (!notice.getBrand().getBrand_num().equals(member.getBrand().getBrand_num()) &&
                !notice.getHotel().getHotel_num().equals(member.getHotel().getHotel_num())) {
            throw new SecurityException("삭제할 수 있는 권한이 없습니다.");
        }

        List<Image> imageList =
        imageRepository.findByTarget("notice", notice.getNotice_num());
        if(imageList != null && !imageList.isEmpty()) {
            for (Image image : imageList){

                if(image != null ){
                    log.info("삭제 " + image.getImage_id());
                    imageService.deleteImage(image.getImage_id());
                }

            }
        }

        noticeRepository.delete(notice);

    }
}



