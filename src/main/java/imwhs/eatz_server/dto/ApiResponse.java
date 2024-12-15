package imwhs.eatz_server.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

/**
 * ApiResponse 클래스입니다.
 * 비즈니스 데이터를 공통 API 응답 데이터 형식으로 전달하기 위해 사용합니다.
 * @param <T> 응답 데이터 타입.
 */
@Getter
public class ApiResponse<T> {

    /**
     * 응답 상태.<br/>
     * 요청에 대한 비즈니스 로직을 성공적으로 처리한 경우 "success", 처리 중 예외가 발생한 경우 "error"를 사용합니다.
     */
    private String status;

    /**
     * 응답 데이터.<br/>
     */
    private T data;

    /**
     * 메시지.<br/>
     * 요청 처리와 관련된 추가 메시지입니다.
     */
    private String message;

    /**
     * 응답 생성 시점의 타임스탬프.
     */
    private LocalDateTime timestamp;

    public ApiResponse(String status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 요청을 성공적으로 처리한 공통 API 응답 데이터를 생성합니다.
     * @param data 응답 데이터.
     * @return ApiResponse 객체.
     * @param <T> 응답 데이터 타입.
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", data, null);
    }

    /**
     * 요청을 성공적으로 처리한 공통 API 응답 데이터를 생성합니다.
     * @param data 응답 데이터.
     * @param message 메시지.
     * @return ApiResponse 객체.
     * @param <T> 응답 데이터 타입.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>("success", data, message);
    }

    public static <T> ApiResponse<Paged<T>> success(Page<T> page) {
        return ApiResponse.success(new Paged<>(page));
    }

    /**
     * 요청 처리를 실패한 공통 API 응답 데이터를 생성합니다.
     * @param message 메시지.
     * @return ApiResponse 객체.
     * @param <T> 응답 데이터 타입.
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("error", null, message);
    }

}
