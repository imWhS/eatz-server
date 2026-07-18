package imwhs.eatz_server.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EatzUserErrorType implements EatzErrorType {

    NOT_FOUND(HttpStatus.NOT_FOUND, "EATZ_USER_NOT_FOUND", "사용자를 찾을 수 없어요."),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "EATZ_USER_DUPLICATED_EMAIL", "이미 다른 계정이 사용하고 있는 이메일 주소예요."),
    DUPLICATED_USERNAME(HttpStatus.CONFLICT, "EATZ_USER_DUPLICATED_USERNAME", "이미 다른 계정이 사용하고 있는 사용자 이름이에요."),
    INCORRECT_CURRENT_PASSWORD(HttpStatus.UNAUTHORIZED, "EATZ_USER_INCORRECT_CURRENT_PASSWORD", "현재 사용하고 있는 암호와 일치하지 않아요."),
    USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND, "EATZ_USER_USERNAME_NOT_FOUND", "해당 사용자 이름이 설정된 사용자를 찾을 수 없어요.");


    private final HttpStatus status;
    private final String code;
    private final String message;

}
