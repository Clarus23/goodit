package kr.goodit.assignment.member.controller;

import kr.goodit.assignment.member.domain.Member;
import kr.goodit.assignment.member.dto.MemberResponse;
import kr.goodit.assignment.member.security.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MyPageControllerTest {

    @InjectMocks
    private MyPageController myPageController;

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
        Member member = createMember(1, "testuser", "test@test.com");
        CustomUserDetails userDetails = new CustomUserDetails(member);

        // when
        Model model = new ExtendedModelMap();
        String viewName = myPageController.index(userDetails, model);

        // then
        MemberResponse result = (MemberResponse) model.getAttribute("member");
        assertThat(viewName).isEqualTo("mypage/index");
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("userDetails가 null이면 로그인 페이지로 redirect된다")
    void userDetails_null이면_로그인으로_redirect() {
        Model model = new ExtendedModelMap();

        String viewName = myPageController.index(null, model);

        assertThat(viewName).isEqualTo("redirect:/login");
    }
}