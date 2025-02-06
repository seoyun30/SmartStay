package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.dto.RoomItemDTO;
import com.lookatme.smartstay.service.CartService;
import com.lookatme.smartstay.service.RoomItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final RoomItemService roomItemService;

    //룸예약 장바구니 등록
    @PostMapping("/cartRoomReserveRegister")
    public ResponseEntity cartRoomReserveRegister(@Valid RoomItemDTO roomItemDTO, BindingResult bindingResult,
                                                  Principal principal) {

        log.info("브라우저 roomItemDTO: " + roomItemDTO);
        log.info("로그인 principal: " + principal);

        if (bindingResult.hasErrors()) {

            StringBuffer sb = new StringBuffer();
            List<FieldError> fieldErrorList
                    = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrorList) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);

        }

        String email = principal.getName();

        try {
            Long roomitem_num = cartService.addCartRoomItem(roomItemDTO, email);

            return new ResponseEntity<Long>(roomitem_num, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    //장바구니 전체 목록
    @GetMapping("/cartList")
    public String cartList(Principal principal, Model model) {

        //룸 예약 장바구니 목록
        List<RoomItemDTO> roomItemDTOList = cartService.getCartRoomItemList(principal.getName());

        roomItemDTOList.forEach(roomItemDTO -> log.info("roomItemDTO: " + roomItemDTO));

        model.addAttribute("roomItemDTOList", roomItemDTOList);

        //차후 서비스 예약 장바구니 목록 추가

        return "cart/cartList";
    }

    //룸예약 장바구니 수정
    @PostMapping("/cartRoomReserveModify")
    public ResponseEntity cartRoomReserveModify (@Valid RoomItemDTO roomItemDTO, BindingResult bindingResult, Principal principal) {

        String email = principal.getName();

        log.info("장바구니 수정 roomItemDTO: " + roomItemDTO);

        if (bindingResult.hasErrors()) {
            StringBuffer sb = new StringBuffer();
            List<FieldError> fieldErrorList
                    = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrorList) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        //수정 가능 사항: 예약인원수, 예약 날짜, 예약 요청사항
        try {
            roomItemService.updateRoomItem(roomItemDTO, email);
        } catch (Exception e) {
            return new ResponseEntity<String>("룸예약 수정에 실패했습니다. 고객센터에 문의해주십시오.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(roomItemDTO.getRoomitem_num(), HttpStatus.OK);
    }

    //룸예약 장바구니 삭제
    @DeleteMapping("/cartRoomReserveDelete/{roomitem_num}")
    public ResponseEntity cartRoomReserveDelete (@PathVariable("roomitem_num") Long roomitem_num,
                                                 Principal principal) {

        if (!cartService.validateCartRoomItem(roomitem_num, principal.getName())) {
            return new ResponseEntity<String>("수정권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.deleteCartRoomItem(roomitem_num);

        return new ResponseEntity<Long>(roomitem_num, HttpStatus.OK);
    }


}
