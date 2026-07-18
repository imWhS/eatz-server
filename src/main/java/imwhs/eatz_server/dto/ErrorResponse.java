package imwhs.eatz_server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import imwhs.eatz_server.common.error.EatzErrorType;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 모든 API의 오류 응답을 공통 형식으로 전달하기 위해 사용합니다.
 */
@Getter
public class ErrorResponse {

    /**
     * 시스템 오류 코드<br/>
     * 비즈니스 로직 처리 실패 원인 또는 이유를 나타내는 코드입니다. 비즈니스 로직 처리에 성공한 경우 null이 됩니다.
     */
    private final String code;

    /**
     * 메시지<br/>
     * 요청 처리 결과에 대한 추가 메시지입니다.
     */
    private final String message;

    /**
     * 응답 생성 시점의 타임스탬프<br/>
     * HTTP 응답이 생성된 시간입니다.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    private ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    private ErrorResponse(String message) {
        this.code = null;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public static ErrorResponse create(EatzErrorType errorType) {
        return new ErrorResponse(errorType.getCode(), errorType.getMessage());
    }

    public static ErrorResponse create(EatzErrorType errorType, String customMesssage) {
        return new ErrorResponse(errorType.getCode(), customMesssage);
    }

    public static ErrorResponse create(String errorCode, String message) {
        return new ErrorResponse(errorCode, message);
    }

//    public static ErrorResponse create(String message) {
//        return new ErrorResponse(message);
//    }

}
