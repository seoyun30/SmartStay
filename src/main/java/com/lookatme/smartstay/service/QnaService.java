package com.lookatme.smartstay.service;


import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.entity.Hotel;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.Qna;
import com.lookatme.smartstay.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class QnaService {

    private final QnaRepository qnaRepository;
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;
    private final ImageService imageService;
    private final QnaReplyRepository qnaReplyRepository;
    private final MemberRepository memberRepository;
    private final HotelRepository hotelRepository;


    // ✅ 로그인된 사용자 ID 가져오는 메서드 추가
    private String getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/member/login"; // 인증되지 않은 경우 기본값 반환
        }
        return auth.getName(); // 로그인된 사용자의 이메일 (또는 사용자명) 반환
    }

    
    public void register(QnaDTO qnaDTO, MultipartFile[] multipartFiles) {
        log.info("등록 서비스 들어온 값: " + qnaDTO);
        log.info("등록된 서비스 들어온 파일 목록: " + Arrays.toString(multipartFiles));

        // 로그인된 사용자 ID 가져오기
        String loggedInUser = getLoggedInUser();
        log.info("로그인된 사용자 ID: " + loggedInUser);

        // Member 엔티티 조회
        Member member = memberRepository.findMemberByEmail(loggedInUser)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // Hotel 엔티티 조회 (호텔 번호로 호텔 찾기)
        Hotel hotel = hotelRepository.findById(qnaDTO.getHotel_num())
                .orElseThrow(() -> new EntityNotFoundException("호텔을 찾을 수 없습니다."));

        // Qna 객체 생성 및 설정
        Qna qna = Qna.builder()
                .title(qnaDTO.getTitle())
                .content(qnaDTO.getContent())
                .writer(loggedInUser)// 작성자 설정
                .hotel(hotel)
                .build();

        // Qna 저장
        qna = qnaRepository.save(qna);
        log.info("저장 후 Qna 정보: " + qna);

        // 이미지가 있다면 저장
        if (multipartFiles != null && multipartFiles.length > 0) {
            List<MultipartFile> imageFileList = Arrays.asList(multipartFiles);
            try {
                imageService.saveImage(imageFileList, "qna", qna.getQna_num());
            } catch (Exception e) {
                log.error("이미지 저장 중 오류 발생: ", e);
                throw new RuntimeException("이미지 저장 실패", e);
            }
        }
    }

    
    public QnaDTO read(Long qna_num) {
        log.info("서비스 읽기로 들어온값 : " + qna_num);

        // qna_num이 null이거나 유효하지 않으면 예외 처리
        if (qna_num == null || qna_num <= 0) {
            log.error("유효하지 않은 qna_num 값: " + qna_num);
            throw new EntityNotFoundException("잘못된 게시글 번호입니다.");
        }

        Qna qna = qnaRepository.findById(qna_num)
                .orElseThrow(() -> {
                    log.error("게시글을 찾을 수 없음: " + qna_num);
                    return new EntityNotFoundException("게시글을 찾을 수 없습니다.");
                });

        QnaDTO qnaDTO = modelMapper.map(qna, QnaDTO.class)
                .setHotelDTO(modelMapper.map(qna.getHotel(), HotelDTO.class));

        // 이미지 리스트 추가
        Optional.of(imageService.findImagesByTarget("qna", qna_num))
                .map(imageList -> imageList.stream()
                        .map(image -> {
                            ImageDTO imageDTO = modelMapper.map(image, ImageDTO.class);
                            imageDTO.setThumbnail_url(image.getThumbnail_url()); // 썸네일 URL 설정
                            return imageDTO;
                        })
                        .collect(Collectors.toList()))
                .ifPresent(qnaDTO::setImageDTOList);

        log.info("서비스에서 컨트롤러로 나간값 : " + qnaDTO);

        return qnaDTO;
    }


    
    public List<QnaDTO> list(PageRequestDTO pageRequestDTO) {
        List<Qna> qnaList = qnaRepository.findAll();

        // 조회한 QnA 리스트를 로깅
        qnaList.forEach(qna -> log.info(qna.toString()));

        List<QnaDTO> qnaDTOList = qnaList.stream().map( qna ->
                modelMapper.map(qna, QnaDTO.class)
                        .setHotelDTO(modelMapper.map(qna.getHotel(), HotelDTO.class))
        ).collect(Collectors.toList());
        // 변환된 DTO 리스트 로깅
        qnaDTOList.forEach(qnaDTO -> log.info(qnaDTO.toString()));
        return qnaDTOList;  // DTO 리스트 반환
    }

    public List<QnaDTO> myQnaList(String email) {

        List<QnaDTO> qnaDTOList;
        if (email != null) {
            List<Qna> qnaList = qnaRepository.findByWriter(email);  // 작성자(email)로 QnA 목록 조회
            qnaDTOList = qnaList.stream().map( qna ->
                            modelMapper.map(qna, QnaDTO.class)
                                    .setHotelDTO(modelMapper.map(qna.getHotel(), HotelDTO.class))
                    )
                    .collect(Collectors.toList());
        } else {
            qnaDTOList = new ArrayList<>();
        }

        return qnaDTOList;  // DTO 리스트 반환
    }

    private boolean checkIfManager() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false; // 인증되지 않은 경우 관리자 아님
        }

        return auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_MANAGER"));
    }



    
    public void modify(QnaDTO qnaDTO) {
        //이미지 추가하게 되면 사용 메소드 MultipartFile[] multipartFiles, Long[] delino
        log.info("수정 서비스 들어온값:" + qnaDTO);

        Optional<Qna> optionalQna =
                qnaRepository.findById(qnaDTO.getQna_num());

        Qna qna =
                optionalQna.orElseThrow(EntityNotFoundException::new);

        log.info(qna);

        qna.setTitle(qnaDTO.getTitle());
        qna.setContent(qnaDTO.getContent());
        qna.setWriter(qnaDTO.getWriter());

    }

    
    public void del(Long qna_num) {
        log.info("삭제로 들어온 값:" + qna_num);
        // 로그인한 사용자 확인
        String loggedInUser = getLoggedInUser();

        // 게시글 찾기
        Qna qna = qnaRepository.findById(qna_num)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        // 작성자가 일치하지 않으면 예외 발생
        if (!qna.getWriter().equals(loggedInUser)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }
        //qnaReplyRepository.deleteAllByQna_num(qna_num);
        //imageService.delAll(qna_num);
        qnaRepository.deleteById(qna_num);

    }

    //조회수 증가 메서드
    
    public void incrementViewCount(Long qna_num) {
        log.info("조회수 증가 서비스 호출 :"+qna_num);

        Qna qna = qnaRepository.findById(qna_num)
                .orElseThrow(()-> new EntityNotFoundException("문의 게시글을 찾을 수 없습니다."));
        qna.incrementViewCount(); // 증가
        qnaRepository.save(qna); //업데이트된 qna 엔티티 저장

        log.info("조회수 증가 후 QnA:" + qna);
    }

    
    public PageResponseDTO<QnaDTO> pagelist(PageRequestDTO pageRequestDTO) {

        log.info("진입 : "+ pageRequestDTO);

        Page<Qna> qnaPage = null;
        int currentPage = pageRequestDTO.getPage()-1;
        int guestLimits = pageRequestDTO.getSize();
        Pageable qnaPageable = PageRequest.of(currentPage, guestLimits,
                Sort.by(Sort.Direction.DESC,"qna_num"));  //위의 주석 내용과 같은 개념(PageRequestDTO에 사용)

        log.info("페이저블 : " + pageRequestDTO);

        if(pageRequestDTO.getType() == null || pageRequestDTO.getKeyword()==null || pageRequestDTO.getKeyword().equals("")){
            qnaPage =  qnaRepository.selcetAll(qnaPageable);
        } else if(pageRequestDTO.getType().equals("t")){
            log.info( "제목으로 검색 검색키워드는"  +pageRequestDTO.getKeyword() );
            qnaPage =  qnaRepository.findByTitleContaining(pageRequestDTO.getKeyword(), qnaPageable);

        }else if(pageRequestDTO.getType().equals("c")){
            log.info( "내용으로 검색 검색키워드는"  +pageRequestDTO.getKeyword() );
            qnaPage =  qnaRepository.findByContentContaining(pageRequestDTO.getKeyword(), qnaPageable);

        }else if(pageRequestDTO.getType().equals("w")){
            log.info( "작성자로 검색으로  검색키워드는"  +pageRequestDTO.getKeyword() );
            qnaPage =  qnaRepository.selectlikeWriter(pageRequestDTO.getKeyword(), qnaPageable);

        }else if(pageRequestDTO.getType().equals("h")){
            log.info( "작성자로 검색으로  검색키워드는"  +pageRequestDTO.getKeyword() );
            qnaPage =  qnaRepository.selectlikeHotel(pageRequestDTO.getKeyword(), qnaPageable);

        }else if(pageRequestDTO.getType().equals("tc")){
            log.info( "제목 + 내용중에 검색  검색키워드는"  +pageRequestDTO.getKeyword() );
            qnaPage =  qnaRepository.titleOrCon(pageRequestDTO.getKeyword(), qnaPageable);

        }else if(pageRequestDTO.getType().equals("cw")){
            log.info( "내용 + 작성자으로  검색키워드는"  +pageRequestDTO.getKeyword() );
            qnaPage =  qnaRepository.findByContentContainingOrWriterContaining(pageRequestDTO.getKeyword(),pageRequestDTO.getKeyword(), qnaPageable);

        }else if(pageRequestDTO.getType().equals("tw")){
            log.info( "제목 + 작성자 검색 검색키워드는"  +pageRequestDTO.getKeyword() );
            qnaPage =  qnaRepository.findByTitleContainingOrWriterContaining(pageRequestDTO.getKeyword(),pageRequestDTO.getKeyword(), qnaPageable);

        }else if(pageRequestDTO.getType().equals("tcw")){
            log.info( "제목 + 내용 + 작성자으로 검색 검색키워드는"  +pageRequestDTO.getKeyword() );
            qnaPage =  qnaRepository.titleOrConOrWr(pageRequestDTO.getKeyword(), qnaPageable);

        }

        //변환
        List<Qna> qnaList = qnaPage.getContent();
        qnaList.forEach(qna -> log.info(qna));
        //dto변환
        List<QnaDTO> qnaDTOList=
                qnaList.stream().map(qna -> modelMapper.map(qna, QnaDTO.class)
                                .setHotelDTO(modelMapper.map(qna.getHotel(), HotelDTO.class)))
                        .collect(Collectors.toList());

        PageResponseDTO<QnaDTO> qnaDTOPageResponseDTO
                = PageResponseDTO.<QnaDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(qnaDTOList)
                .total((int)qnaPage.getTotalElements())
                .build();

        return qnaDTOPageResponseDTO;
    }

    
    public PageResponseDTO<QnaDTO> pagemylist(PageRequestDTO pageRequestDTO,String email) {

        log.info("진입 : "+ pageRequestDTO);

        Page<Qna> qnaPage = null;
        int currentPage = pageRequestDTO.getPage()-1;
        int guestLimits = pageRequestDTO.getSize();
        Pageable qnaPageable = PageRequest.of(currentPage, guestLimits,
                Sort.by(Sort.Direction.DESC,"qna_num"));  //위의 주석 내용과 같은 개념(PageRequestDTO에 사용)

        log.info("페이저블 : " + pageRequestDTO);

        if(pageRequestDTO.getType() == null || pageRequestDTO.getKeyword()==null || pageRequestDTO.getKeyword().equals("")){
            qnaPage =  qnaRepository.findByWriter(email, qnaPageable);
        } else if(pageRequestDTO.getType().equals("t")){
            log.info( "제목으로 검색 검색키워드는"  +pageRequestDTO.getKeyword() );
            qnaPage =  qnaRepository.findByTitleAndWriterContaining(pageRequestDTO.getKeyword(), email, qnaPageable);

        }else if(pageRequestDTO.getType().equals("c")){
            log.info( "내용으로 검색 검색키워드는"  +pageRequestDTO.getKeyword() );
            qnaPage =  qnaRepository.findByContentAndWriterContaining(pageRequestDTO.getKeyword(), email, qnaPageable);

        }else if(pageRequestDTO.getType().equals("h")){
            log.info( "작성자로 검색으로  검색키워드는"  +pageRequestDTO.getKeyword() );
            qnaPage =  qnaRepository.selectlikeHotelAndWriter(pageRequestDTO.getKeyword(), email, qnaPageable);

        }else if(pageRequestDTO.getType().equals("tc")){
            log.info("제목 + 내용중에 검색  검색키워드는 " + pageRequestDTO.getKeyword());
            qnaPage = qnaRepository.titleOrConAndWriter(pageRequestDTO.getKeyword(), email, qnaPageable);
        }

        //변환
        List<Qna> qnaList = qnaPage.getContent();
        qnaList.forEach(qna -> log.info(qna));
        //dto변환
        List<QnaDTO> qnaDTOList=
                qnaList.stream().map(qna -> modelMapper.map(qna, QnaDTO.class)
                                .setHotelDTO(modelMapper.map(qna.getHotel(), HotelDTO.class)))
                        .collect(Collectors.toList());

        PageResponseDTO<QnaDTO> qnaDTOPageResponseDTO
                = PageResponseDTO.<QnaDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(qnaDTOList)
                .total((int)qnaPage.getTotalElements())
                .build();

        return qnaDTOPageResponseDTO;
    }

    
    public PageResponseDTO<QnaDTO> pageListsearchdsl(PageRequestDTO pageRequestDTO) {

        log.info(pageRequestDTO);

        Pageable pageable = pageRequestDTO.getPageable("qna_num");  //위의 주석 내용과 같은 개념(PageRequestDTO에 사용)
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();

        Page<Qna> qnaPage = qnaRepository.selcetAll(pageable);

        log.info(pageRequestDTO);

        //변환
        List<Qna> qnaList = qnaPage.getContent();
        //dto변환
        List<QnaDTO> qnaDTOList=
                qnaList.stream().map(qna -> modelMapper.map(qna, QnaDTO.class))
                        .collect(Collectors.toList());


        PageResponseDTO<QnaDTO> qnaDTOPageResponseDTO
                = PageResponseDTO.<QnaDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(qnaDTOList)
                .total((int)qnaPage.getTotalElements())
                .build();

        return qnaDTOPageResponseDTO;
    }



}
