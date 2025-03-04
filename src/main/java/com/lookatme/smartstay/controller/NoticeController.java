package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.Util.PagenationUtil;
import com.lookatme.smartstay.constant.Role;
import com.lookatme.smartstay.dto.*;

import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.Notice;
import com.lookatme.smartstay.service.HotelService;
import com.lookatme.smartstay.service.ImageService;
import com.lookatme.smartstay.service.MemberService;
import com.lookatme.smartstay.service.NoticeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;
    private final MemberService memberService;
    private final HotelService hotelService;
    private final ModelMapper modelMapper;
    private final PagenationUtil pagenationUtil; //페이지번호 처리 클래스
    private final ImageService imageService;


    //등록 페이지 이동
    @GetMapping("/noticeRegister")
    public String noticeRegisterGet(NoticeDTO noticeDTO ){

        return "notice/noticeRegister";
    }

    //등록 내용 저장
    @PostMapping("/noticeRegister")
    public String noticeRegisterPost(@Valid NoticeDTO noticeDTO, Long hotel_num, BindingResult bindingResult, Principal principal,  @RequestParam("multipartFileList") List<MultipartFile> multipartFileList, Model model) throws Exception {

         log.info("컨트롤러로 들어온 값 " + noticeDTO);

         if(multipartFileList != null && !multipartFileList.isEmpty()){
             log.info("파일있음" );
             multipartFileList.forEach( multipartFile -> log.info(multipartFile.getOriginalFilename()));
         }else {
             log.info("파일없음" );

         }

         if (bindingResult.hasErrors()){
             log.info("유효성 검사에 문제가 있음");
             log.info(bindingResult.getAllErrors());

             return "notice/noticeRegister";
         }

         try {
             noticeService.noticeRegister(noticeDTO, multipartFileList , principal);

         }catch (Exception e){
             model.addAttribute("msg", e.getMessage());

             return "notice/noticeRegister";

         }


         return "redirect:/notice/noticeList";

    }

    //목록 페이지
    @GetMapping("/noticeList")
    public String noticeList(PageRequestDTO pageRequestDTO, Model model, Principal principal) {

        String email = principal.getName();
        log.info("로그인한 사용자 " + email);
        log.info("모든 데이터를 읽어온다..." + pageRequestDTO);

        PageResponseDTO<NoticeDTO> pageResponseDTO = noticeService.noticeList(pageRequestDTO, email);

        log.info("전달되는 DTO" + pageResponseDTO);
        log.info("전달되는 DTO리스트 " + pageResponseDTO);


        model.addAttribute("pageResponseDTO", pageResponseDTO);


        return "notice/noticeList";
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
