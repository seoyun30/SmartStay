package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.Util.PagenationUtil;
import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.entity.Chief;
import com.lookatme.smartstay.service.ChiefService;
import com.lookatme.smartstay.service.ManagerService;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/chief")
public class ChiefController {

    private final ChiefService chiefService;
    private final ManagerService managerService;
    private PagenationUtil pagenation;
    private final PagenationUtil pagenationUtil;

    //등록
    @GetMapping("/chiefRegister")
    public String chiefRegisterGet(Model model) {
        model.addAttribute("chiefDTO", new ChiefDTO());
        return "chief/chiefRegister";
    }
    @PostMapping("/chiefRegister")
    public String chiefRegisterPost(Model model, ChiefDTO chiefDTO,
                                    List<MultipartFile> multipartFiles, RedirectAttributes redirectAttributes) throws Exception {
        chiefService.insert(chiefDTO, multipartFiles);
        redirectAttributes.addFlashAttribute("msg", "등록이 완료되었습니다.");
        return "redirect:/chief/chiefList";
    }

    //목록
    @GetMapping("/chiefList") //슈퍼어드민만 사용
    public String chiefList(Principal principal, PageRequestDTO pageRequestDTO, Model model) {
        log.info("목록진입");

        List<ChiefDTO> chiefDTOList =  chiefService.chiefList();
       model.addAttribute("chiefDTOList", chiefDTOList);
        return "chief/chiefList";
    }

    //상세보기
    @GetMapping("/chiefRead")
    public String chiefRead(Long chief_num, Model model) {
        ChiefDTO chiefDTO=chiefService.read(chief_num);
        model.addAttribute("chiefDTO", chiefDTO);
        return "chief/chiefRead";
    }

    //수정
    @GetMapping("/chiefModify")
    public String chiefModifyGet(Long chief_num, Model model) {
        ChiefDTO chiefDTO=chiefService.read(chief_num);
        model.addAttribute("chiefDTO", chiefDTO);// end접속해제
        return "chief/chiefModify";
    }
    @PostMapping("/chiefModify")
    public String chiefModifyPost(@Valid ChiefDTO chiefDTO, BindingResult bindingResult, @RequestParam("delnumList") List<Long> delnumList,
                                  @RequestParam("multipartFiles") List<MultipartFile> multipartFiles, ImageDTO imageDTO, RedirectAttributes redirectAttributes) throws Exception {
        //@RequestParam("delnumList") List<Long> delnumList, @RequestParam("multipartFiles") List<MultipartFile> multipartFiles
        //사진등록, 사진삭제번호 할때 사용하는 방법

        if (bindingResult.hasErrors()) {
            log.info("유효성 검사 실패:" + bindingResult.getAllErrors());
            return "chief/chiefModify";
        }
        log.info("유효성 통과");

        chiefService.update(chiefDTO, multipartFiles );
        redirectAttributes.addFlashAttribute("msg", "수정 완료되었습니다.");

        log.info("수정 완료");
        return "redirect:/chief/chiefList";
    }
    //삭제
    @PostMapping("/chiefDelete")
    public String chiefDelete(long id) {
        log.info("삭제할 번호 :"+id);
        chiefService.delete(id);
        return "redirect:/chief/chiefList";
    }


    //매장 등록
    @GetMapping("/managerRegister")
    public String managerRegisterGet(Model model) {
        model.addAttribute("managerDTO", new ManagerDTO());
        return "manager/managerRegister";
    }

    @PostMapping("/managerRegister")
    public String managerRegisterPost(ManagerDTO managerDTO, MemberDTO memberDTO,
                                      List<MultipartFile> multipartFiles) throws Exception {
        managerService.managerInsert(managerDTO, multipartFiles);
        return "chief/managerRegister";
    }

    //매장 목록
    @GetMapping("/managerList")
    public String mangerList(Principal principal, PageRequestDTO pageRequestDTO) {
        managerService.managerList();
        return "chief/managerList";
    }

    //매장 상세보기
    @GetMapping("/managerRead")
    public String managerRead(Principal principal, PageRequestDTO pageRequestDTO) {
       // managerService.managerRead();
        return "chief/managerRead";
    }
    //@GetMapping("/manager/managerRead")
    //public String managerRead(Principal principal, PageRequestDTO pageRequestDTO) {
    //    return "manager/managerRead";
    //}

    //매장 수정
    @GetMapping({"/managerModify", "manager/managerModify"})
    public String managerModifyGet(Principal principal, PageRequestDTO pageRequestDTO) {
        return "managerModify";
    }

    @PostMapping({"/managerModify", "manager/magerModify"})
    public String managerModifyPost(ManagerDTO managerDTO, MemberDTO memberDTO, List<Long> delnumList,
                                    List<MultipartFile> multipartFiles, ImageDTO imageDTO, PageRequestDTO pageRequestDTO) {
        managerService.managerUpdate(managerDTO);
        return "managerModify";
    }

    //매장 삭제
    @PostMapping("/mangaerDelete")
    public String mangaerDelete(ManagerDTO managerDTO) {
        managerService.managerDelete(managerDTO.getManager_num());
        return "mangaerList";
    }
}
