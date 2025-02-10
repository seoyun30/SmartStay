package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.service.NoticeServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeServiceImpl noticeService;


    //목록 페이지
    @GetMapping("/noticeList")
    public String noticeListFrom(PageRequestDTO pageRequestDTO, Model model){
        log.info("모든 데이터를 읽어온다...");
        List<NoticeDTO> noticeDTOList = noticeService.noticeList();

        model.addAttribute("noticeDTOList", noticeDTOList);
        return "notice/noticeList";
    }

    //등록 페이지 이동
    @GetMapping("/noticeRegister")
    public String noticeRegisterFrom(){
        log.info("입력폼 페이지 이동...");

        return "notice/noticeRegister";
    }

    //등록 내용 저장
    @PostMapping("/noticeRegister")
    public String noticeRegisterPost(NoticeDTO noticeDTO, MemberDTO memberDTO, List<MultipartFile> multipartFileList){
        log.info("입력폼 내용을 저장...");
        noticeService.noticeRegister(noticeDTO);

        return "redirect:/notice/noticeList";
    }

    //상세 보기 페이지
    @GetMapping("/noticeRead")
    public String noticeRead(Long id, PageRequestDTO pageRequestDTO, Model model){
        log.info("개별읽기...");
        NoticeDTO noticeDTO = noticeService.noticeRead(id);

        log.info("개별정보를 페이지에 전달...");
        model.addAttribute("notice", noticeDTO);

        return "notice/noticeRead";
    }

    //
    @GetMapping("/noticeModify")
    public String noticeModifyGet(Long notice_num, Principal principal, PageRequestDTO pageRequestDTO, Model model){
        log.info("수정할 데이터 읽기...");
        NoticeDTO noticeDTO = noticeService.noticeRead(notice_num);

        log.info("개별정보를 페이지에 전달...");
        model.addAttribute("notice", noticeDTO);

        return "notice/noticeModify";
    }

    @PostMapping("/noticeModify")
    public String noticeModifyPost(NoticeDTO noticeDTO, MemberDTO memberDTO,
                                   List<MultipartFile> multipartFileList,
                                   PageRequestDTO pageRequestDTO, ImageDTO imageDTO, List<Long> delnumList) {
        log.info("수정된 데이터 저장...");
        noticeService.noticeModify(noticeDTO, multipartFileList, delnumList);

        return "redirect:/notice/noticeList";
    }

    @PostMapping("/noticeDelete")
    public String noticeDelete(Long notice_num){
        log.info("삭제 처리...");
        noticeService.noticeDelete(notice_num);

        return "redirect:/notice/noticeList";
    }
}
