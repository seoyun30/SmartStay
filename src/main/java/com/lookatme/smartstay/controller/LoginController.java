package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.constant.Role;
import com.lookatme.smartstay.dto.BrandDTO;
import com.lookatme.smartstay.dto.HotelDTO;
import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.dto.NewPasswordDTO;
import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.service.BrandService;
import com.lookatme.smartstay.service.HotelService;
import com.lookatme.smartstay.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

            if (e.getMessage().contains("연락처 중복")) { // 예외 메시지 확인 후 처리
                bindingResult.rejectValue("phone", "error.memberDTO", "이미 사용중인 연락처입니다.");
                return "member/adSignup"; // 다시 회원가입 폼으로 이동
            }

            model.addAttribute("msg", e.getMessage());

            return "member/adSignup";
        }


        redirectAttributes.addFlashAttribute("adSignupMsg", "회원가입 승인요청되었습니다.");

        redirectAttributes.addFlashAttribute("memberDTO", memberDTO);

        return "redirect:/member/adSignup";
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

        redirectAttributes.addFlashAttribute("cmSignupMsg", "회원가입 승인요청되었습니다.");
        log.info("Redirected cmSignupMsg: " + redirectAttributes.getFlashAttributes().get("cmSignupMsg"));

        redirectAttributes.addFlashAttribute("memberDTO", memberDTO);
        return "redirect:/member/cmSignup";
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
        return "member/login";
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

   @GetMapping("/login") //로그인페이지
    public String loginGet(MemberDTO memberDTO, Principal principal){

        if(principal != null){
            log.info("=========================");

        }

        return "member/login";
   }

    @GetMapping("/loginPW") //로그인페이지(임시비밀번호 발급시)
    public String loginPW(MemberDTO memberDTO, Principal principal){

        if(principal != null){
            log.info("=========================");
            MemberDTO dto =
            memberService.findbyEmail(principal.getName());
            if(dto.getRole().name().equals("CHIEF")  || dto.getRole().name().equals("MANAGER") || dto.getRole().name().equals("SUPERADMIN")){
                return "redirect:/adMain";
            }else {
                return "redirect:/";
            }


        }

        return "member/loginPW";
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


    @GetMapping("/changePW") // 임시비밀번호 변경
    public String changePWGet(Principal principal, Model model , NewPasswordDTO newPasswordDTO){



        if(principal == null){
            return "redirect:/member/loginPW";
        }

        return "member/changePW";
    }



    @PostMapping("/changePW") // 마이페이지 정보수정
    public String changePWPost(@Valid NewPasswordDTO newPasswordDTO, BindingResult bindingResult, Model model, Principal principal) {


        log.info("정보업데이트" + newPasswordDTO);
        log.info("정보업데이트" + newPasswordDTO.getPassword().length());

        if(bindingResult.hasErrors()){
            log.info(bindingResult.getAllErrors());

            return "member/changePW";
        }

        MemberDTO memberDTO =
        memberService.findbyEmail(principal.getName());

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (!passwordEncoder.matches(newPasswordDTO.getPassword(), memberDTO.getPassword()    )) {
            log.info("기존 비밀번호가 일치하지 않음");
            model.addAttribute("msg", "기존 비밀번호가 올바르지 않습니다.");
            return "member/changePW";
        }

        log.info("비밀번호 확인은 했음");
        // 새 비밀번호 유효성 검사
//        if (memberDTO.getPassword().length() < 8 || memberDTO.getPassword().length() > 20) {
//            model.addAttribute("msg", "비밀번호는 8 ~ 20 글자로 입력해주세요.");
//            model.addAttribute("memberDTO", memberDTO);
//            return "member/changePW";
//        }
//
//        if (!memberDTO.getPassword().equals(memberDTO.getRepassword())) {
//            model.addAttribute("msg", "비밀번호와 비밀번호 재확인이 일치하지 않습니다.");
//            model.addAttribute("memberDTO", memberDTO);
//            return "member/changePW"; //
//        }

            // 비밀번호 업데이트
            try {
                memberDTO.setPassword(newPasswordDTO.getNewPassword());
                memberService.updateMember(memberDTO);
            } catch (Exception e) {
                model.addAttribute("msg", "비밀번호 변경 중 오류가 발생했습니다.");
                return "member/changePW";
            }

            String redirectUrl = "/"; // 기본값 (USER는 메인 페이지로 이동)
            if (memberDTO.getRole() == Role.SUPERADMIN || memberDTO.getRole() == Role.CHIEF || memberDTO.getRole() == Role.MANAGER) {
                redirectUrl = "/adMain"; // 관리자 계열은 /adMain으로 이동
            }

            return "redirect:" + redirectUrl;

    }


   @GetMapping("/findID") //아이디찾기(유저)
    public String findIDGet(){
        return "member/findID";
   }

   @PostMapping("/findID") //아이디찾기(유저)
    public String findIDPost(String name, String tel, Model model){
       try {
           Member member = memberService.findID(name, tel);
           MemberDTO memberDTO = new MemberDTO();
           memberDTO.setEmail(member.getEmail());
           log.info("이메일 찾았니?" + memberDTO);

           model.addAttribute("memberDTO", memberDTO);

       }catch (EntityNotFoundException e) {
           model.addAttribute("msg", e.getMessage());
           model.addAttribute("memberDTO", new MemberDTO());
       }
        return "member/findID";
   }



    @GetMapping("/findPW") //비밀번호찾기(유저)
    public String findPWGet(){
        return "member/findPW";
    }

    @PostMapping("/findPW") //회원정보 확인(유저)
    public String findPWPost(@ModelAttribute MemberDTO memberDTO, Model model){
       try{
           memberService.passwordSend(memberDTO);
           model.addAttribute("successMsg", "임시비밀번호가 전송되었습니다.");
           return "member/findPW";
       }catch (IllegalStateException e){
           model.addAttribute("msg", e.getMessage());
           return "member/findPW";
       }catch (Exception e){
           model.addAttribute("msg", "예기치 않은 오류가 발생했습니다.");
           return "member/findPW";
       }
    }


}
