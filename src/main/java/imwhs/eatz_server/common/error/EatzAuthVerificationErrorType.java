package imwhs.eatz_server.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EatzAuthVerificationErrorType implements EatzErrorType {

    CODE_VALIDATION_INVALID(HttpStatus.BAD_REQUEST, "VERIFICATION_CODE_VALIDATION_INVALID", "인증 코드가 올바르지 않아요. 인증 시간이 만료되었거나, 인증을 시도한 적 없는 것 같아요."),
    CODE_VALIDATION_MISMATCH(HttpStatus.BAD_REQUEST, "VERIFICATION_CODE_VALIDATION_MISMATCH", "인증 코드가 일치하지 않아요."),
    CODE_DAILY_CREATION_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "VERIFICATION_CODE_DAILY_CREATION_LIMIT_EXCEEDED", "오늘 하루 인증 코드를 생성할 수 있는 횟수를 초과했어요."),
    CODE_REISSUABLE_INTERVAL_REMAINING(HttpStatus.BAD_REQUEST, "VERIFICATION_CODE_INTERVAL_REMAINING", "인증 코드를 발급 받을 수 있는 대기 시간이 아직 지나지 않았어요.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}
