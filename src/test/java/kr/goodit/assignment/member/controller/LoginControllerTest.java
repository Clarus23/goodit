package kr.goodit.assignment.member.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @InjectMocks private LoginController loginController;

    @Test
    @DisplayName("세션에 메시지가 없으면 로그인 뷰를 반환하고 모델에 아무것도 담지 않는다")
    void 세션_메시지_없을_때_로그인_뷰_반환() {
        MockHttpSession session = new MockHttpSession();
        Model model = new ExtendedModelMap();

        String viewName = loginController.login(session, model, null);

        assertThat(viewName).isEqualTo("member/login");
        assertThat(model.getAttribute("errorMessage")).isNull();
        assertThat(model.getAttribute("logoutMessage")).isNull();
    }

    @Test
    @DisplayName("세션에 loginError가 있으면 errorMessage를 모델에 담고 세션에서 제거한다")
    void 로그인_실패_에러메시지_모델에_담기고_세션에서_제거() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loginError", "아이디 또는 비밀번호가 올바르지 않습니다.");
        Model model = new ExtendedModelMap();

        String viewName = loginController.login(session, model, null);

        assertThat(viewName).isEqualTo("member/login");
        assertThat(model.getAttribute("errorMessage"))
                .isEqualTo("아이디 또는 비밀번호가 올바르지 않습니다.");
        assertThat(session.getAttribute("loginError")).isNull();
    }

    @Test
    @DisplayName("이미 인증된 사용자가 로그인 페이지 접근 시 마이페이지로 redirect된다")
    void 인증된_사용자_마이페이지로_redirect() {
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("encoded_password")
                .roles("USER")
                .build();
        MockHttpSession session = new MockHttpSession();
        Model model = new ExtendedModelMap();

        String viewName = loginController.login(session, model, userDetails);

        assertThat(viewName).isEqualTo("redirect:/mypage");
    }


    @Test
    @DisplayName("세션에 logoutMessage가 있으면 모델에 담고 세션에서 제거한다")
    void 로그아웃_메시지_모델에_담기고_세션에서_제거() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("logoutMessage", "로그아웃 되었습니다.");
        Model model = new ExtendedModelMap();

        String viewName = loginController.login(session, model, null);

        assertThat(viewName).isEqualTo("member/login");
        assertThat(model.getAttribute("logoutMessage"))
                .isEqualTo("로그아웃 되었습니다.");
        assertThat(session.getAttribute("logoutMessage")).isNull();
    }
}
