package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.Util.PagenationUtil;
import com.lookatme.smartstay.dto.*;

import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.Notice;
import com.lookatme.smartstay.service.ImageService;
import com.lookatme.smartstay.service.MemberService;
import com.lookatme.smartstay.service.NoticeServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.xml.transform.Result;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeServiceImpl noticeService;
    private final MemberService memberService;
    private final PagenationUtil pagenationUtil; //페이지번호 처리 클래스
    private final ImageService imageService;


    //목록 페이지
    @GetMapping("/noticeList")
    public String noticeList(PageRequestDTO pageRequestDTO, Model model){
        log.info("모든 데이터를 읽어온다..." + pageRequestDTO);

        List<NoticeDTO> noticeDTOList = noticeService.noticeList();

//        Map<String, Integer> pageInfo = pagenationUtil.pagination(noticeDTOS);
        model.addAttribute("noticeList", noticeDTOList);
        System.out.println("노티스노티스" + noticeDTOList);
//        model.addAttribute("pageInfo", pageInfo);

        return "notice/noticeList";
    }

    //등록 페이지 이동
    @GetMapping("/noticeRegister")
    public String noticeRegisterGet(Principal principal, Model model, RedirectAttributes redirectAttributes){
        log.info("입력폼 페이지 이동..." );


        // 1. 로그인 확인 **: Principal이 null이면 로그인 페이지로 이동 //로그인 창에서 오류가 확인되어야 출력..
        if (principal == null) {
            log.info("로그인 후 이용 가능합니다.");
            return "redirect:/member/login";
        }

        //현재 로그인한 사용자의 정보 조회(성공)// 등록 버튼을 권한이 없을 시 안보이게 가려놓음
        MemberDTO memberDTO = memberService.readMember(principal.getName());
        // 2. 권한확인**: chief || Manager가 아닐 경우 페이지 이동 오류
        if (memberDTO != null && memberDTO.getRole().name().equals("USER")) {
            redirectAttributes.addFlashAttribute("errorMessage", "작성 권한이 없습니다.");
            return "redirect:/notice/noticeList";   //목록으로
        }

        return "notice/noticeRegister";
    }

    //등록 내용 저장
    @PostMapping("/noticeRegister")
    public String noticeRegisterPost(@Valid NoticeDTO noticeDTO,Long hotel_num, BindingResult result, Principal principal, List<MultipartFile> multipartFileList, Model model) throws Exception {
        log.info("입력폼 내용을 저장..." + noticeDTO);
        System.out.println("저장된 내용"+ noticeDTO);

        //폼 유효성 검사
        if (result.hasErrors()) {
            System.out.println(result.hasErrors());
            model.addAttribute("registerErrorMessage", "제목과 내용을 모두 입력해주세요.");
            return "redirect:/notice/noticeRegister";  // 유효성 검사 실패시 오류
        }


        try {
            // 공지사항 등록 서비스 호출
            noticeService.noticeRegister(noticeDTO, principal.getName(),hotel_num, multipartFileList);
            return "redirect:/notice/noticeList";  // 등록 성공 시 목록 페이지로 이동
        } catch (Exception e) {
            // 기타 예외
            model.addAttribute("registerErrorMessage","공지사항 등록 중 오류가 발생했습니다." );
            return "redirect:/notice/noticeList";  // 오류 발생 시 목록 페이지로 이동
        }


    }

    //상세 보기 페이지
    @GetMapping("/noticeRead")
    public String noticeRead(Long id, Model model){

        log.info("개별읽기..." + id);

        NoticeDTO noticeDTO = noticeService.noticeRead(id);
        System.out.println("없니?"+ noticeDTO);
        model.addAttribute("noticeDTO", noticeDTO);
        return "notice/noticeRead";

    }

    //
    @GetMapping("/noticeModify")
    public String noticeModifyGet(Long notice_num, RedirectAttributes redirectAttributes,Principal principal, PageRequestDTO pageRequestDTO, Model model){
        log.info("수정 폼 페이지 이동..");

        //로그인한 사용자 정보 조회
        MemberDTO memberDTO = memberService.readMember(principal.getName());
        if (principal == null){

        }
        System.out.println("들어오니??");
//        // 권한이 있는지 확인
//        if (memberDTO != null && memberDTO.getRole().name().equals("USER")) {
//            redirectAttributes.addFlashAttribute("errorMessage", "작성자가 아님으로 수정할 권한이 없습니다.");
//            return "redirect:/notice/noticeRead?notice_num=" + notice_num; // 오류 메시지를 추가하여 read 페이지로 리다이렉트
//        }


        model.addAttribute("noticeDTO", noticeService.noticeRead(notice_num));

        // 공지 사항 조회
        NoticeDTO noticeDTO = noticeService.noticeRead(notice_num);
        if (noticeDTO == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "해당 공지사항을 찾을 수 없습니다.");
            return "redirect:/notice/noticeRead";
        }

        // 공지사항 작성자와 로그인한 사용자 이메일 비교
        if (!noticeDTO.getCreate_by().equals(principal.getName())) {
            redirectAttributes.addFlashAttribute("errorMessage", "작성자가 아님으로 수정할 권한이 없습니다.");
            return "redirect:/notice/noticeRead";
        }


        // 조회된 공지사항 데이터를 수정 페이지에 전달
        model.addAttribute("notice", noticeDTO);
        return "notice/noticeModify";
    }

    @PostMapping("/noticeModify")
    public String noticeModifyPost(@Valid NoticeDTO noticeDTO, BindingResult result, String email,
                                   @RequestParam(value = "multipartFileList", required = false) List<MultipartFile> multipartFileList,
                                   PageRequestDTO pageRequestDTO,
                                   @RequestParam(value = "delnumList", required = false, defaultValue = "0") List<Long> delnumList,
                                   RedirectAttributes redirectAttributes) {

        log.info("수정된 데이터 저장 시도: " + noticeDTO);

        // 입력 값 유효성 검사
        if (result.hasErrors()) {
            log.error("입력 값 유효성 검사 실패");
            redirectAttributes.addFlashAttribute("errorMessage", "입력이 올바르지 않습니다.");
            return "redirect:/notice/noticeModify" + noticeDTO.getNotice_num(); // 수정 페이지로 다시 이동
        }
        try { // 공지사항 수정 서비스 호출
            noticeService.noticeModify(noticeDTO, email, multipartFileList, delnumList);
        } catch (IllegalArgumentException e) {
            // 변경된 내용이 없는 경우 처리
            log.error("변경된 내용이 없습니다: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "변경된 내용이 없습니다.");
            return "redirect:/notice/noticeModify";
        } catch (Exception e) { //기타 예외 처리
            log.error("공지사항 수정 중 오류 발생: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "공지사항 수정 중 오류가 발생했습니다.");
            return "redirect:/notice/noticeModify";
        }
//        noticeService.noticeModify(noticeDTO, multipartFileList, delnumList);

        //수정 성공 시 목록 페이지로 이동
        return "redirect:/notice/noticeList";
    }

    @PostMapping("/noticeDelete")
    public String noticeDelete(Long notice_num){
        log.info("삭제 처리...");
        noticeService.noticeDelete(notice_num);

        return "redirect:/notice/noticeList";
    }
}
