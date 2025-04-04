package imwhs.eatz_server.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCodeUser implements BaseErrorCode {

    EATZ_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "EATZ_USER_NOT_FOUND", "사용자를 찾을 수 없어요."),
    DUPLICATED_USER_EMAIL(HttpStatus.BAD_REQUEST, "DUPLICATED_USER_EMAIL", "이미 사용 중인 이메일 주소예요."),
    DUPLICATED_USER_USERNAME(HttpStatus.BAD_REQUEST, "DUPLICATED_USER_USERNAME", "이미 사용 중인 사용자 이름이에요."),
    INCORRECT_CURRENT_PASSWORD(HttpStatus.UNAUTHORIZED, "INCORRECT_CURRENT_PASSWORD", "현재 사용 중인 비밀번호와 일치하지 않아요."),
    EATZ_USER_USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND, "EATZ_USER_USERNAME_NOT_FOUND", "존재하지 않는 사용자 이름이에요.");

    private final HttpStatus status;

    private final String code;

    private final String message;

}
