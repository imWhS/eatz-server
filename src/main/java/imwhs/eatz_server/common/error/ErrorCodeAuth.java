package imwhs.eatz_server.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCodeAuth implements BaseErrorCode {

    CREDENTIALS_INVALID(HttpStatus.UNAUTHORIZED, "CREDENTIALS_INVALID", "아이디 또는 비밀 번호가 올바르지 않아요."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "INVALID_VERIFICATION_CODE", "인증 코드가 올바르지 않아요."),
    NOT_VERIFIED_SIGN_IN_EMAIL(HttpStatus.BAD_REQUEST, "NOT_VERIFIED_SIGN_IN_EMAIL", "인증이 완료되지 않은 이메일 주소예요."),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED_USER", "권한이 없어요."),
    TOKEN_ACCESS_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_ACCESS_EXPIRED", "액세스 토큰이 만료됐어요."),
    TOKEN_REFRESH_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_REFRESH_EXPIRED", "리프레시 토큰이 만료됐어요."),
    TOKEN_ACCESS_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_ACCESS_INVALID", "액세스 토큰이 유효하지 않아요."),
    TOKEN_REFRESH_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_REFRESH_INVALID", "리프레시 토큰이 유효하지 않아요."),
    TOKEN_ACCESS_MISSING(HttpStatus.BAD_REQUEST, "TOKEN_ACCESS_MISSING", "액세스 토큰이 존재하지 않아요."),
    TOKEN_REFRESH_MISSING(HttpStatus.BAD_REQUEST, "TOKEN_REFRESH_MISSING", "리프레시 토큰이 존재하지 않아요.");

    private final HttpStatus status;

    private final String code;

    private final String message;

}
