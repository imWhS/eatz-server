package imwhs.eatz_server.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EatzCommonErrorType implements EatzErrorType {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부에서 오류가 발생했어요."),
    INVALID_REQUEST_ARGUMENTS(HttpStatus.BAD_REQUEST, "INVALID_REQUEST_ARGUMENTS", "요청으로 보낸 데이터의 2개 이상이 올바르지 않아요."),
    INVALID_REQUEST_ARGUMENT(HttpStatus.BAD_REQUEST, "INVALID_REQUEST_ARGUMENT", "요청으로 보낸 데이터가 올바르지 않아요."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_NOT_FOUND", "댓글을 찾을 수 없어요."),
    THEME_NOT_FOUND(HttpStatus.NOT_FOUND, "THEME_NOT_FOUND", "테마를 찾을 수 없어요."),
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT_NOT_FOUND", "신고를 찾을 수 없어요."),
    REPORT_REASON_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT_REASON_NOT_FOUND", "신고 이유를 찾을 수 없어요."),
    REPORT_REASON_IS_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "REPORT_REASON_IS_NOT_ACTIVE", "비활성화된 신고 이유예요."),
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "TAG_NOT_FOUND", "태그를 찾을 수 없어요."),
    INGREDIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "INGREDIENT_NOT_FOUND", "재료를 찾을 수 없어요."),
    KITCHENWARE_NOT_FOUND(HttpStatus.NOT_FOUND, "KITCHENWARE_NOT_FOUND", "도구를 찾을 수 없어요."),
    LIKED_NOT_FOUND(HttpStatus.NOT_FOUND, "LIKED_NOT_FOUND", "좋아요를 찾을 수 없어요.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}
