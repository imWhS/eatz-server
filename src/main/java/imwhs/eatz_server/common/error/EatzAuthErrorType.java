package imwhs.eatz_server.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EatzAuthErrorType implements EatzErrorType {

    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_REQUESTS", "오늘 하루 가입할 수 있는 최대 횟수를 초과했어요. 내일 다시 시도해주세요."),
    CREDENTIALS_INVALID(HttpStatus.UNAUTHORIZED, "CREDENTIALS_INVALID", "사용자 인증에 실패했어요. 이메일 주소 또는 암호가 올바르지 않아요."),
    NOT_VERIFIED_SIGN_IN_EMAIL(HttpStatus.NOT_FOUND, "NOT_VERIFIED_SIGN_IN_EMAIL", "인증이 완료되지 않은 이메일 주소예요."),
    UNAUTHORIZED_USER(HttpStatus.FORBIDDEN, "UNAUTHORIZED_USER", "권한이 없어요."),
    TOKEN_ACCESS_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_ACCESS_EXPIRED", "액세스 토큰이 만료됐어요."),
    TOKEN_REFRESH_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_REFRESH_EXPIRED", "리프레시 토큰이 만료됐어요."),
    TOKEN_ACCESS_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_ACCESS_INVALID", "액세스 토큰이 유효하지 않아요."),
    TOKEN_REFRESH_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_REFRESH_INVALID", "리프레시 토큰이 유효하지 않아요."),
    TOKEN_ACCESS_MISSING(HttpStatus.UNAUTHORIZED, "TOKEN_ACCESS_MISSING", "액세스 토큰이 없어요."),
    TOKEN_REFRESH_MISSING(HttpStatus.UNAUTHORIZED, "TOKEN_REFRESH_MISSING", "리프레시 토큰이 없어요.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}
