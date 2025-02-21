package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.Util.PagenationUtil;
import com.lookatme.smartstay.dto.HotelDTO;
import com.lookatme.smartstay.dto.ImageDTO;
import com.lookatme.smartstay.dto.PageRequestDTO;
import com.lookatme.smartstay.repository.ImageRepository;
import com.lookatme.smartstay.repository.MemberRepository;
import com.lookatme.smartstay.service.HotelService;
import com.lookatme.smartstay.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
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
@RequestMapping("/hotel")
public class HotelController {

    private final HotelService hotelService;
    private final ImageService imageService;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;
    private PagenationUtil pagenation;
    private final PagenationUtil pagenationUtil;

    //등록
    @GetMapping("/hotelRegister")
    public String hotelRegisterGet(Model model) {
        model.addAttribute("hotelDTO", new HotelDTO());
        return "hotel/hotelRegister";
    }
    @PostMapping("/hotelRegister")
    public String hotelRegisterPost(HotelDTO hotelDTO, RedirectAttributes redirectAttributes,
                                    @RequestParam(value = "multi", required = false)  List<MultipartFile> multi,
                                    @RequestParam(value = "mainImageIndex", required = false, defaultValue = "0") Long mainImageIndex,
                                    Principal principal) throws Exception {
        log.info("hotelRegister : " + hotelDTO);
        multi.forEach(multipartFile -> {log.info("multipartFile : " + multipartFile);});
        hotelService.insert(hotelDTO, principal.getName(), multi);
        redirectAttributes.addFlashAttribute("msg", "등록이 완료되었습니다.");
        return "redirect:/hotel/hotelList";
    }

    //목록
    @GetMapping("/hotelList") //슈퍼어드민만 사용
    public String hotelList(Principal principal, PageRequestDTO pageRequestDTO, Model model) {
        log.info("목록진입");

        List<HotelDTO> hotelDTOList =  hotelService.myHotelList(principal.getName());
        model.addAttribute("hotelDTOList", hotelDTOList);
        return "hotel/hotelList";
    }

    //상세보기
    @GetMapping("/hotelRead")
    public String hotelRead(Long hotel_num, Model model) {
        HotelDTO hotelDTO = hotelService.read(hotel_num);
        model.addAttribute("hotelDTO", hotelDTO);
        return "hotel/hotelRead";
    }

    //수정
    @GetMapping("/hotelModify")
    public String hotelModifyGet(Long hotel_num, Model model) {
        HotelDTO hotelDTO = hotelService.read(hotel_num);
        model.addAttribute("hotelDTO", hotelDTO);// end접속해제
        return "hotel/hotelModify";
    }
    @PostMapping("/hotelModify")
    public String hotelModifyPost(HotelDTO hotelDTO, BindingResult bindingResult,
                                  @RequestParam(value = "delnumList", required = false) List<Long> delnumList,
                                  @RequestParam(value = "multi", required = false) List<MultipartFile> multi,
                                  ImageDTO imageDTO, RedirectAttributes redirectAttributes) throws Exception {

        if (bindingResult.hasErrors()) {
            log.info("유효성 검사 실패:" + bindingResult.getAllErrors());
            return "hotel/hotelModify";
        }
        log.info("유효성 통과");

        if (multi != null && multi.stream().allMatch(MultipartFile::isEmpty)) {
            multi = null;
        }
        if (delnumList != null && delnumList.isEmpty()) {
            delnumList = null;
        }

        hotelService.update(hotelDTO, multi, delnumList);
        redirectAttributes.addFlashAttribute("msg", "수정 완료되었습니다.");

        log.info("수정 완료");
        return "redirect:/hotel/hotelList";
    }
    //삭제
    @PostMapping("/hotelDelete")
    public String hotelDelete(long id) {
        log.info("삭제할 번호 :"+id);
        hotelService.delete(id);
        return "redirect:/hotel/hotelList";
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
    public ResponseEntity<HotelDTO> stateUpdate(@RequestParam("hotel_num") Long hotel_num){

        HotelDTO hotelDTO = hotelService.stateUpdate(hotel_num);

        return ResponseEntity.ok(hotelDTO);
    }

           /*
                   // 기존 삭제 처리
        for (Long delNum : delnumList) {
            imageService.deleteImage(delNum);
        }
           // 새로운 이미지 저장
        if (multi != null && !multi.isEmpty()) {
            imageService.saveImage(multi, "hotel", hotelDTO.getHotel_num());
        }

        // 저장된 이미지 리스트를 다시 불러와서 DTO에 설정
        List<Image> updatedImages = imageService.findImagesByTarget("hotel", hotelDTO.getHotel_num());
        List<ImageDTO> imageDTOList = updatedImages.stream()
                .map(image -> modelMapper.map(image, ImageDTO.class))
                .collect(Collectors.toList());
        hotelDTO.setImageDTOList(imageDTOList);
*/
}
