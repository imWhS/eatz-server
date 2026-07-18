package imwhs.eatz_server.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EatzRecipeErrorType implements EatzErrorType {

    NOT_FOUND(HttpStatus.NOT_FOUND, "RECIPE_NOT_FOUND", "레시피를 찾을 수 없어요."),
    SAVED_NOT_FOUND(HttpStatus.NOT_FOUND, "RECIPE_SAVED_NOT_FOUND", "저장된 레시피를 찾을 수 없어요.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}