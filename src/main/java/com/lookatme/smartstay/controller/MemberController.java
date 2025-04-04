package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.repository.HotelRepository;
import com.lookatme.smartstay.repository.MemberRepository;
import com.lookatme.smartstay.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final RoomReserveService roomReserveService;
    private final OrderReserveService orderReserveService;
    private final ReviewService reviewService;
    private final HotelService hotelService;

    @GetMapping("/adMypage") // 마이페이지 정보보기(관리자)
    public String adMypage(Principal principal, Model model, Authentication authentication){

        String email = authentication.getName();
        MemberDTO memberDTO = memberService.readMember(email);
        log.info(memberDTO);
        model.addAttribute("memberDTO", memberDTO);

        return "member/adMypage";
    }


    @GetMapping("/adMypagePasswordCheck")
    public String adMypagePasswordCheck(MemberDTO memberDTO, Principal principal, RedirectAttributes RedirectAttributes){

        if(principal == null){
            RedirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";

        }
        return "member/adMypagePasswordCheck";
    }

    @PostMapping("/adMypagePasswordCheck")
    public String adMypagePasswordCheckPost(@RequestParam("password") String password, Principal principal, RedirectAttributes redirectAttributes) {

        if(principal == null){
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        String email = principal.getName();

        if(memberService.checkPassword(email, password)) {
            return "redirect:/member/adMypageModify";
        }else {
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/member/adMypagePasswordCheck";
        }

    }

    @GetMapping("/adMypageModify") // 마이페이지 정보수정(관리자)
    public String adMypageModifyGet(Principal principal, HttpSession session, Model model){

        MemberDTO memberDTO = memberService.findbyEmail(principal.getName());

        log.info("memberDTO" + memberDTO);

        if(memberDTO == null){
            return "redirect:/member/adMypage";
        }

        model.addAttribute("memberDTO", memberDTO);

        return "member/adMypageModify";
    }

    @PostMapping("/adMypageModify") // 마이페이지 정보수정
    public String adMypageModifyPost(MemberDTO memberDTO, Model model){

        log.info("정보업데이트" + memberDTO);
        log.info("정보업데이트" + memberDTO.getPassword().length());

        if(memberDTO.getPassword().length() >= 1){
            log.info("비밀번호 유효성검사");

            if(memberDTO.getPassword().length() < 8  || memberDTO.getPassword().length() > 20){

                log.info("비밀번호 유효성검사");

                String  msg = "비밀번호는 8 ~ 20 글자로 입력해주세요.";


                memberDTO = memberService.findbyEmail(memberDTO.getEmail());

                log.info("memberDTO" + memberDTO);

                if(memberDTO == null){
                    return "redirect:/member/adMypage";
                }


                model.addAttribute("memberDTO", memberDTO);
                model.addAttribute("msg", msg);

                return "member/adMypageModify";
            }

        }

        try {
            memberService.updateMember(memberDTO);

        }catch (Exception e){

            return "redirect:/member/adMypage";
        }

        return "redirect:/member/adMypage";

    }

    @GetMapping("/userAllMyPage") //user의 첫 마이페이지
    public String userAllMyPage(Model model, Principal principal){

        String email = principal.getName();

        MemberDTO memberDTO = memberService.findbyEmail(email);
        model.addAttribute("memberDTO", memberDTO);


        //내 룸예약 조회
        PageRequestDTO pageRequestDTO = new PageRequestDTO();
        pageRequestDTO.setSize(4);
        PageResponseDTO<RoomReserveItemDTO> reserveItemDTOPageResponseDTO = roomReserveService.findMyRoomReservePage(principal.getName(), pageRequestDTO);
        model.addAttribute("reserveItemDTOPageResponseDTO", reserveItemDTOPageResponseDTO);

        PageResponseDTO<OrderReserveItemDTO> orderReserveItemDTOPageResponseDTO = orderReserveService.findMyOrderPage(principal.getName(), pageRequestDTO);
        model.addAttribute("orderReserveItemDTOPageResponseDTO", orderReserveItemDTOPageResponseDTO);

        return "member/userAllMyPage";
    }

    @GetMapping("/mypage") // 마이페이지 정보보기(유저)
    public String mypage(Model model, Authentication authentication) {

        String email = authentication.getName();
        MemberDTO memberDTO = memberService.readMember(email);
        log.info(memberDTO);
        model.addAttribute("memberDTO", memberDTO);


        return "member/mypage";

    }

    @GetMapping("/mypagePasswordCheck")
    public String mypagePasswordCheck(MemberDTO memberDTO, Principal principal, RedirectAttributes RedirectAttributes){

        if(principal == null){
            RedirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";

        }
        return "member/mypagePasswordCheck";
    }

    @PostMapping("/mypagePasswordCheck")
    public String mypagePasswordCheckPost(@RequestParam("password") String password, Principal principal, RedirectAttributes redirectAttributes) {

        if(principal == null){
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        String email = principal.getName();

        if(memberService.checkPassword(email, password)) {
            return "redirect:/member/mypageModify";
        }else {
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/member/mypagePasswordCheck";
        }

    }

    @GetMapping("/mypageModify") // 마이페이지 정보수정(유저)
    public String mypageModifyGet(Model model, Principal principal){

        MemberDTO memberDTO = memberService.findbyEmail(principal.getName());

        log.info("memberDTO" + memberDTO);

        if(memberDTO == null){
            return "redirect:/member/mypage";
        }

        model.addAttribute("memberDTO", memberDTO);

        return "member/mypageModify";
    }

    @PostMapping("/mypageModify") // 마이페이지 정보수정
    public String mypageModifyPost(MemberDTO memberDTO, Model model){

        log.info("정보업데이트" + memberDTO);
        log.info("정보업데이트" + memberDTO.getPassword().length());

        if(memberDTO.getPassword().length() >= 1){
            log.info("비밀번호 유효성검사");

            if(memberDTO.getPassword().length() < 8  || memberDTO.getPassword().length() > 20){
                //리다이렉트

                log.info("비밀번호 유효성검사");

                String  msg = "비밀번호는 8 ~ 20 글자로 입력해주세요.";


                memberDTO = memberService.findbyEmail(memberDTO.getEmail());

                log.info("memberDTO" + memberDTO);

                if(memberDTO == null){
                    return "redirect:/member/mypage";
                }


                model.addAttribute("memberDTO", memberDTO);
                model.addAttribute("msg", msg);

                return "member/mypageModify";
            }
        }

        try {
            memberService.updateMember(memberDTO);

        }catch (Exception e){

            return "redirect:/member/mypage";
        }

        return "redirect:/member/mypage";

    }

    @GetMapping("/memberList") //전체 회원 목록
    public String memberList(Principal principal, PageRequestDTO pageRequestDTO,
                             @RequestParam(value = "sort", required = false, defaultValue = "DESC") String sortOrder,
                             @RequestParam(value = "orderType", required = false, defaultValue = "member_num") String orderType,
                             @RequestParam(value="type", defaultValue = "") String type,
                             Model model){

        log.info("진입");
        log.info("정렬 기준: " + sortOrder);
        log.info("정렬 기준: " + orderType);

        if (orderType == null || orderType.isEmpty()) {
            orderType = "member_num";  // 기본 정렬 기준
        }
        if (sortOrder == null || sortOrder.isEmpty()) {
            sortOrder = "DESC";  // 기본 정렬 방향
        }

        PageResponseDTO<MemberDTO> pageResponseDTO = memberService.memberList(pageRequestDTO, sortOrder, orderType, type);

        model.addAttribute("pageResponseDTO", pageResponseDTO);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("orderType", orderType);
        model.addAttribute("type", type);
        model.addAttribute("pageRequestDTO", pageRequestDTO);

        return "member/memberList";
    }

    @GetMapping("/adPowerList") // 권한승인(총판)
    public String adPowerList(Principal principal, PageRequestDTO pageRequestDTO, String email,
                              @RequestParam(value="type", defaultValue = "") String type, Model model) {

        PageResponseDTO<MemberDTO> pageResponseDTO = memberService.adPowerList(pageRequestDTO, email, type);

        log.info("전달되는 pageResponseDTO" + pageResponseDTO);
        log.info("전달되는 pageResponseDTO DTO리스트" + pageResponseDTO.getDtoList());

        model.addAttribute("pageResponseDTO", pageResponseDTO);



        return "member/adPowerList";

    }

    @PostMapping("/powerAdmit")
    @ResponseBody
    public ResponseEntity<MemberDTO> powerAdmitPost(@RequestParam("email") String email){

        MemberDTO memberDTO = memberService.powerAdmit(email);

        return ResponseEntity.ok(memberDTO);
    }

    @PostMapping("/adPowerMember") //권한 승인
    public String adPowerMember(@RequestParam("email") PageRequestDTO pageRequestDTO, String email, String type, Model model){

        try {
            List<MemberDTO> adPowerList = memberService.adPowerList(pageRequestDTO, email, type).getDtoList();
            model.addAttribute("adPowerList", adPowerList);
            model.addAttribute("message", "변경완료");

        } catch (Exception e) {
            model.addAttribute("message", "변경오류");
        }

        return "redirect:/member/adPowerList";

    }

    @GetMapping("/cmPowerList") // 권한승인(치프, 매니져)
    public String cmPowerList(Principal principal, PageRequestDTO pageRequestDTO,
                              @RequestParam(value="type", defaultValue = "") String type, Model model) {

        log.info(principal);
        log.info(principal.getName());
        String email = principal.getName();

        PageResponseDTO<MemberDTO> pageResponseDTO = memberService.cmPowerList(pageRequestDTO, email, type);
        model.addAttribute("pageResponseDTO", pageResponseDTO);


        return "member/cmPowerList";
    }

    @PostMapping("/changePower") // 권한승인(치프, 매니져)
    public ResponseEntity<?> changePower(String role, Long hotel_num, MemberDTO memberDTO, Principal principal) {

        log.info("권한변경 요청: ", principal.getName());
        log.info("role: " + role);
        log.info("hotel_num: " + hotel_num);
        log.info("memberDTO: " + memberDTO);

        if(role.equals("MANAGER") && hotel_num == null) {
            return ResponseEntity.badRequest().body("MANAGER는 호텔번호가 필요함");
        }
        memberService.changePower(role, memberDTO, hotel_num);


        return ResponseEntity.ok().build();
    }

    @GetMapping("/hotelsByBrand") // 브랜드에 속한 호텔 목록 반환
    public ResponseEntity<List<HotelDTO>> getHotelsByBrand( Principal principal) {

        List<HotelDTO> hotelDTOs = hotelService.myHotelList(principal.getName());
        return ResponseEntity.ok(hotelDTOs);
    }

    @PostMapping("/powerMember") //권한 승인
    public String powerMember(@RequestParam("email") String email, Model model){
//
//
//        try {
//            memberService.powerMember(email);
//            List<MemberDTO> cmPowerList = memberService.cmPowerList(email);
//            model.addAttribute("cmPowerList", cmPowerList);
//            model.addAttribute("message", "변경완료");
//
//        } catch (Exception e) {
//            model.addAttribute("message", "변경오류");
//        }
        return "redirect:/member/cmPowerList";

    }

    //회원용 룸 예약 목록
    @GetMapping("/myRoomReserveList")
    public String myRoomReserveList(PageRequestDTO pageRequestDTO, Principal principal,ReserveSearchDTO reserveSearchDTO, Model model){
        //내 룸예약 조회
        PageResponseDTO<RoomReserveItemDTO> pageResponseDTO = roomReserveService.findMyRoomReservePageSearch(principal.getName(), pageRequestDTO, reserveSearchDTO);
        model.addAttribute("pageResponseDTO", pageResponseDTO);
        model.addAttribute("reserveSearchDTO", reserveSearchDTO);

        return "member/myRoomReserveList";
    }

    @GetMapping("/myRoomReserveRead")
    public String myRoomReserveRead(Long roomreserveitem_num, Principal principal, Model model){
        //내 룸예약 조회
        RoomReserveItemDTO roomReserveItemDTO = roomReserveService.findRoomReserveItem(roomreserveitem_num, principal.getName());
        model.addAttribute("roomReserveItemDTO", roomReserveItemDTO);

        boolean check = reviewService.validateReview(roomReserveItemDTO.getRoomReserveDTO().getReserve_num(), principal.getName());
        model.addAttribute("check", check);

        return "member/myRoomReserveRead";
    }

    @GetMapping("/myOrderReserveList")
    public String myOrderReserveList(PageRequestDTO pageRequestDTO, Principal principal, ReserveSearchDTO reserveSearchDTO, Model model){
        //내 룸서비스 주문 조회
        PageResponseDTO<OrderReserveItemDTO> pageResponseDTO = orderReserveService.findMyOrderPageSearch(principal.getName(), pageRequestDTO, reserveSearchDTO);
        model.addAttribute("pageResponseDTO", pageResponseDTO);
        model.addAttribute("reserveSearchDTO", reserveSearchDTO);

        return "member/myOrderReserveList";
    }

    @GetMapping("/myOrderReserveRead")
    public String myOrderReserveRead(Long serviceitem_num, Principal principal, Model model){
        //내 룸서비스 상세조회
        OrderReserveItemDTO orderReserveItemDTO = orderReserveService.findOrderReserveItem(serviceitem_num, principal.getName());
        model.addAttribute("orderReserveItemDTO", orderReserveItemDTO);

        return "member/myOrderReserveRead";
    }

}
