package kr.goodit.assignment.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Member
    DUPLICATE_USERNAME(409, "이미 사용중인 사용자명입니다."),
    DUPLICATE_EMAIL(409, "이미 사용중인 이메일입니다."),
    NOT_FOUND_MEMBER(404, "존재하지 않는 회원입니다."),

    // 전역
    NOT_FOUND(404, "리소스를 찾을 수 없습니다."),
    DUPLICATE(409, "이미 존재하는 값입니다."),
    INVALID_INPUT(400, "입력값이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다.");

    private final int status;
    private final String message;
}
