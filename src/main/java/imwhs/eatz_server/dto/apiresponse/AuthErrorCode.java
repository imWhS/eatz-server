package imwhs.eatz_server.dto.apiresponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    UNAUTHORIZED_USER("UNAUTHORIZED_USER", "사용자 인증이 필요합니다."),
    TOKEN_REFRESH_EXPIRED("TOKEN_REFRESH_EXPIRED", "리프레시 토큰이 만료됐습니다."),
    TOKEN_ACCESS_EXPIRED("TOKEN_ACCESS_EXPIRED", "액세스 토큰이 만료됐습니다."),
    EMAIL_PASSWORD_MISMATCH("EMAIL_PASSWORD_MISMATCH", "이메일 주소와 비밀 번호에 해당하는 사용자가 존재하지 않습니다.");

    private final String code;
    private final String message;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
