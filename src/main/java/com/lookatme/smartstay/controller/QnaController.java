package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.HotelDTO;
import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.dto.PageResponseDTO;
import com.lookatme.smartstay.dto.QnaDTO;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.repository.HotelRepository;
import com.lookatme.smartstay.repository.MemberRepository;
import com.lookatme.smartstay.service.HotelService;
import com.lookatme.smartstay.service.ImageService;
import com.lookatme.smartstay.service.QnaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/qna")
public class QnaController {

    private final QnaService qnaService;
    private final ImageService imageService;
    private final MemberRepository memberRepository;
    private final HotelRepository hotelRepository;
    private final HotelService hotelService;

    //등록
    @GetMapping("/qnaRegister")
    public String qnaRegisterGet(Model model, Principal principal) {
        QnaDTO qnaDTO = new QnaDTO();

        if (principal != null) {
            String loggedInEmail = principal.getName(); // 현재 로그인한 사용자의 이메일
            log.info("현재 로그인한 사용자 이메일: " + loggedInEmail);
            Member member = memberRepository.findMemberByEmail(loggedInEmail)
                    .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
            qnaDTO.setWriter(member.getName()); // 이메일이 아닌 '이름'을 작성자로 설정
        }

        // 호텔 목록을 가져오는 부분
        List<HotelDTO> hotelList = hotelService.hotelList(); // 모든 호텔을 가져옴

        model.addAttribute("qnaDTO", qnaDTO);
        model.addAttribute("hotelDTOList", hotelList);
        return "qna/qnaRegister";
    }

    @PostMapping("/qnaRegister")
    public String qnaRegisterPost(@Valid QnaDTO qnaDTO, BindingResult bindingResult, MultipartFile[] multipartFiles) {

        log.info("컨트롤러로 들어온 값:" + qnaDTO);
        //log.info("컨트롤러로 들어온 값:" + MultipartFile.getOriginalFilename());

        if (bindingResult.hasErrors()) {
            log.info("유효성 검사나 문제가 있다 아래 로그는 모든 문제를 출력");
            log.info(bindingResult.getAllErrors());

            return "qna/qnaRegister";
        }
        qnaService.register(qnaDTO, multipartFiles);

        return "redirect:/qna/qnaList";
    }

    //목록
    @GetMapping("/qnaList")
    public String qnaList(Model model, PageRequestDTO pageRequestDTO, Principal principal) {
        log.info("pageRequestDTO: " + pageRequestDTO);
        String loggedInEmail = null;
        if (principal != null) {
            loggedInEmail = principal.getName();
        }
        model.addAttribute("loggedInEmail", loggedInEmail);
        // 페이징 처리된 QnA 목록을 반환받음 (호텔 정보 포함)
        PageResponseDTO<QnaDTO> pageResponseDTO = qnaService.pagelist(pageRequestDTO);
        // QnA 목록을 모델에 추가
        model.addAttribute("pageResponseDTO", pageResponseDTO);
        // 추가로, 페이지 관련 정보나 다른 데이터를 넘길 수 있음
        // 예: 현재 페이지, 총 페이지 수, 이전/다음 페이지 상태 등
        return "qna/qnaList";  // qnaList.html 또는 jsp 등으로 반환
    }

    @GetMapping("/myQnaList")
    public String myQnaList(Model model, PageRequestDTO pageRequestDTO ,Principal principal) {
        log.info("pageRequestDTO: " + pageRequestDTO);

        if (principal == null) {
            return "redirect:/member/login";
        }

        PageResponseDTO<QnaDTO> pageResponseDTO = qnaService.pagemylist(pageRequestDTO, principal.getName());

        model.addAttribute("pageResponseDTO", pageResponseDTO);

        return "qna/myQnaList";  // myQnaList.html 또는 jsp 등으로 반환
    }

    //cm목록
    @GetMapping("/hcmQnaList")
    public String hcmQnaList(Model model,  PageRequestDTO pageRequestDTO, Long hotel_num, Principal principal) {
        log.info("pageRequestDTO" + pageRequestDTO);

        if (principal == null) {
            return "redirect:/member/login";
        }

        PageResponseDTO<QnaDTO> pageResponseDTO = qnaService.pagehlist(pageRequestDTO, hotel_num, principal.getName());
        model.addAttribute("pageResponseDTO", pageResponseDTO);

        log.info("qnaDTO: " + pageResponseDTO);

        return "qna/hcmQnaList";
    }

    //상세
    @GetMapping("/qnaRead")
    public String qnaRead(Long qna_num, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        log.info("컨트롤러 읽기로 들어온 게시글 번호:" + qna_num);

        String loggedInEmail = null;
        if (principal != null) {
            loggedInEmail = principal.getName();
        }
        model.addAttribute("loggedInEmail", loggedInEmail);

        if (qna_num == null || qna_num.equals("")) {
            log.info("들어온 id 가 이상함");
            return "redirect:/qna/qnaList";
        }

        try {
            QnaDTO qnaDTO = qnaService.read(qna_num);
            model.addAttribute("qnaDTO", qnaDTO);
        } catch (EntityNotFoundException e) {
            log.info("id로 값을 찾지 못함");
            redirectAttributes.addFlashAttribute("errorMessage", "해당 게시글을 찾을 수 없습니다.");
            return "redirect:/qna/qnaList";
        }

        return "qna/qnaRead";
    }

    //수정
    @GetMapping("/qnaModify")
    public String qnaModifyGet(Long qna_num, Model model) { //PageRequestDTO pageRequestDTO

        if(qna_num == null || qna_num.equals("")) {
            log.info("들어온 qna_num이 이상함");
            return "redirect:/qna/qnaList"; //+pageRequestDTO.getLink();
        }
        //try~chach문으로 이미지 넣을것

        // QnaDTO를 가져와서 model에 추가
        try {
            QnaDTO qnaDTO = qnaService.read(qna_num);
            model.addAttribute("qnaDTO", qnaDTO);
        } catch (EntityNotFoundException e) {
            log.info("해당 id로 게시글을 찾을 수 없음");
            return "redirect:/qna/qnaList";
        }

        return "qna/qnaModify";
    }

    @PostMapping("/qnaModify")
    public String qnaModifyPost(QnaDTO qnaDTO, BindingResult bindingResult, Long[] delino, Model model) {
        qnaService.modify(qnaDTO);

        log.info("업데이트포스"+qnaDTO);
        //log.info("업데이트포스"+pageRequestDTO);
        if (bindingResult.hasErrors()) {
            log.info("유효성검사 확인!!");
            log.info(bindingResult.getAllErrors()); //유효성 내용 콘솔창에 출력
            return "redirect:qna/qnaModify";
        }

        return "redirect:/qna/myQnaList"; //+pageRequestDTO.getLink()
    }

    //삭제
    @PostMapping("/qnaDelete")
    public String qnaDelete(Long qna_num) {
        log.info(qna_num);
        qnaService.del(qna_num);
        return "redirect:/qna/myQnaList";
    }

    //조회수 상승 코드(REST 방식)
    @PostMapping("/incrementViewCount")
    public ResponseEntity incrementViewCont(@RequestBody Map<String,Long> request) {

        Long qna_num = request.get("qna_num");
        log.info("qna_num: " + qna_num);

        if (qna_num != null) {
            qnaService.incrementViewCount(qna_num);
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.badRequest().build();
        }
    }

}
