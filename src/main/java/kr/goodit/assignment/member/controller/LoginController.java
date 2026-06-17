package kr.goodit.assignment.member.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(HttpSession session, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if(userDetails != null) return "redirect:/mypage";

        String loginError = (String) session.getAttribute("loginError");
        String logoutMessage = (String) session.getAttribute("logoutMessage");

        if(loginError != null) {
            model.addAttribute("errorMessage", loginError);
            session.removeAttribute("loginError");
        }
        if(logoutMessage != null) {
            model.addAttribute("logoutMessage", logoutMessage);
            session.removeAttribute("logoutMessage");
        }

        return "member/login";
    }
}
