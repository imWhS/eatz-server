package imwhs.eatz_server.dto.apiresponse;

import com.fasterxml.jackson.annotation.JsonFormat;
import imwhs.eatz_server.common.error.BaseErrorCode;
import imwhs.eatz_server.common.error.ErrorCode;
import imwhs.eatz_server.dto.Paged;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

/**
 * ApiResponse 클래스입니다.
 * 모든 API의 응답을 공통 형식으로 전달하기 위해 사용합니다.
 * @param <T> 응답 데이터 타입.
 */
@Getter
public class ApiResponse<T> {

    /**
     * 응답 상태.<br/>
     * 요청에 대한 비즈니스 로직을 성공적으로 처리한 경우 SUCCESS, 비즈니스 로직 처리에 실패한 경우 ERROR로 설정합니다.
     */
    private ApiResponseStatus status;

    /**
     * 비즈니스 데이터.<br/>
     * 요청에 대한 비즈니스 로직을 성공적으로 처리한 결과 데이터입니다. 비즈니스 로직 처리에 실패한 경우 null이 됩니다.
     */
    private T data;

    /**
     * 시스템 오류 코드.<br/>
     * 비즈니스 로직 처리 실패 원인 또는 이유를 나타내는 코드입니다. 비즈니스 로직 처리에 성공한 경우 null이 됩니다.
     */
    private String errorCode;

    /**
     * 메시지.<br/>
     * 요청 처리 결과에 대한 추가 메시지입니다.
     */
    private String message;

    /**
     * 응답 생성 시점의 타임스탬프.<br/>
     * HTTP 응답이 생성된 시간입니다.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public ApiResponse(ApiResponseStatus status, T data, String errorCode, String message) {
        this.status = status;
        this.data = data;
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(ApiResponseStatus status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 요청을 성공적으로 처리한 경우의 HTTP 응답 데이터를 생성합니다. 응답 데이터는 생략합니다.
     * @return ApiResponse 객체.
     * @param <T> 응답 데이터 타입.
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(ApiResponseStatus.SUCCESS, null, null);
    }

    /**
     * 요청을 성공적으로 처리한 경우의 HTTP 응답 데이터를 생성합니다.
     * @param data 응답 데이터.
     * @return ApiResponse 객체.
     * @param <T> 응답 데이터 타입.
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ApiResponseStatus.SUCCESS, data, null);
    }

    /**
     * 요청을 성공적으로 처리한 경우의 HTTP 응답 데이터를 생성합니다.
     * @param data 응답 데이터.
     * @param message 메시지.
     * @return ApiResponse 객체.
     * @param <T> 응답 데이터 타입.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(ApiResponseStatus.SUCCESS, data, message);
    }

    /**
     * 요청을 성공적으로 처리한 경우의 HTTP 응답 데이터를 생성합니다.
     * @param message 메시지.
     * @return ApiResponse 객체.
     * @param <T> 응답 데이터 타입.
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(ApiResponseStatus.SUCCESS, null, message);
    }

    /**
     * 요청을 성공적으로 처리한 경우의 HTTP 응답 데이터를 생성합니다. 페이징을 적용합니다.
     * @param page 페이징 적용된 응답 데이터
     * @return ApiResponse 객체.
     * @param <T> 응답 데이터 타입.
     */
    public static <T> ApiResponse<Paged<T>> success(Page<T> page) {
        return ApiResponse.success(new Paged<>(page));
    }

    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return new ApiResponse<>(ApiResponseStatus.ERROR, null, errorCode, message);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(ApiResponseStatus.ERROR, null, message);
    }

}
