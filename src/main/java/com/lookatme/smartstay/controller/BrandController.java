package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.Util.PagenationUtil;
import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.service.BrandService;
import jakarta.validation.Valid;
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
@RequestMapping("/brand")
public class BrandController {

    private final BrandService brandService;
    private PagenationUtil pagenation;
    private final PagenationUtil pagenationUtil;

    //등록
    @GetMapping("/brandRegister")
    public String brandRegisterGet(Model model) {
        model.addAttribute("brandDTO", new BrandDTO());
        return "brand/brandRegister";
    }
    @PostMapping("/brandRegister")
    public String brandRegisterPost(Model model, BrandDTO brandDTO,
                                    List<MultipartFile> multipartFiles, RedirectAttributes redirectAttributes) throws Exception {
        brandService.insert(brandDTO, multipartFiles);
        redirectAttributes.addFlashAttribute("msg", "등록이 완료되었습니다.");
        return "redirect:/brand/brandList";
    }

    //목록
    @GetMapping("/brandList") //슈퍼어드민만 사용
    public String brandList(Principal principal, PageRequestDTO pageRequestDTO, Model model) {
        log.info("목록진입");

        List<BrandDTO> brandDTOList =  brandService.brandList();
       model.addAttribute("brandDTOList", brandDTOList);
        return "brand/brandList";
    }

    //상세보기
    @GetMapping("/brandRead")
    public String brandRead(Long brand_num, Model model) {
        BrandDTO brandDTO = brandService.read(brand_num);
        model.addAttribute("brandDTO", brandDTO);
        return "brand/brandRead";
    }

    //수정
    @GetMapping("/brandModify")
    public String brandModifyGet(Long brand_num, Model model) {
        BrandDTO brandDTO = brandService.read(brand_num);
        model.addAttribute("brandDTO", brandDTO);// end접속해제
        return "brand/brandModify";
    }
    @PostMapping("/brandModify")
    public String brandModifyPost(@Valid BrandDTO brandDTO, BindingResult bindingResult, @RequestParam("delnumList") List<Long> delnumList,
                                  @RequestParam("multipartFiles") List<MultipartFile> multipartFiles, ImageDTO imageDTO, RedirectAttributes redirectAttributes) throws Exception {
        //@RequestParam("delnumList") List<Long> delnumList, @RequestParam("multipartFiles") List<MultipartFile> multipartFiles
        //사진등록, 사진삭제번호 할때 사용하는 방법

        if (bindingResult.hasErrors()) {
            log.info("유효성 검사 실패:" + bindingResult.getAllErrors());
            return "brand/brandModify";
        }
        log.info("유효성 통과");

        brandService.update(brandDTO, multipartFiles );
        redirectAttributes.addFlashAttribute("msg", "수정 완료되었습니다.");

        log.info("수정 완료");
        return "redirect:/brand/brandList";
    }
    //삭제
    @PostMapping("/brandDelete")
    public String brandDelete(long id) {
        log.info("삭제할 번호 :"+id);
        brandService.delete(id);
        return "redirect:/brand/brandList";
    }
}
