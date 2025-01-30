package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.BrandDTO;
import com.lookatme.smartstay.dto.HotelDTO;
import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.service.BrandService;
import com.lookatme.smartstay.service.HotelService;
import com.lookatme.smartstay.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/member")
public class LoginController {

    private final MemberService memberService;
    private final BrandService brandService;
    private final HotelService hotelService;

    @GetMapping("/adTerms") // 약관페이지(총판,매니져)
    public String adTerms(){
        return "member/adTerms";
    }

    @GetMapping("/terms") //약관페이지(유저)
    public String terms(){
        return "member/adTerms";
    }

    @GetMapping("/adSignup") //회원가입페이지(saveSuperAdminMember-슈퍼어드민이 승인해주는 첫번째 치프)
    public String adSignupGet(Model model){
        model.addAttribute("memberDTO", new MemberDTO());
        model.addAttribute("brandDTO", new BrandDTO());
        log.info("회원가입");
        return "member/adSignup";
    }

    @PostMapping("/adSignup") //회원가입포스트(saveSuperAdminMember-슈퍼어드민이 승인해주는 첫번째 치프)
    public String adSignupPost(@Valid MemberDTO memberDTO, BindingResult bindingResult,
                             RedirectAttributes redirectAttributes, Model model){

        log.info("최초가입으로 들어오는 : " + memberDTO);

        if(bindingResult.hasErrors()){
            log.info(bindingResult.getAllErrors());
            return "member/adSignup";
        }

        try{
            memberService.saveSuperAdminMember(memberDTO);

        }catch (IllegalStateException e){

            model.addAttribute("msg", e.getMessage());

            return "member/adSignup";
        }


        redirectAttributes.addFlashAttribute("memberDTO", memberDTO);
        return "member/adSignup";
    }

    @GetMapping("/cmSignup") //회원가입페이지(savChiefMember-치프가 승인해주는 치프,매니져)
    public String cmSignupGet(Model model){
        model.addAttribute("memberDTO", new MemberDTO());

        List<BrandDTO> brandDTOList =
                brandService.brandList();

        log.info("매니저 치프 가입시 가져오는 총판목록 : ");
        log.info("===" + brandDTOList);

        model.addAttribute("brandDTOList", brandDTOList);

        model.addAttribute("hotelDTO", new HotelDTO());

        List<HotelDTO> hotelDTOList =
                hotelService.hotelList();

        model.addAttribute("hotelDTOList", hotelDTOList);


        return "member/cmSignup";
    }

    @PostMapping("/cmSignup") //회원가입포스트(saveChiefMember-치프가 승인해주는 치프,매니져)
    public String cmSignupPost(@Valid MemberDTO memberDTO, BindingResult bindingResult,
                               BrandDTO brandDTO, HotelDTO hotelDTO,
                               RedirectAttributes redirectAttributes, Model model){


        log.info("회원가입으로 들어오는 : " + memberDTO);
        log.info("회원가입으로 들어오는 : " + brandDTO);
        log.info("회원가입으로 들어오는 : " + hotelDTO);

        if(bindingResult.hasErrors()){
            log.info(bindingResult.getAllErrors());

            List<BrandDTO> brandDTOList =
                    brandService.brandList();

            model.addAttribute("brandDTOList", brandDTOList);

            List<HotelDTO> hotelDTOList =
                    hotelService.hotelList();

            model.addAttribute("hotelDTOList", hotelDTOList);


            return "member/cmSignup";
        }

        if(memberDTO.getCorn().equals("C")){
            log.info("치프로 저장");

            try{
                memberService.saveChiefMember(memberDTO, brandDTO);

            }catch (IllegalStateException e){

                log.info("이메일 중복 발생 ");
                model.addAttribute("msg", e.getMessage());

                List<BrandDTO> brandDTOList =
                        brandService.brandList();

                model.addAttribute("brandDTOList", brandDTOList);

                List<HotelDTO> hotelDTOList =
                        hotelService.hotelList();

                model.addAttribute("hotelDTOList", hotelDTOList);
                return "member/cmSignup";
            }


        } else {
            log.info("매니져로 저장");

            try {
                memberService.saveManagerMember(memberDTO, brandDTO, hotelDTO);
            }catch (IllegalStateException e){
                log.info("아이디 중복 발생 ");
                model.addAttribute("msg", e.getMessage());

                List<BrandDTO> brandDTOList =
                        brandService.brandList();

                model.addAttribute("brandDTOList", brandDTOList);

                List<HotelDTO> hotelDTOList =
                        hotelService.hotelList();

                model.addAttribute("hotelDTOList", hotelDTOList);
                return "member/cmSignup";
            }

        }


        redirectAttributes.addFlashAttribute("memberDTO", memberDTO);
        return "redirect:/member/login";
    }


