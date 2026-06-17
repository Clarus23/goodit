package kr.goodit.assignment.member.service;

import kr.goodit.assignment.member.dto.MemberResponse;
import kr.goodit.assignment.member.dto.MemberSignupRequest;

import java.util.List;

public interface MemberService {
    // 회원가입
    MemberResponse signup(MemberSignupRequest request);

    // 전체 회원 조회
    List<MemberResponse> getMembers();

    // 단일 회원 조회
    MemberResponse getMember(Integer memberId);
}
