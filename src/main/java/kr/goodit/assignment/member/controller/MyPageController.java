package kr.goodit.assignment.member.controller;

import kr.goodit.assignment.member.dto.MemberResponse;
import kr.goodit.assignment.member.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    @GetMapping
    public String index(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if(userDetails == null) return "redirect:/login";

        model.addAttribute("member", MemberResponse.from(userDetails.getMember()));
        return "mypage/index";
    }
}
