package imwhs.eatz_server.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EatzAuthPasswordResetErrorType implements EatzErrorType {

    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "PASSWORD_RESET_TOKEN_INVALID", "암호 설정 토큰이 유효하지 않아요."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "PASSWORD_RESET_UNAUTHORIZED", "암호 설정 권한이 없어요."),
    EMAIL_REACHED_DAILY_SENDABLE_LIMIT(HttpStatus.BAD_REQUEST, "PASSWORD_RESET_EMAIL_REACHED_SENDABLE_LIMIT", "오늘 하루 암호 설정 편지를 보낼 수 있는 횟수를 초과했어요.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}
