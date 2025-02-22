package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.BrandDTO;
import com.lookatme.smartstay.dto.HotelDTO;
import com.lookatme.smartstay.dto.MemberDTO;
import com.lookatme.smartstay.dto.RoomDTO;
import com.lookatme.smartstay.service.BrandService;
import com.lookatme.smartstay.service.HotelService;
import com.lookatme.smartstay.service.MemberService;
import com.lookatme.smartstay.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MainController {

    private final HotelService hotelService;
    private final MemberService memberService;
    private final BrandService brandService;
    private final RoomService roomService;

    @GetMapping("/adMain")
    public String adMain(Model model, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return "redirect:/member/login";
        }

        String email = authentication.getName();
        MemberDTO memberDTO = memberService.findbyEmail(email);
        log.info(memberDTO);

        if (memberDTO == null) {
            log.error("회원 정보가 없습니다.");
            return "redirect:/member/login";
        }
        log.info("user:{}", memberDTO);

        model.addAttribute("adminEmail", memberDTO.getEmail());
        model.addAttribute("adminRole", memberDTO.getRole() != null ? memberDTO.getRole().name() : "권한 없음");

        if ("SUPERADMIN".equals(memberDTO.getRole().name())) {
            model.addAttribute("SUPERADMIN", "super admin 입니다.");
            log.info("SUPERADMIN 로그인: {}", email);

        } else if ("CHIEF".equals(memberDTO.getRole().name())) {
            if (memberDTO.getBrandDTO() != null && memberDTO.getBrandDTO().getBrand_num() != null) {
                BrandDTO brandDTO = brandService.read(memberDTO.getBrandDTO().getBrand_num());
                if (brandDTO != null) {
                    model.addAttribute("brandName", brandDTO.getBrand_name());
                    model.addAttribute("brandDTO", brandDTO);
                    log.info("CHIEF 브랜드 정보: {}", brandDTO);
                } else {
                    log.warn("브랜드 정보를 찾을 수 없습니다.");
                    model.addAttribute("brandName", "브랜드 없음");
                }
            } else {
                log.warn("CHIEF의 브랜드 정보가 설정되어 있지 않습니다.");
                model.addAttribute("brandName", "");
            }

        } else if ("MANAGER".equals(memberDTO.getRole().name())) {
            HotelDTO hotelDTO = hotelService.myHotel(memberDTO.getEmail());
            if (hotelDTO != null) {
                model.addAttribute("hotelName", hotelDTO.getHotel_name());
                model.addAttribute("hotelDTO", hotelDTO);
                log.info("MANAGER 호텔 정보: {}", hotelDTO);
            } else {
                log.warn("호텔 정보를 찾을 수 없습니다.");
                model.addAttribute("hotelName", "호텔 없음");
            }
        } else {
            log.warn("알 수 없는 ROLE: {}", memberDTO.getRole());
            model.addAttribute("adminRole", "권한 없음");
        }

        log.info("User authorities in controller: {}", authentication.getAuthorities());

        return "adMain";
    }

    @GetMapping("/")
    public String main(Model model) {

        List<HotelDTO> list = hotelService.activeHotelList();  // 변경된 부분


        List<HotelDTO> top12Hotels = list.stream().limit(12).collect(Collectors.toList());

        model.addAttribute("list", top12Hotels);

        return "main";
    }

    @GetMapping("/search")
    public String search(Model model) {

        List<HotelDTO> results = hotelService.hotelList();
        model.addAttribute("results", results);

        return "main";
    }

    @GetMapping("/searchList")
    public String searchList(@RequestParam(value = "query", required = false) String query, Model model) {

        List<HotelDTO> results;

        if (query == null || query.trim().isEmpty()) {
            results = List.of();
            model.addAttribute("message","검색어를 입력하세요.");
        }else {
            results = hotelService.searchList(query);
        }

        model.addAttribute("results", results);
        model.addAttribute("query", query);

        return "searchList";
    }

    @GetMapping("/searchRead")
    public String searchRead(@RequestParam Long hotel_num, @RequestParam(defaultValue = "asc") String order, Model model) {

        HotelDTO hotelDTO = hotelService.read(hotel_num);
        model.addAttribute("hotelDTO", hotelDTO);

        List<RoomDTO> roomList = roomService.searchRead(hotel_num);

        if (order.equalsIgnoreCase("asc")) {
            roomList.sort(Comparator.comparing(RoomDTO::getRoom_price));
        }else if (order.equalsIgnoreCase("desc")) {
            roomList.sort(Comparator.comparing(RoomDTO::getRoom_price).reversed());
        }

        for (RoomDTO room : roomList) {
            log.info("룸 번호:{}", room.getRoom_num());
            log.info("대표 이미지 URL:{}", room.getMainImage() != null ? room.getMainImage().getImage_url() : "없음");
        }

        model.addAttribute("roomList", roomList);
        model.addAttribute("hotel_num", hotel_num);
        model.addAttribute("order", order);

        return "searchRead";
    }

    @GetMapping("/searchRoomRead")
    public String searchRoomRead(@RequestParam Long room_num, Model model) {

        RoomDTO roomDTO = roomService.roomRead(room_num);
        model.addAttribute("roomDTO", roomDTO);

        return "searchRoomRead";
    }

    //활성화된 호텔만 보이기
    @GetMapping("/activeHotel")
    @ResponseBody
    public ResponseEntity<List<HotelDTO>> getActiveHotel() {
        List<HotelDTO> activeHotel = hotelService.activeHotelList();
        return ResponseEntity.ok(activeHotel);
    }
}
