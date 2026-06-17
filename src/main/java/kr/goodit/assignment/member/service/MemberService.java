package kr.goodit.assignment.member.service;

import kr.goodit.assignment.member.dto.MemberResponse;
import kr.goodit.assignment.member.dto.MemberSignupRequest;

import java.util.List;

public interface MemberService {
    // 회원가입
    public MemberResponse signup(MemberSignupRequest request);

    // 전체 회원 조회
    public List<MemberResponse> getMembers();

    // 단일 회원 조회
    public MemberResponse getMember(Integer memberId);
}
