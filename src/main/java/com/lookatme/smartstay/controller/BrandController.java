package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.Util.PagenationUtil;
import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.repository.MemberRepository;
import com.lookatme.smartstay.service.BrandService;
import com.lookatme.smartstay.service.ImageService;
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
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/brand")
public class BrandController {

    private final BrandService brandService;
    private final ImageService imageService;
    private final MemberRepository memberRepository;
    private PagenationUtil pagenation;
    private final PagenationUtil pagenationUtil;

    //등록
    @GetMapping("/brandRegister")
    public String brandRegisterGet(Model model) {
        model.addAttribute("brandDTO", new BrandDTO());
        return "brand/brandRegister";
    }
    @PostMapping("/brandRegister")
    public String brandRegisterPost(Model model, BrandDTO brandDTO, Principal principal,
                                    List<MultipartFile> multi, RedirectAttributes redirectAttributes) throws Exception {

        log.info("brandRegister : " + brandDTO);
        multi.forEach(multipartFile -> {log.info("multipartFile : " + multipartFile);});
        brandService.insert(brandDTO, principal.getName(), multi);
        redirectAttributes.addFlashAttribute("msg", "등록이 완료되었습니다.");
        return "redirect:/brand/brandList";
    }



   //목록
    @GetMapping("/brandList") //슈퍼어드민만 사용
    public String brandList(Principal principal, PageRequestDTO pageRequestDTO, Model model) {
        log.info("목록진입");

        String email = principal.getName();
        Member member = memberRepository.findByEmail(email);
        List<BrandDTO> brandDTOList =  brandService.myBrand(email, member);
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
    public String brandModifyPost(@Valid BrandDTO brandDTO, BindingResult bindingResult,
                                  @RequestParam("delnumList") List<Long> delnumList,
                                  @RequestParam("multipartFiles") List<MultipartFile> multipartFiles,
                                  ImageDTO imageDTO, RedirectAttributes redirectAttributes) throws Exception {

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
