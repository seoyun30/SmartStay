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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class QnaService {

    private final QnaRepository qnaRepository;
    private final ModelMapper modelMapper;
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

    //등록
    public void register(QnaDTO qnaDTO) {
        log.info("등록 서비스 들어온 값: " + qnaDTO);

        String loggedInUser = getLoggedInUser();
        log.info("로그인된 사용자 ID: " + loggedInUser);

        Member member = memberRepository.findMemberByEmail(loggedInUser)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Hotel hotel = hotelRepository.findById(qnaDTO.getHotel_num())
                .orElseThrow(() -> new EntityNotFoundException("호텔을 찾을 수 없습니다."));

        // Qna 객체 생성 및 설정
        Qna qna = Qna.builder()
                .title(qnaDTO.getTitle())
                .content(qnaDTO.getContent())
                .writer(loggedInUser)
                .hotel(hotel)
                .build();

        // Qna 저장
        qna = qnaRepository.save(qna);
        log.info("저장 후 Qna 정보: " + qna);
    }

    //상세보기
    public QnaDTO read(Long qna_num) {
        log.info("서비스 읽기로 들어온값 : " + qna_num);

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

        log.info("서비스에서 컨트롤러로 나간값 : " + qnaDTO);

        return qnaDTO;
    }

    //수정
    public void modify(QnaDTO qnaDTO) {
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

    //삭제
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

    //일반목록
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

    //유저 마이페이지 목록
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

    //매니저 페이징 목록
    public PageResponseDTO<QnaDTO> pagehlist(PageRequestDTO pageRequestDTO, String email) {

        log.info("진입 : " + pageRequestDTO);

        // 로그인한 관리자의 소속된 호텔 정보를 가져옵니다.
        Member member = memberRepository.findByEmail(email);

        if (member == null || member.getHotel() == null) {
            log.info("사용자의 소속 호텔을 찾을 수 없음 : " + email);
            return null;  // 소속 호텔이 없다면 null 반환
        }

        Long adminHotelNum = member.getHotel().getHotel_num();  // 로그인한 사용자의 호텔 번호
        log.info("관리자 소속 호텔 번호 : " + adminHotelNum);

        Page<Qna> qnaPage = null;
        int currentPage = pageRequestDTO.getPage() - 1;
        int guestLimits = pageRequestDTO.getSize();
        Pageable qnaPageable = PageRequest.of(currentPage, guestLimits,
                Sort.by(Sort.Direction.DESC, "qna_num"));

        log.info("페이저블 : " + pageRequestDTO);

        if (pageRequestDTO.getType() == null || pageRequestDTO.getKeyword() == null || pageRequestDTO.getKeyword().equals("")) {
            qnaPage = qnaRepository.findbyHotel(adminHotelNum, qnaPageable);
        } else if (pageRequestDTO.getType().equals("t")) {
            log.info("제목으로 검색, 검색 키워드는: " + pageRequestDTO.getKeyword());
            qnaPage = qnaRepository.findByTitleAndHotel_numContaining(pageRequestDTO.getKeyword(), adminHotelNum, qnaPageable);
        } else if (pageRequestDTO.getType().equals("c")) {
            log.info("내용으로 검색, 검색 키워드는: " + pageRequestDTO.getKeyword());
            qnaPage = qnaRepository.findByContentContainingAndHotel_numContaining(pageRequestDTO.getKeyword(), adminHotelNum, qnaPageable);
        } else if (pageRequestDTO.getType().equals("w")) {
            log.info("작성자로 검색, 검색 키워드는: " + pageRequestDTO.getKeyword());
            qnaPage = qnaRepository.selectlikeWriterAndHotel_numContaining(pageRequestDTO.getKeyword(), adminHotelNum, qnaPageable);
        } else if (pageRequestDTO.getType().equals("tc")) {
            log.info("제목 + 내용 검색, 검색 키워드는: " + pageRequestDTO.getKeyword());
            qnaPage = qnaRepository.titleOrConAndHotel_numContaining(pageRequestDTO.getKeyword(), pageRequestDTO.getKeyword(), adminHotelNum, qnaPageable);
        } else if (pageRequestDTO.getType().equals("cw")) {
            log.info("내용 + 작성자로 검색, 검색 키워드는: " + pageRequestDTO.getKeyword());
            qnaPage = qnaRepository.findByTitleContainingOrWriterContainingAndHotel_numContaining(pageRequestDTO.getKeyword(), pageRequestDTO.getKeyword(), adminHotelNum, qnaPageable);
        } else if (pageRequestDTO.getType().equals("tcw")) {
            log.info("제목 + 내용 + 작성자로 검색, 검색 키워드는: " + pageRequestDTO.getKeyword());
            qnaPage = qnaRepository.titleOrConOrWrAnaHotel_numContaining(pageRequestDTO.getKeyword(), adminHotelNum, qnaPageable);
        }

        // 변환
        List<Qna> qnaList = qnaPage.getContent();
        qnaList.forEach(qna -> log.info(qna));

        // DTO 변환
        List<QnaDTO> qnaDTOList = qnaList.stream()
                .map(qna -> modelMapper.map(qna, QnaDTO.class)
                        .setHotelDTO(modelMapper.map(qna.getHotel(), HotelDTO.class)))
                .collect(Collectors.toList());

        log.info("qnaDTOList : " + qnaDTOList);

        PageResponseDTO<QnaDTO> qnaDTOPageResponseDTO = PageResponseDTO.<QnaDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(qnaDTOList)
                .total((int) qnaPage.getTotalElements())
                .build();

        return qnaDTOPageResponseDTO;
    }

    //치프 페이징 목록
    public PageResponseDTO<QnaDTO> pageblist(PageRequestDTO pageRequestDTO, String email) {

        log.info("진입 : " + pageRequestDTO);

        // 로그인한 관리자의 소속된 호텔 정보를 가져옵니다.
        Member member = memberRepository.findByEmail(email);

        if (member == null || member.getBrand() == null) {
            log.info("사용자의 소속 브랜드을 찾을 수 없음 : " + email);
            return null;
        }

        Long adminBrandNum = member.getBrand().getBrand_num();
        log.info("관리자 소속 호텔 번호 : " + adminBrandNum);

        Page<Qna> qnaPage = null;
        int currentPage = pageRequestDTO.getPage() - 1;
        int guestLimits = pageRequestDTO.getSize();
        Pageable qnaPageable = PageRequest.of(currentPage, guestLimits,
                Sort.by(Sort.Direction.DESC, "qna_num"));

        log.info("페이저블 : " + pageRequestDTO);

        if (pageRequestDTO.getType() == null || pageRequestDTO.getKeyword() == null || pageRequestDTO.getKeyword().equals("")) {
            qnaPage = qnaRepository.findbyBrand(adminBrandNum, qnaPageable);
        } else if (pageRequestDTO.getType().equals("t")) {
            log.info("제목으로 검색, 검색 키워드는: " + pageRequestDTO.getKeyword());
            qnaPage = qnaRepository.findByTitleAndBrand_numContaining(pageRequestDTO.getKeyword(), adminBrandNum, qnaPageable);
        } else if (pageRequestDTO.getType().equals("c")) {
            log.info("내용으로 검색, 검색 키워드는: " + pageRequestDTO.getKeyword());
            qnaPage = qnaRepository.findByContentContainingAndBrand_numContaining(pageRequestDTO.getKeyword(), adminBrandNum, qnaPageable);
        } else if (pageRequestDTO.getType().equals("w")) {
            log.info("작성자로 검색, 검색 키워드는: " + pageRequestDTO.getKeyword());
            qnaPage = qnaRepository.selectlikeWriterAndBrand_numContaining(pageRequestDTO.getKeyword(), adminBrandNum, qnaPageable);
        }else if(pageRequestDTO.getType().equals("h")){
            log.info( "작성자로 검색으로  검색키워드는"  +pageRequestDTO.getKeyword() );
            qnaPage =  qnaRepository.selectlikeHotelAndBrand_numContaining(pageRequestDTO.getKeyword(), adminBrandNum, qnaPageable);
        } else if (pageRequestDTO.getType().equals("tc")) {
            log.info("제목 + 내용 검색, 검색 키워드는: " + pageRequestDTO.getKeyword());
            qnaPage = qnaRepository.titleOrConAndBrand_numContaining(pageRequestDTO.getKeyword(), pageRequestDTO.getKeyword(), adminBrandNum, qnaPageable);
        } else if (pageRequestDTO.getType().equals("cw")) {
            log.info("내용 + 작성자로 검색, 검색 키워드는: " + pageRequestDTO.getKeyword());
            qnaPage = qnaRepository.findByTitleContainingOrWriterContainingAndBrand_numContaining(pageRequestDTO.getKeyword(), pageRequestDTO.getKeyword(), adminBrandNum, qnaPageable);
        } else if (pageRequestDTO.getType().equals("tcw")) {
            log.info("제목 + 내용 + 작성자로 검색, 검색 키워드는: " + pageRequestDTO.getKeyword());
            qnaPage = qnaRepository.titleOrConOrWrAnaBrand_numContaining(pageRequestDTO.getKeyword(), adminBrandNum, qnaPageable);
        }

        // 변환
        List<Qna> qnaList = qnaPage.getContent();
        qnaList.forEach(qna -> log.info(qna));

        // DTO 변환
        List<QnaDTO> qnaDTOList = qnaList.stream()
                .map(qna -> modelMapper.map(qna, QnaDTO.class)
                        .setHotelDTO(modelMapper.map(qna.getHotel(), HotelDTO.class)))
                .collect(Collectors.toList());

        log.info("qnaDTOList : " + qnaDTOList);

        PageResponseDTO<QnaDTO> qnaDTOPageResponseDTO = PageResponseDTO.<QnaDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(qnaDTOList)
                .total((int) qnaPage.getTotalElements())
                .build();

        return qnaDTOPageResponseDTO;
    }

}
