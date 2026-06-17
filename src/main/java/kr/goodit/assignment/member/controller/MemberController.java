package kr.goodit.assignment.member.controller;

import jakarta.validation.Valid;
import kr.goodit.assignment.member.dto.MemberResponse;
import kr.goodit.assignment.member.dto.MemberSignupRequest;
import kr.goodit.assignment.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse signup(
            @Valid @RequestBody MemberSignupRequest request) {

        return memberService.signup(request);
    }

    @GetMapping
    public List<MemberResponse> getMembers() {

        return memberService.getMembers();
    }

    @GetMapping("/{memberId}")
    public MemberResponse getMember(@PathVariable Integer memberId) {

        return memberService.getMember(memberId);
    }
}
