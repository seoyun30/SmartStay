package com.lookatme.smartstay.service;


import com.lookatme.smartstay.dto.HotelDTO;
import com.lookatme.smartstay.dto.ImageDTO;
import com.lookatme.smartstay.dto.QnaDTO;
import com.lookatme.smartstay.entity.Hotel;
import com.lookatme.smartstay.entity.Image;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.Qna;
import com.lookatme.smartstay.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.juli.logging.Log;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class QnaServiceImpl implements QnaService {

    private final QnaRepository qnaRepository;
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final ImageService imageService;
    private final QnaReplyRepository qnaReplyRepository;
    private final MemberRepository memberRepository;
    private final HotelRepository  hotelRepository;


    // ✅ 로그인된 사용자 ID 가져오는 메서드 추가
    private String getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "loging"; // 인증되지 않은 경우 기본값 반환
        }
        return auth.getName(); // 로그인된 사용자의 이메일 (또는 사용자명) 반환
    }

    @Override
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

    @Override
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

        QnaDTO qnaDTO = modelMapper.map(qna, QnaDTO.class);

        // 이미지 리스트 추가
        List<Image> imageList = imageService.findImagesByTarget("qna", qna_num);
        List<ImageDTO> imageDTOList = imageList.stream()
                .map(image -> {
                    ImageDTO imageDTO = modelMapper.map(image, ImageDTO.class);
                    imageDTO.setThumbnail_url(image.getThumbnail_url()); // 썸네일 URL 설정
                    return imageDTO;
                })
                .collect(Collectors.toList());

        qnaDTO.setImageDTOList(imageDTOList);

        log.info("서비스에서 컨트롤러로 나간값 : " + qnaDTO);

        return qnaDTO;
    }

    @Override
    public List<QnaDTO> list() {
        List<Qna> qnaList = qnaRepository.findAll();

        // 조회한 QnA 리스트를 로깅
        qnaList.forEach(qna -> log.info(qna.toString()));

        List<QnaDTO> qnaDTOList = qnaList.stream()
                .map(qna -> {
                    // Qna 엔티티를 QnaDTO로 변환
                    QnaDTO qnaDTO = modelMapper.map(qna, QnaDTO.class);

                    // 명시적으로 매핑 규칙을 추가
                    modelMapper.typeMap(Qna.class, QnaDTO.class).addMappings(mapper -> {
                        // Hotel 객체의 hotel_num을 QnaDTO의 hotel_num에 매핑
                        mapper.map(src -> src.getHotel().getHotel_num(), QnaDTO::setHotel_num);
                    });

                    // Qna의 Hotel 객체를 HotelDTO로 변환하여 setHotelDTO에 설정
                    qnaDTO.setHotelDTO(modelMapper.map(qna.getHotel(), HotelDTO.class));

                    return qnaDTO;
                })
                .collect(Collectors.toList()); // 리스트로 변환

       /* // QnA 엔티티 리스트를 QnA DTO로 변환
        List<QnaDTO> qnaDTOList = qnaList.stream()
                .map(qna -> modelMapper.map(qna, QnaDTO.class)
                        .setHotelDTO(modelMapper.map(qna.getHotel(), HotelDTO.class))
                ).collect(Collectors.toList()); // 리스트로 변환*/

        // 변환된 DTO 리스트 로깅
        qnaDTOList.forEach(qnaDTO -> log.info(qnaDTO.toString()));

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


    @Override
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
/*---------------------------------------------------
        // 파일 삭제
        if (delino != null && !delino[0].equals("")) {
            log.info("업데이트포스" + Arrays.toString(delino));
            for (Long ino : delino) {
                boardImgService.del(ino);
            }
        }

        // 파일 등록

        if (multipartFiles != null && multipartFiles.length > 0) {

            for (MultipartFile multipartFile : multipartFiles) {
                if (multipartFile != null) {
                    log.info("여기 사진이 있어요.");
                    log.info("업데이트 서비스 새로 등록하는 파일 : " + multipartFile.getOriginalFilename());

                    boardImgService.boardImgregister(boardDTO.getBno(), multipartFile);
                }
            }
        }
------------------------------------------------------*/
//        if (multipartFile != null && !multipartFile.getOriginalFilename().equals("")) {
//                log.info("여기 사진이 있어요");
//                log.info("업데이트포스" + multipartFile.getOriginalFilename());
//
//                boardImgService.boardImgregister(boardDTO.getBno(), multipartFile);
//            }
//
//            if (delino != null && !delino[0].equals("")){
//                log.info("업데이트포스" + Arrays.toString(delino));
//
//                //사진삭제
//                for (Long ino : delino) {
//                    boardImgService.del(ino);
//                }
//            }





    @Override
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
    @Override
    public void incrementViewCount(Long qna_num) {
        log.info("조회수 증가 서비스 호출 :"+qna_num);

        Qna qna = qnaRepository.findById(qna_num)
                .orElseThrow(()-> new EntityNotFoundException("문의 게시글을 찾을 수 없습니다."));
        qna.incrementViewCount(); // 증가
        qnaRepository.save(qna); //업데이트된 qna 엔티티 저장

        log.info("조회수 증가 후 QnA:" + qna);
    }

/*--------------------------추후 페이징 처리 사용
    @Override
    public PageResponseDTO<BoardDTO> pagelist(PageRequestDTO pageRequestDTO) {

//        Pageable pageable = PageRequest
//                .of(page-1, size, Sort.by("bno").descending());


        log.info(pageRequestDTO);
        Page<Board> boardPage = null;
        Pageable pageable = pageRequestDTO.getPageable("bno");  //위의 주석 내용과 같은 개념(PageRequestDTO에 사용)
        log.info(pageRequestDTO);

        if(pageRequestDTO.getType() == null || pageRequestDTO.getKeyword()==null || pageRequestDTO.getKeyword().equals("")){
            boardPage =  boardRepository.findAll(pageable);

        }else if(pageRequestDTO.getType().equals("t")){
            log.info( "제목으로 검색 검색키워드는"  +pageRequestDTO.getKeyword() );
            boardPage =  boardRepository.findByTitleContaining(pageRequestDTO.getKeyword(), pageable);

        }else if(pageRequestDTO.getType().equals("c")){
            log.info( "내용으로 검색 검색키워드는"  +pageRequestDTO.getKeyword() );
            boardPage =  boardRepository.findByContentContaining(pageRequestDTO.getKeyword(), pageable);

        }else if(pageRequestDTO.getType().equals("w")){
            log.info( "작성자로 검색으로  검색키워드는"  +pageRequestDTO.getKeyword() );
            boardPage =  boardRepository.selectlikeWriter(pageRequestDTO.getKeyword(), pageable);

        }else if(pageRequestDTO.getType().equals("tc")){
            log.info( "제목 + 내용중에 검색  검색키워드는"  +pageRequestDTO.getKeyword() );
            boardPage =  boardRepository.titleOrCon(pageRequestDTO.getKeyword(), pageable);

        }else if(pageRequestDTO.getType().equals("cw")){
            log.info( "내용 + 작성자으로  검색키워드는"  +pageRequestDTO.getKeyword() );
            boardPage =  boardRepository.findByContentContainingOrWriterContaining(pageRequestDTO.getKeyword(),pageRequestDTO.getKeyword(), pageable);

        }else if(pageRequestDTO.getType().equals("tw")){
            log.info( "제목 + 작성자 검색 검색키워드는"  +pageRequestDTO.getKeyword() );
            boardPage =  boardRepository.findByTitleContainingOrWriterContaining(pageRequestDTO.getKeyword(),pageRequestDTO.getKeyword(), pageable);

        }else if(pageRequestDTO.getType().equals("tcw")){
            log.info( "제목 + 내용 + 작성자으로 검색 검색키워드는"  +pageRequestDTO.getKeyword() );
            boardPage =  boardRepository.titleOrConOrWr(pageRequestDTO.getKeyword(), pageable);

        }

        //변환
        List<Board> boardList = boardPage.getContent();
        //dto변환
        List<BoardDTO> boardDTOList=
                boardList.stream().map(board -> modelMapper.map(board, BoardDTO.class))
                        .collect(Collectors.toList());

        PageResponseDTO<BoardDTO> boardDTOPageResponseDTO
                = PageResponseDTO.<BoardDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(boardDTOList)
                .total((int)boardPage.getTotalElements())
                .build();


        //문제발생
        //페이징 처리는 내부적으로 되었으나
        //사용자에게 보여주려면 1~10까지 버튼과 혹은
        //21~30까지의 버튼을 알아야한다. 그 값 또한 같이 넘겨야해서
        //RequestPageDTO를 이용해서 사용


        return boardDTOPageResponseDTO;
    }

        @Override
    public PageResponseDTO<BoardDTO> pageListsearchdsl(PageRequestDTO pageRequestDTO) {

        log.info(pageRequestDTO);

        Pageable pageable = pageRequestDTO.getPageable("bno");  //위의 주석 내용과 같은 개념(PageRequestDTO에 사용)
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();


        Page<Board> boardPage = boardRepository.searchAll(types, keyword, pageable);

        log.info(pageRequestDTO);

        //변환
        List<Board> boardList = boardPage.getContent();
        //dto변환
        List<BoardDTO> boardDTOList=
                boardList.stream().map(board -> modelMapper.map(board, BoardDTO.class))
                        .collect(Collectors.toList());


        PageResponseDTO<BoardDTO> boardDTOPageResponseDTO
                = PageResponseDTO.<BoardDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(boardDTOList)
                .total((int)boardPage.getTotalElements())
                .build();

        return boardDTOPageResponseDTO;
    }
---------------------------------------------------------------*/


}
