package kr.goodit.assignment.member.controller;

import kr.goodit.assignment.member.dto.MemberResponse;
import kr.goodit.assignment.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {
    private final MemberService memberService;

    @GetMapping
    public String index(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        MemberResponse member = memberService.findByUsername(userDetails.getUsername());
        model.addAttribute("member", member);
        return "mypage/index";
    }
}
