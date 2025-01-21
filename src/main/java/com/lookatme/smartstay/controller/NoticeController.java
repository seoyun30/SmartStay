package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.ImageDTO;
import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.dto.NoticeDTO;
import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/List")
    public String noticeListFrom(PageRequestDTO pageRequestDTO, Model model){
        log.info("모든 데이터를 읽어온다...");
        List<NoticeDTO> noticeDTOList = noticeService.noticeList();

        model.addAttribute("noticeDTOList", noticeDTOList);
        return "notice/noticeList";
    }

    @GetMapping("/Register")
    public String noticeRegisterFrom(){
        log.info("입력폼 페이지 이동...");

        return "notice/noticeRegister";
    }

    @PostMapping("/Register")
    public String noticeRegisterPost(NoticeDTO noticeDTO, MemberDTO memberDTO, List<MultipartFile> multipartFileList){
        log.info("입력폼 내용을 저장...");
        noticeService.noticeRegister(noticeDTO);

        return "redirect:/noticeList";
    }

    @GetMapping("/Read")
    public String noticeRead(Long id, PageRequestDTO pageRequestDTO, Model model){
        log.info("개별읽기...");
        NoticeDTO noticeDTO = noticeService.noticeRead(id);

        log.info("개별정보를 페이지에 전달...");
        model.addAttribute("notice", noticeDTO);

        return "notice/noticeRead";
    }

    @GetMapping("/Modify")
    public String noticeModifyGet(Long notice_num, Principal principal, PageRequestDTO pageRequestDTO, Model model){
        log.info("수정할 데이터 읽기...");
        NoticeDTO noticeDTO = noticeService.noticeRead(notice_num);

        log.info("개별정보를 페이지에 전달...");
        model.addAttribute("notice", noticeDTO);

        return "notice/noticeModify";
    }

    @PostMapping("/Modify")
    public String noticeModifyPost(NoticeDTO noticeDTO, MemberDTO memberDTO, List<MultipartFile> multipartFileList,
                                   PageRequestDTO pageRequestDTO, ImageDTO imageDTO){
        log.info("수정된 데이터 저장...");
        noticeService.noticeModify(noticeDTO);

        return "redirect:/noticeModify";
    }

    @PostMapping("/Delete")
    public String noticeDelete(Long notice_num){
        log.info("삭제 처리...");
        noticeService.noticeDelete(notice_num);

        return "redirect:/noticeList";
    }
}
