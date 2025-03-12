package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.repository.MemberRepository;
import com.lookatme.smartstay.service.HotelService;
import com.lookatme.smartstay.service.MemberService;
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
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final HotelService hotelService;

    //등록
    @GetMapping("/qnaRegister")
    public String qnaRegisterGet(Model model, Principal principal) {
        QnaDTO qnaDTO = new QnaDTO();

        if (principal != null) {
            String loggedInEmail = principal.getName();
            log.info("현재 로그인한 사용자 이메일: " + loggedInEmail);
            Member member = memberRepository.findMemberByEmail(loggedInEmail)
                    .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
            qnaDTO.setWriter(member.getName());
        }

        List<HotelDTO> hotelList = hotelService.activeHotelList();

        model.addAttribute("qnaDTO", qnaDTO);
        model.addAttribute("hotelDTOList", hotelList);
        return "qna/qnaRegister";
    }

    @PostMapping("/qnaRegister")
    public String qnaRegisterPost(@Valid QnaDTO qnaDTO, BindingResult bindingResult) {

        log.info("컨트롤러로 들어온 값:" + qnaDTO);

        if (bindingResult.hasErrors()) {
            log.info("유효성 검사나 문제가 있다 아래 로그는 모든 문제를 출력");
            log.info(bindingResult.getAllErrors());

            return "qna/qnaRegister";
        }
        qnaService.register(qnaDTO);

        return "redirect:/qna/myQnaList";
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

        PageResponseDTO<QnaDTO> pageResponseDTO = qnaService.pagelist(pageRequestDTO);

        model.addAttribute("pageResponseDTO", pageResponseDTO);

        return "qna/qnaList";
    }

    //유저 페이지 목록
    @GetMapping("/myQnaList")
    public String myQnaList(Model model, PageRequestDTO pageRequestDTO ,Principal principal) {
        log.info("pageRequestDTO: " + pageRequestDTO);

        if (principal == null) {
            return "redirect:/member/login";
        }

        PageResponseDTO<QnaDTO> pageResponseDTO = qnaService.pagemylist(pageRequestDTO, principal.getName());

        model.addAttribute("pageResponseDTO", pageResponseDTO);

        return "qna/myQnaList";
    }

    //매니저 소속 호텔용 목록
    @GetMapping("/hcmQnaList")
    public String hcmQnaList(Model model,  PageRequestDTO pageRequestDTO, Principal principal) {
        log.info("pageRequestDTO" + pageRequestDTO);

        if (principal == null) {
            return "redirect:/member/login";
        }

        PageResponseDTO<QnaDTO> pageResponseDTO = qnaService.pagehlist(pageRequestDTO, principal.getName());
        model.addAttribute("pageResponseDTO", pageResponseDTO);

        log.info("qnaDTO: " + pageResponseDTO);

        return "qna/hcmQnaList";
    }

    //치프 소속 브랜드용 목록
    @GetMapping("/bcmQnaList")
    public String bcmQnaList(Model model,  PageRequestDTO pageRequestDTO, Principal principal) {
        log.info("pageRequestDTO" + pageRequestDTO);

        if (principal == null) {
            return "redirect:/member/login";
        }

        PageResponseDTO<QnaDTO> pageResponseDTO = qnaService.pageblist(pageRequestDTO, principal.getName());
        model.addAttribute("pageResponseDTO", pageResponseDTO);

        log.info("qnaDTO: " + pageResponseDTO);

        return "qna/bcmQnaList";
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
            MemberDTO memberDTO = memberService.findbyEmail(principal.getName());
            model.addAttribute("qnaDTO", qnaDTO);
            model.addAttribute("loginUserDTO", memberDTO);
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
    public String qnaModifyPost(QnaDTO qnaDTO, BindingResult bindingResult) {
        qnaService.modify(qnaDTO);

        log.info("업데이트포스"+qnaDTO);
        //log.info("업데이트포스"+pageRequestDTO);
        if (bindingResult.hasErrors()) {
            log.info("유효성검사 확인!!");
            log.info(bindingResult.getAllErrors());
            return "redirect:qna/qnaModify";
        }

        return "redirect:/qna/myQnaList";
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
