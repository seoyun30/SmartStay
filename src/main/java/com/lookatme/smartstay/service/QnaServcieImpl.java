package com.lookatme.smartstay.service;


import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.dto.QnaDTO;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.Qna;
import com.lookatme.smartstay.repository.MemberRepository;
import com.lookatme.smartstay.repository.QnaReplyRepository;
import com.lookatme.smartstay.repository.QnaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class QnaServcieImpl implements QnaService {
    private final QnaRepository qnaRepository;

    //reply,이미지 추후사용
    //private final QnaReplyRepository qnaReplyRepository;
    //private final ImageRepository imageRepository;

    private final ModelMapper modelMapper = new ModelMapper();
    private final ImageService imageService;
    private final QnaReplyRepository qnaReplyRepository;
    private final MemberRepository memberRepository;

    @Override
    public void register(QnaDTO qnaDTO) {
        log.info("등록 서비스 들어온값:"+qnaDTO);
        // Qna 객체에 title, content 및 member 설정
        Qna qna = Qna.builder()
                .title(qnaDTO.getTitle())
                .content(qnaDTO.getContent())
                .build();
        qnaRepository.save(qna);
    }

    /*이미지 추후
    @Override
    public void register(QnaDTO qnaDTO, MultipartFile[] multipartFiles){
        log.info("등록된 서비스 들어온 값 :"+qnaDTO);
        log.info("등로된 서비스 들어온 값 :"+multipartFiles);

        Qna qna = modelMapper.map(qnaDTO, Qna.class);
        log.info("저장 전에 qnaDTO를 qna로 변환함 :"+qna);

        Qna=qnaRepository.save(qna);
        log.info("저장 후에 결과를 가지고 있는 qna :"+qna);

        if (multipartFiles != null){
            for (MultipartFile file: multipartFiles) {
                if (!file.isEmpty()){
                    log.info("사진이 저장됩니다.");
                    imageService.imageregister(qna.getQna_num(), file);
                }
            }
        }
    } */

    @Override
    public QnaDTO read(Long qna_num) {
        log.info("서비스 읽기로 들어온값 : " +  qna_num);

        // qna_num이 null이거나 유효하지 않으면 예외 처리
        if (qna_num == null || qna_num <= 0) {
            log.error("유효하지 않은 qna_num 값: " + qna_num);
            throw new EntityNotFoundException("잘못된 게시글 번호입니다.");
        }

        Optional<Qna> optionalQna =
               qnaRepository.findById(qna_num);

        Qna qna = optionalQna.orElseThrow(EntityNotFoundException::new);

        QnaDTO qnaDTO = modelMapper.map( qna, QnaDTO.class );

        log.info("서비스에서 컨트롤러로 나간값 :  " +qnaDTO);

        return qnaDTO;
    }

    @Override
    public List<QnaDTO> list() {
        // 모든 QnA 게시글을 조회
        List<Qna> qnaList = qnaRepository.findAll();

        // 조회한 QnA 리스트를 로깅
        qnaList.forEach(qna -> log.info(qna.toString()));

        // QnA 엔티티 리스트를 QnA DTO로 변환
        List<QnaDTO> qnaDTOList = qnaList.stream()
                .map(qna -> modelMapper.map(qna, QnaDTO.class))  // 괄호가 빠져있었음
                .collect(Collectors.toList()); // 리스트로 변환

        // 변환된 DTO 리스트 로깅
        qnaDTOList.forEach(qnaDTO -> log.info(qnaDTO.toString()));

        return qnaDTOList;  // DTO 리스트 반환
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



    }

    @Override
    public void del(Long qna_num) {
        log.info("삭제로 들어온 값:" + qna_num);

        //qnaReplyRepository.deleteAllByQna_num(qna_num);
        //imageService.delAll(qna_num);
        qnaRepository.deleteById(qna_num);

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
