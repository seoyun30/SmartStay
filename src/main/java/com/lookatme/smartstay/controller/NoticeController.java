package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.Util.PagenationUtil;
import com.lookatme.smartstay.config.CustomAuthenticationFailureHandler;
import com.lookatme.smartstay.constant.Role;
import com.lookatme.smartstay.dto.*;

import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.entity.Notice;
import com.lookatme.smartstay.service.HotelService;
import com.lookatme.smartstay.service.ImageService;
import com.lookatme.smartstay.service.MemberService;
import com.lookatme.smartstay.service.NoticeService;
import jakarta.persistence.EntityNotFoundException;
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
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;


    //등록 페이지 이동
    @GetMapping("/noticeRegister")
    public String noticeRegisterGet(NoticeDTO noticeDTO ){

        return "notice/noticeRegister";
    }

    //등록 내용 저장
    @PostMapping("/noticeRegister")
    public String noticeRegisterPost(@Valid NoticeDTO noticeDTO, BindingResult bindingResult, Long hotel_num, Principal principal,  @RequestParam("multipartFileList") List<MultipartFile> multipartFileList, Model model) throws Exception {

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

    @GetMapping("/userNoticeList")
    public String userNoticeList(PageRequestDTO pageRequestDTO, Model model) {


        log.info("모든 데이터를 읽어온다..." + pageRequestDTO);

        PageResponseDTO<NoticeDTO> pageResponseDTO = noticeService.userNoticeList(pageRequestDTO);

        log.info("전달되는 DTO" + pageResponseDTO);
        log.info("전달되는 DTO리스트 " + pageResponseDTO);



        model.addAttribute("pageResponseDTO", pageResponseDTO);


        return "notice/userNoticeList";
    }



    //상세 보기 페이지
    @GetMapping("/noticeRead")
    public String noticeRead(Long notice_num, Model model, PageRequestDTO pageRequestDTO, Principal principal, RedirectAttributes redirectAttributes) {

        log.info("컨트롤러 읽기로 들어온 게시글" + notice_num);
        log.info("컨트롤러 읽기로 들어온 페이징 " + pageRequestDTO);

        if(principal == null) {
            return "redirect:/member/login";
        }

        if (notice_num == null || notice_num.equals("")) {
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 글입니다.");
            log.info("들어온 게시글번호 이상함");

            return "redirect:/notice/noticeList";
        }


        try {
            NoticeDTO noticeDTO = noticeService.noticeRead(notice_num);
            log.info("컨트롤러에서 서비스 read() 불러온 값 " + noticeDTO);
            model.addAttribute("noticeDTO", noticeDTO);

        } catch (EntityNotFoundException e) {
            log.info("id로 값을 찾지 못함");
            log.info("데이터 없음");

            return "redirect:/notice/noticeList";

        }
        return "notice/noticeRead";

    }

    @GetMapping("/userNoticeRead")
    public String userNoticeRead(Long notice_num, Model model, PageRequestDTO pageRequestDTO, RedirectAttributes redirectAttributes) {

        log.info("컨트롤러 읽기로 들어온 게시글" + notice_num);
        log.info("컨트롤러 읽기로 들어온 페이징 " + pageRequestDTO);

        if (notice_num == null || notice_num.equals("")) {
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 글입니다.");
            log.info("들어온 게시글번호 이상함");

            return "redirect:/notice/userNoticeList";
        }


        try {
            NoticeDTO noticeDTO = noticeService.noticeRead(notice_num);
            log.info("컨트롤러에서 서비스 read() 불러온 값 " + noticeDTO);
            model.addAttribute("noticeDTO", noticeDTO);

        } catch (EntityNotFoundException e) {
            log.info("id로 값을 찾지 못함");
            log.info("데이터 없음");

            return "redirect:/notice/userNoticeList";

        }
        return "notice/userNoticeRead";

    }

    @GetMapping("/noticeModify")
    public String noticeModifyGet(@RequestParam Long notice_num, Principal principal, Model model){

        if ( principal == null) {
            return "redirect:/member/login";
        }

        NoticeDTO noticeDTO = noticeService.noticeRead(notice_num);
        if (noticeDTO != null) {
            model.addAttribute("noticeDTO", noticeDTO);
            return "notice/noticeModify";
        }else {
            return "redirect:/notice/noticeList";
        }

    }

    @PostMapping("/noticeModify")
    public String noticeModifyPost(@Valid NoticeDTO noticeDTO, BindingResult result, RedirectAttributes redirectAttributes,
                                   @RequestParam(value = "multipartFileList", required = false) List<MultipartFile> multipartFileList,
                                   @RequestParam(value = "delnumList", required = false) List<Long> delnumList,
                                   Principal principal) throws Exception {

        log.info("수정된 데이터 저장 시도: " + noticeDTO);

        // 입력 값 유효성 검사
        if (result.hasErrors()) {
            log.error("입력 값 유효성 검사 실패");
            log.error(result.getAllErrors());
            return "/notice/noticeModify";
        }
        log.info("들어온 사진");

        for ( MultipartFile  multipartFile : multipartFileList) {
            log.info(multipartFile.getOriginalFilename());
        }
        log.info("삭제할 값들");
        log.info(delnumList);
        log.info(delnumList);


        if (multipartFileList != null && multipartFileList.stream().allMatch(MultipartFile::isEmpty)) {
            multipartFileList = null;
        }
        if (delnumList != null && delnumList.isEmpty()) {
            delnumList = null;
        }

        String email = principal.getName();

        if(memberService.findbyEmail(email).getRole().name().equals("user")){

        }


        try {
            if(multipartFileList != null && !multipartFileList.isEmpty()){
                log.info("업로드된 이미지 파일: " + multipartFileList.size());
            }else {
                log.info("이미지 파일이 없습니다.");
            }

            noticeService.noticeModify(noticeDTO, email, multipartFileList, delnumList);
            redirectAttributes.addFlashAttribute("msg", "공지사항이 수정되었습니다." + noticeDTO.getNotice_num());
        } catch (Exception e) {
            log.error("공지사항 수정 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "공지사항 수정 중 오류가 발생했습니다.");
            return "redirect:/notice/noticeModify?notice_num=" + noticeDTO.getNotice_num();
        }

        return "redirect:/notice/noticeList";
    }

    @PostMapping("/noticeDelete")
    public String noticeDelete(Long notice_num, Principal principal){
        log.info("삭제 처리...");
        noticeService.noticeDelete(notice_num, principal.getName());

        return "redirect:/notice/noticeList";
    }
}
