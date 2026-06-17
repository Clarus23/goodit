package kr.goodit.assignment.member.service;

import kr.goodit.assignment.common.exception.BusinessException;
import kr.goodit.assignment.common.exception.ErrorCode;
import kr.goodit.assignment.member.domain.Member;
import kr.goodit.assignment.member.dto.MemberResponse;
import kr.goodit.assignment.member.dto.MemberSignupRequest;
import kr.goodit.assignment.member.repository.MemberRepository;
import kr.goodit.assignment.member.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService, UserDetailsService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public MemberResponse signup(MemberSignupRequest request) {
        if(memberRepository.existsByUsername(request.getUsername()))
            throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
        if(memberRepository.existsByEmail(request.getEmail()))
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);

        Member member = Member.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .build();

        return MemberResponse.from(memberRepository.save(member));
    }

    @Override
    public List<MemberResponse> getMembers() {
        return memberRepository.findAll().stream()
                .map(MemberResponse::from)
                .toList();
    }

    @Override
    public MemberResponse getMember(Integer memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        return MemberResponse.from(member);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("[" + username + "] 회원을 찾을 수 없습니다."));

        return new CustomUserDetails(member);
    }
}
