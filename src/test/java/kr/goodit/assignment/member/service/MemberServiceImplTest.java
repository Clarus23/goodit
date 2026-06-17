package kr.goodit.assignment.member.service;

import kr.goodit.assignment.common.exception.BusinessException;
import kr.goodit.assignment.common.exception.ErrorCode;
import kr.goodit.assignment.member.domain.Member;
import kr.goodit.assignment.member.dto.MemberResponse;
import kr.goodit.assignment.member.dto.MemberSignupRequest;
import kr.goodit.assignment.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock private MemberRepository memberRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private MemberServiceImpl memberService;

    // helper method
    private MemberSignupRequest createRequest(String username, String password, String email) {
        MemberSignupRequest request = new MemberSignupRequest();

        ReflectionTestUtils.setField(request, "username", username);
        ReflectionTestUtils.setField(request, "password", password);
        ReflectionTestUtils.setField(request, "email", email);

        return request;
    }

    private Member createMember(Integer memberId, String username, String email) {
        Member member = Member.builder()
                .username(username)
                .password("encode_password")
                .email(email)
                .build();

        ReflectionTestUtils.setField(member, "id", memberId);

        return member;
    }

    @Test
    @DisplayName("회원 가입 성공")
    void signup_success() {
        // given
        MemberSignupRequest request = createRequest("testuser", "password123", "test@test.com");
        Member savedMember = createMember(1, "testuser", "test@test.com");

        given(memberRepository.existsByUsername("testuser")).willReturn(false);
        given(memberRepository.existsByEmail("test@test.com")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("encode_password");
        given(memberRepository.save(any(Member.class))).willReturn(savedMember);

        // when
        MemberResponse response = memberService.signup(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@test.com");
        verify(passwordEncoder).encode("password123");
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("회원 가입 실패 - email 중복 시 DUPLICATE_EMAIL 예외")
    void signup_duplicateEmail() {
        // given
        MemberSignupRequest request = createRequest("testuser", "password123", "test@test.com");

        given(memberRepository.existsByUsername("testuser")).willReturn(false);
        given(memberRepository.existsByEmail("test@test.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.signup(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(e ->
                        assertThat(((BusinessException) e).getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_EMAIL)
                );

        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("전체 회원 조회 - 저장된 회원 수만큼 반환")
    void getMembers_success() {
        // given
        List<Member> members = List.of(
                createMember(1, "user1", "user1@test.com"),
                createMember(2, "user2", "user2@test.com")
        );
        given(memberRepository.findAll()).willReturn(members);

        // when
        List<MemberResponse> responses = memberService.getMembers();

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getUsername()).isEqualTo("user1");
        assertThat(responses.get(1).getUsername()).isEqualTo("user2");
    }

    @Test
    @DisplayName("전체 회원 조회 - 회원이 없으면 빈 리스트 반환")
    void getMembers_empty() {
        // given
        given(memberRepository.findAll()).willReturn(List.of());

        // when
        List<MemberResponse> responses = memberService.getMembers();

        // then
        assertThat(responses).isEmpty();
    }

    // --- getMember() ---

    @Test
    @DisplayName("단건 회원 조회 성공 - id에 해당하는 MemberResponse 반환")
    void getMember_success() {
        // given
        Member member = createMember(1, "testuser", "test@test.com");
        given(memberRepository.findById(1)).willReturn(Optional.of(member));

        // when
        MemberResponse response = memberService.getMember(1);

        // then
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("단건 회원 조회 실패 - 존재하지 않는 id 조회 시 NOT_FOUND_MEMBER 예외")
    void getMember_notFound() {
        // given
        given(memberRepository.findById(999)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.getMember(999))
                .isInstanceOf(BusinessException.class)
                .satisfies(e ->
                        assertThat(((BusinessException) e).getErrorCode())
                                .isEqualTo(ErrorCode.NOT_FOUND_MEMBER)
                );
    }
}
