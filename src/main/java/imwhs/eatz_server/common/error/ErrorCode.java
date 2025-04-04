package imwhs.eatz_server.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR", "서버 내부 오류예요."),
    RECIPE_NOT_FOUND(HttpStatus.NOT_FOUND, "RECIPE_NOT_FOUND", "레시피를 찾을 수 없어요."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_NOT_FOUND", "댓글을 찾을 수 없어요."),
    RATING_NOT_FOUND(HttpStatus.NOT_FOUND, "RATING_NOT_FOUND", "평가를 찾을 수 없어요."),
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT_NOT_FOUND", "신고를 찾을 수 없어요."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND", "카테고리를 찾을 수 없어요."),
    INGREDIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "INGREDIENT_NOT_FOUND", "재료를 찾을 수 없어요."),
    LIKED_NOT_FOUND(HttpStatus.NOT_FOUND, "LIKED_NOT_FOUND", "좋아요를 찾을 수 없어요."),
    PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "PLAN_NOT_FOUND", "플랜을 찾을 수 없어요."),
    SAVED_RECIPE_NOT_FOUND(HttpStatus.NOT_FOUND, "SAVED_RECIPE_NOT_FOUND", "저장된 레시피를 찾을 수 없어요."),
    DUPLICATED_PLAN(HttpStatus.BAD_REQUEST, "DUPLICATED_PLAN", "이미 플래너의 해당 날짜에 레시피가 등록되어 있어요.");


    private final HttpStatus status;

    private final String code;

    private final String message;

}
