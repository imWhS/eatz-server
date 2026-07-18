package imwhs.eatz_server.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EatzPlanErrorType implements EatzErrorType {

    NOT_FOUND(HttpStatus.NOT_FOUND, "PLAN_NOT_FOUND", "플랜을 찾을 수 없어요."),
    DUPLICATED(HttpStatus.CONFLICT, "PLAN_DUPLICATED", "이미 플래너의 해당 날짜에 레시피가 등록되어 있어요.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}