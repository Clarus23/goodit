package kr.goodit.assignment.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSignupRequest {

    @NotBlank(message = "사용자명을 입력하여 주십시오.")
    @Size(min = 3, max = 50, message = "사용자명은 3자 이상 50자 이하여야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호를 입력하여 주십시오.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이메일을 입력하여 주십시오.")
    @Email(message = "올바르지 않은 이메일 입니다.")
    private String email;
}