    @GetMapping("/signup") //회원가입페이지(유저)
    public String signupGet(Model model){
        model.addAttribute("memberDTO", new MemberDTO());
        log.info("회원가입");
        return "member/signup";
    }

   @PostMapping("/signup") //회원가입 포스트
    public String signupPost(@Valid MemberDTO memberDTO, BindingResult bindingResult,
                             RedirectAttributes redirectAttributes, Model model){

        log.info(memberDTO);

        if(bindingResult.hasErrors()){
            log.info(bindingResult.getAllErrors());
           return "member/signup";
       }

        log.info("통과");

      try{
            memberService.saveMember(memberDTO);

       }catch (IllegalStateException e){

           model.addAttribute("msg", e.getMessage());

           return "member/signup";
       }

       redirectAttributes.addFlashAttribute("memberDTO", memberDTO);
        return "member/signup";
   }

   /*@GetMapping("/adLogin") //로그인페이지(총판,매니져) 관리자들
    public String adLoginGet(MemberDTO memberDTO, Principal principal){

       if(principal != null){
           log.info("========로그인중 ==============");

       }

       return "member/adLogin";
        //return "member/adSignup";
   }*/

  /*  @PostMapping("/adLogin") //로그인
    public String adLoginPost(MemberDTO memberDTO, Principal principal){


        log.info("로그인");

        return "redirect:/adMain";

    }*/

    /*@PostMapping("/adLogout") //로그아웃
    public String adLogout(HttpSession session){

        session.invalidate();

        return "redirect:/adLogin";
    }*/

   @GetMapping("/login") //로그인페이지(유저)
    public String loginGet(MemberDTO memberDTO, Principal principal){

        if(principal != null){
            log.info("=========================");

        }

        return "member/login";
   }

/*   @PostMapping("/login") //로그인
    public String loginPost(MemberDTO memberDTO, Principal principal){


        log.info("로그인");

        return "redirect:/";
   }*/

  /* @PostMapping("/logout") //로그아웃
    public String logout(HttpSession session){

        session.invalidate();

        return "redirect:/login";
   }*/

   @GetMapping("/adFindID") // 아이디찾기(관리자)
    public String adFindIDGet(){
        return "member/adFindID";
   }

    @PostMapping("/adFindID") //아이디찾기(관리자)
    public String adFindIDPost(MemberDTO memberDTO){
        return "member/adFindID";
    }

   @GetMapping("/findID") //아이디찾기(유저)
    public String findIDGet(){
        return "member/findID";
   }

   @PostMapping("/findID") //아이디찾기(유저)
    public String findIDPost(MemberDTO memberDTO){
        return "member/findID";
   }

   @GetMapping("/adFindPW") //비밀번호찾기(관리자)
    public String adFindPWGet(){
        return "member/adFindPW";
   }

   @PostMapping("/adFindPW") //회원정보 확인(관리자)
    public String adFindPWPost(MemberDTO memberDTO){
        return "member/adFindPW";
   }

    @GetMapping("/findPW") //비밀번호찾기(유저)
    public String findPWGet(){
        return "member/findPW";
    }

    @PostMapping("/findPW") //회원정보 확인(유저)
    public String findPWPost(MemberDTO memberDTO){
        return "member/findPW";
    }

    @GetMapping("/adChangePW") //비밀번호 재설정(관리자)
    public String adChangePWGet(){
        return "member/adChangePW";
    }

    @PostMapping("/adChangePW") //비밀번호 재설정(관리자)
    public String adChangePW(MemberDTO memberDTO){
        return "member/adChangePW";
    }

    @GetMapping("/changePW") //비밀번호 재설정(관리자)
    public String changePWGet(){
        return "member/changePW";
    }

    @PostMapping("/changePW") //비밀번호 재설정(관리자)
    public String changePW(MemberDTO memberDTO){
        return "member/changePW";
    }


}
