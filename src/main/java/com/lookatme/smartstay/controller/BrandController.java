package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.Util.PagenationUtil;
import com.lookatme.smartstay.dto.BrandDTO;
import com.lookatme.smartstay.dto.ImageDTO;
import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.repository.ImageRepository;
import com.lookatme.smartstay.repository.MemberRepository;
import com.lookatme.smartstay.service.BrandService;
import com.lookatme.smartstay.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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
    private final ImageService imageService;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private PagenationUtil pagenation;
    private final PagenationUtil pagenationUtil;

    //등록
    @GetMapping("/brandRegister")
    public String brandRegisterGet(Model model) {
        model.addAttribute("brandDTO", new BrandDTO());
        return "brand/brandRegister";
    }
    @PostMapping("/brandRegister")
    public String brandRegisterPost(Model model, BrandDTO brandDTO,  RedirectAttributes redirectAttributes,
                                    @RequestParam(value = "multi", required = false) List<MultipartFile> multi,
                                    @RequestParam(value = "mainImageIndex", required = false, defaultValue = "0") Long mainImageIndex,
                                    Principal principal) {

        try{
            log.info("brandRegister : " + brandDTO);
            if (multi != null){
                multi.forEach(multipartFile -> {log.info("multipartFile : " + multipartFile);});
            }

            brandService.insert(brandDTO, principal.getName(), multi);
            redirectAttributes.addFlashAttribute("msg", "등록이 완료되었습니다.");
            return "redirect:/brand/brandList";

        } catch (IllegalStateException e) {         //브랜드 중복가입시 에러메시지 알림 추가
            log.error("브랜드 등록 오류: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/brand/brandRegister";
        }catch (Exception e) {
            log.error("예상치 못한 오류 발생 : " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "브랜드 등록 중 오류가 발생했습니다.");
            return "redirect:/brand/brandRegister";
        }

    }


    //목록
    @GetMapping("/brandList") //슈퍼어드민만 사용
    public String brandList(Principal principal, PageRequestDTO pageRequestDTO, Model model) {
        log.info("목록진입");

        String email = principal.getName();
        Member member = memberRepository.findByEmail(email);

        Long brandNum = (member.getBrand() != null) ? member.getBrand().getBrand_num() : null;

        List<BrandDTO> brandDTOList = brandService.myBrand(brandNum, member);
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
    public String brandModifyPost(BrandDTO brandDTO, BindingResult bindingResult,
                                  @RequestParam(value = "delnumList", required = false) List<Long> delnumList,
                                  @RequestParam(value = "multi", required = false) List<MultipartFile> multi,
                                  ImageDTO imageDTO, RedirectAttributes redirectAttributes) throws Exception {

        if (bindingResult.hasErrors()) {
            log.info("유효성 검사 실패:" + bindingResult.getAllErrors());
            return "brand/brandModify";
        }
        log.info("유효성 통과");

        if (multi != null && multi.stream().allMatch(MultipartFile::isEmpty)) {
            multi = null;
        }
        if (delnumList != null && delnumList.isEmpty()) {
            delnumList = null;
        }

        brandService.update(brandDTO, multi, delnumList);
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

    @DeleteMapping("/deleteImage/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable Long imageId) {
        try {
            imageService.deleteImage(imageId);
            return ResponseEntity.ok("이미지가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            log.error("이미지 삭제 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 삭제 실패");
        }
    }

    //상태변경
    @PostMapping("/stateUpdate")
    @ResponseBody
    public ResponseEntity<BrandDTO> stateUpdate(@RequestParam("brand_num") Long brand_num){

        BrandDTO brandDTO = brandService.stateUpdate(brand_num);

        return ResponseEntity.ok(brandDTO);
    }
}
