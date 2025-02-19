package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.Util.PagenationUtil;
import com.lookatme.smartstay.dto.*;

import com.lookatme.smartstay.service.ImageService;
import com.lookatme.smartstay.service.NoticeServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeServiceImpl noticeService;
    private final PagenationUtil pagenationUtil; //페이지번호 처리 클래스
    private final ImageService imageService;


    //목록 페이지
    @GetMapping("/noticeList")
    public String noticeList(PageRequestDTO pageRequestDTO, Model model){
        log.info("모든 데이터를 읽어온다..." + pageRequestDTO);

        List<NoticeDTO> noticeDTOS = noticeService.noticeList();

//        Map<String, Integer> pageInfo = pagenationUtil.pagination(noticeDTOS);
        model.addAttribute("noticeList", noticeDTOS);
        System.out.println("노티스노티스" + noticeDTOS);
//        model.addAttribute("pageInfo", pageInfo);

        return "notice/noticeList";
    }

    //등록 페이지 이동
    @GetMapping("/noticeRegister")
    public String noticeRegisterGet(){
        log.info("입력폼 페이지 이동..." );

        return "notice/noticeRegister";
    }

    //등록 내용 저장
    @PostMapping("/noticeRegister")
    public String noticeRegisterPost(NoticeDTO noticeDTO, Principal principal, List<MultipartFile> multipartFileList) throws Exception {

        log.info("입력폼 내용을 저장..." + noticeDTO);
//        log.info("들어온값 : " + multipartFileList.get(0).getOriginalFilename());
        // principal.getName() 로그인한 사람으로 메일로 ...
        noticeService.noticeRegister(noticeDTO, principal.getName(), multipartFileList);

        return "redirect:/notice/noticeList";
    }

    //상세 보기 페이지
    @GetMapping("/noticeRead")
    public String noticeRead(Long id, Model model){
        log.info("개별읽기..." + id);

//        log.info("컨드롤러 읽기로 들어온 페이징처리 : " + pageRequestDTO);

//        if (id == null || id.equals("")){
//            log.info("들어온 notice_num가 이상함");
//            return "redirect:/notice/noticeList";
//        }

//        NoticeDTO noticeDTO = new NoticeDTO();
//        try {
            NoticeDTO noticeDTO = noticeService.noticeRead(id);
        System.out.println("없니?"+ noticeDTO);
            model.addAttribute("noticeDTO", noticeDTO);
        return "notice/noticeRead";
//        } catch (EntityNotFoundException e) {
//            log.info("id 값을 찾지 못함");
//            return "redirect:/notice/noticeList";
//        }
//            List<ImageDTO> imageDTOList =
//                    imageService.findImagesByTarget("notice", notice_num)
//        }
//        NoticeDTO noticeDTO = noticeService.noticeRead(notice_num);
//        System.out.println("sss");
//
//        log.info("개별정보를 페이지에 전달...");
//        model.addAttribute("notice", noticeDTO);


    }

    //
    @GetMapping("/noticeModify")
    public String noticeModifyGet(Long notice_num, Principal principal, PageRequestDTO pageRequestDTO, Model model){

        NoticeDTO noticeDTO = noticeService.noticeRead(notice_num);
        model.addAttribute("notice", noticeDTO);
        return "notice/noticeModify";
    }

    @PostMapping("/noticeModify")
    public String noticeModifyPost(NoticeDTO noticeDTO,
                                   @RequestParam(value = "multipartFileList", required = false) List<MultipartFile> multipartFileList,
                                   PageRequestDTO pageRequestDTO, @RequestParam(value = "delnumList", required = false, defaultValue = "0") List<Long> delnumList) {

        log.info("수정된 데이터 저장..." + noticeDTO);
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
