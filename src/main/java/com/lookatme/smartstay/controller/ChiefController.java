package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.Util.PagenationUtil;
import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.entity.Chief;
import com.lookatme.smartstay.service.ChiefService;
import com.lookatme.smartstay.service.ManagerService;
import lombok.Getter;
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
                                    List<MultipartFile> multipartFiles) throws Exception {
        chiefService.insert(chiefDTO, multipartFiles);
        return "chief/chiefRegister";
    }

    //목록
    @GetMapping("/chiefList") //슈퍼어드민만 사용
    public String chiefList(Principal principal, PageRequestDTO pageRequestDTO) {
        chiefService.chiefList();
        return "chief/chiefList";
    }

    //상세보기
    @GetMapping("/chiefRead")
    public String chiefRead() {
        //chiefService.read();
        return "chief/chiefList";
    }

    //수정
    @GetMapping("/chiefModify")
    public String chiefModifyGet(Model model) {
        return "chiefModify";
    }
    @PostMapping("/chiefModify")
    public String chiefModifyPost(ChiefDTO chiefDTO, List<Long> delnumList,
                                  List<MultipartFile> multipartFiles, ImageDTO imageDTO) {
        chiefService.update(chiefDTO);

        return "chiefList";
    }
    //삭제
    @PostMapping("/chiefDelete")
    public String chiefDelete(ChiefDTO chiefDTO) {
        chiefService.delete(chiefDTO.getChief_num());
        return "chief/chiefList";
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
