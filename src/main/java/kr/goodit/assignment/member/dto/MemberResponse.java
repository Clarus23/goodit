package kr.goodit.assignment.member.dto;

import kr.goodit.assignment.member.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberResponse {

    private final Integer id;
    private final String username;
    private final String email;
    private final LocalDateTime createdAt;

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getUsername(), member.getEmail(), member.getCreatedAt());
    }
}
