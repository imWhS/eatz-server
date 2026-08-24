package imwhs.eatz_server.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EatzIngredientErrorType implements EatzErrorType {

    NOT_FOUND(HttpStatus.NOT_FOUND, "RATING_NOT_FOUND", "평가를 찾을 수 없어요."),
    DUPLICATED(HttpStatus.CONFLICT, "RATING_DUPLICATED", "이미 평가를 등록한 레시피예요.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}
