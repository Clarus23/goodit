package kr.goodit.assignment.member.controller;

import kr.goodit.assignment.common.exception.BusinessException;
import kr.goodit.assignment.common.exception.ErrorCode;
import kr.goodit.assignment.member.domain.Member;
import kr.goodit.assignment.member.dto.MemberResponse;
import kr.goodit.assignment.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MyPageControllerTest {

    @InjectMocks
    private MyPageController myPageController;

    @Mock
    private MemberService memberService;

    private Member createMember(Integer id, String username, String email) {
        Member member = Member.builder()
                .username(username)
                .password("encoded_password")
                .email(email)
                .build();
        ReflectionTestUtils.setField(member, "id", id);
        ReflectionTestUtils.setField(member, "createdAt", LocalDateTime.of(2026, 1, 1, 0, 0));
        return member;
    }

    @Test
    @DisplayName("인증된 사용자가 마이페이지 접근 시 회원 정보를 모델에 담아 반환한다")
    void 마이페이지_정상_조회() {
        // given
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("encoded_password")
                .roles("USER")
                .build();

        MemberResponse memberResponse = MemberResponse.from(createMember(1, "testuser", "test@test.com"));
        given(memberService.findByUsername("testuser")).willReturn(memberResponse);

        // when
        Model model = new ExtendedModelMap();
        String viewName = myPageController.index(userDetails, model);

        // then
        assertThat(viewName).isEqualTo("mypage/index");
        assertThat(model.getAttribute("member")).isEqualTo(memberResponse);
    }

    @Test
    @DisplayName("DB에 회원이 없으면 BusinessException이 발생한다")
    void 존재하지_않는_사용자_예외_발생() {
        // given
        UserDetails userDetails = User.builder()
                .username("ghost")
                .password("encoded_password")
                .roles("USER")
                .build();

        given(memberService.findByUsername("ghost"))
                .willThrow(new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        // when & then
        Model model = new ExtendedModelMap();
        assertThatThrownBy(() -> myPageController.index(userDetails, model))
                .isInstanceOf(BusinessException.class)
                .satisfies(e ->
                        assertThat(((BusinessException) e).getErrorCode())
                                .isEqualTo(ErrorCode.NOT_FOUND_MEMBER)
                );
    }
}