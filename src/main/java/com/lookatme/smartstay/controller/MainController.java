package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.constant.RoomState;
import com.lookatme.smartstay.dto.*;
import com.lookatme.smartstay.entity.Image;
import com.lookatme.smartstay.repository.ImageRepository;
import com.lookatme.smartstay.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final ReviewService reviewService;
    private final ImageService imageService;
    private final ImageRepository imageRepository;

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
                    log.info("CHIEF 총판 정보: {}", brandDTO);
                } else {
                    log.warn("총판 정보를 찾을 수 없습니다.");
                    model.addAttribute("brandName", "총판 없음");
                }
            } else {
                log.warn("CHIEF의 총판 정보가 설정되어 있지 않습니다.");
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

    @Secured("ROLE_SUPERADMIN")
    @PostMapping("/uploadImage")
    public String uploadImage(Model model, RedirectAttributes redirectAttributes,
                              @RequestParam("image") MultipartFile image,
                              @RequestParam(value = "mainImageIndex", required = false, defaultValue = "0") Long mainImageIndex) {
        if (image.isEmpty()) {
            redirectAttributes.addFlashAttribute("msg", "이미지를 업로드해주세요.");
            return "redirect:/";
        }
        try {
            imageService.saveBannerImage(image, mainImageIndex);
            redirectAttributes.addFlashAttribute("msg", "이미지가 성공적으로 업로드되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msg", "이미지 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/";
    }

    @Secured("ROLE_SUPERADMIN")
    @DeleteMapping("/deleteImage/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable Long imageId) {
        try {
            List<Image> bannerImages = imageService.getBannerImages();
            if (bannerImages.size() <= 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("배너 이미지는 최소 1개 이상이어야 합니다.");
            }
            imageService.deleteBannerImage(imageId);
            return ResponseEntity.ok("이미지가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            log.error("이미지 삭제 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 삭제 실패");
        }
    }

    @GetMapping("/")
    public String main(Model model) {

        List<HotelDTO> list = hotelService.activeHotelList();  // 변경된 부분

        log.info("활성화된 호텔 수: {}", list.size());

        list = list.stream()
                .filter(HotelDTO::hasAvailableRooms)
                .collect(Collectors.toList());

        log.info("사용 가능한 방이 있는 호텔 수: {}", list.size());

        list.forEach(hotelDTO -> {
            log.info("호텔 이름: {}, 방 상태: {}", hotelDTO.getHotel_name(), hotelDTO.getRooms());
        });

        List<HotelDTO> top12Hotels = list.stream()
                .sorted(Comparator.comparing(HotelDTO::getReview_count).reversed())
                .limit(12)
                .collect(Collectors.toList());

        List<Image> banner = imageRepository.findByTargetTypeOrderByOrderIndex("banner");

        if (banner == null || banner.isEmpty()) {
            log.warn("배너 이미지 데이터가 비어 있습니다.");
        } else {
            log.info("배너 이미지 리스트: {}", banner);
        }

        model.addAttribute("list", list);
        model.addAttribute("top12Hotels", top12Hotels);
        model.addAttribute("banner", banner);

        return "main";
    }

    @GetMapping("/search")
    public String search(Model model) {

        List<HotelDTO> results = hotelService.activeHotelList();

        results = results.stream().filter(HotelDTO::hasAvailableRooms).collect(Collectors.toList());

        model.addAttribute("results", results);

        return "main";
    }

    @GetMapping("/searchList")
    public String searchList(@RequestParam(value = "query", required = false) String query,
                             @RequestParam(value = "order", required = false, defaultValue = "asc") String order, Model model) {

        List<HotelDTO> results;

        if (query == null || query.trim().isEmpty()) {
            results = hotelService.activeHotelList();
            System.out.println("전체 호텔 목록: " + results.size());
        }else {
            results = hotelService.searchList(query);
            System.out.println("검색 결과: " + results.size());
        }

//        results = results.stream().filter(HotelDTO::hasAvailableRooms).collect(Collectors.toList());
        results = results.stream()
                .peek(hotel -> System.out.println("Before filter: " + hotel))
                .filter(HotelDTO::hasAvailableRooms)
                .peek(hotel -> System.out.println("After filter: " + hotel))
                .collect(Collectors.toList());
        System.out.println("가용한 방이 있는 호텔 수: " + results.size());

        if ("asc".equalsIgnoreCase(order)) {
            results.sort((h1, h2) -> Long.compare(
                    h1.getLowestPrice() != null ? h1.getLowestPrice() : Long.MAX_VALUE,
                    h2.getLowestPrice() != null ? h2.getLowestPrice() : Long.MAX_VALUE
            ));
        } else {
            results.sort((h1, h2) -> Long.compare(
                    h2.getLowestPrice() != null ? h2.getLowestPrice() : Long.MIN_VALUE,
                    h1.getLowestPrice() != null ? h1.getLowestPrice() : Long.MIN_VALUE
            ));
        }

        model.addAttribute("results", results);
        model.addAttribute("query", query);
        model.addAttribute("order", order);

        return "searchList";
    }

    @GetMapping("/searchRead")
    public String searchRead(@RequestParam Long hotel_num, @RequestParam(defaultValue = "asc") String order, Model model) {

        HotelDTO hotelDTO = hotelService.read(hotel_num);
        model.addAttribute("hotelDTO", hotelDTO);

        List<ReviewDTO> reviews = reviewService.getLimitedReviews(hotel_num, 3);
        model.addAttribute("reviews", reviews);

        List<RoomDTO> roomList = roomService.searchRead(hotel_num).stream()
                .filter(roomDTO -> roomDTO.getRoom_state() == RoomState.YES)
                .collect(Collectors.toList());

        if (order.equalsIgnoreCase("asc")) {
            roomList.sort(Comparator.comparing(RoomDTO::getRoom_price));
        }else if (order.equalsIgnoreCase("desc")) {
            roomList.sort(Comparator.comparing(RoomDTO::getRoom_price).reversed());
        }

        for (RoomDTO room : roomList) {
            log.info("룸 번호:{}", room.getRoom_num());
        }

        model.addAttribute("roomList", roomList);
        model.addAttribute("hotel_num", hotel_num);
        model.addAttribute("order", order);

        return "searchRead";
    }

    @GetMapping("/searchRoomRead")
    public String searchRoomRead(@RequestParam Long room_num, Model model) {

        RoomDTO roomDTO = roomService.roomRead(room_num);

        if (roomDTO != null && roomDTO.getRoom_state() == RoomState.YES) {
            model.addAttribute("roomDTO", roomDTO);
        }

        List<ReviewDTO> reviews = reviewService.getLimitedRoomReviews(room_num, 3);
        model.addAttribute("reviews", reviews);

        return "searchRoomRead";
    }

    //활성화된 호텔만 보이기
    @GetMapping("/activeHotel")
    @ResponseBody
    public ResponseEntity<List<HotelDTO>> getActiveHotel() {
        List<HotelDTO> activeHotel = hotelService.activeHotelList();

        activeHotel = activeHotel.stream().filter(HotelDTO::hasAvailableRooms).collect(Collectors.toList());

        return ResponseEntity.ok(activeHotel);
    }
}
