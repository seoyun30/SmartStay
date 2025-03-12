package com.lookatme.smartstay.controller;

import com.lookatme.smartstay.entity.Member;
import com.lookatme.smartstay.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private MemberRepository memberRepository;

    @ModelAttribute("superAdmin")
    public String superAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            if ("SUPERADMIN".equals(role)) {
                return "SUPERADMIN";
            }
        }
        return null;
    }

    @ModelAttribute("adminRole")
    public String adminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().iterator().next().getAuthority();
        }
        return "";
    }

    @ModelAttribute("adminEmail")
    public String adminEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "";
    }

    @ModelAttribute("brandName")
    public String brandName() {
        String email = adminEmail();
        Member member = memberRepository.findByEmail(email);
        if (member != null && member.getBrand() != null) {
            return member.getBrand().getBrand_name();
        }
        return "";
    }

    @ModelAttribute("hotelName")
    public String hotelName() {
        String email = adminEmail();
        Member member = memberRepository.findByEmail(email);
        if (member != null && member.getHotel() != null) {
            return member.getHotel().getHotel_name();
        }
        return "";
    }

    @ModelAttribute("brandNum")
    public Long brandNum() {
        String email = adminEmail();
        if (email == null || email.isEmpty()) {
            return null;
        }
        Member member = memberRepository.findByEmail(email);
        if (member != null && member.getBrand() != null) {
            return member.getBrand().getBrand_num();
        }
        return null;
    }

    @ModelAttribute("hotelNum")
    public Long hotelNum() {
        String email = adminEmail();
        if (email == null || email.isEmpty()) {
            return null;
        }
        Member member = memberRepository.findByEmail(email);
        if (member != null && member.getHotel() != null) {
            return member.getHotel().getHotel_num();
        }
        return null;
    }
}
