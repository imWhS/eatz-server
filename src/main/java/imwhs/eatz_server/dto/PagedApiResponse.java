package imwhs.eatz_server.dto;

import lombok.Data;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
public class PagedApiResponse<T> {

    /**
     * 응답 상태.<br/>
     * 요청에 대한 비즈니스 로직을 성공적으로 처리한 경우 "success", 처리 중 예외가 발생한 경우 "error"를 사용합니다.
     */
    private String status;

    /**
     * 페이징이 적용된 데이터 컬렉션 응답 데이터.
     */
    private List<T> data;

    /**
     * 메시지.<br/>
     * 요청 처리와 관련된 추가 메시지입니다.
     */
    private String message;

    /**
     * 데이터 목록의 전체 데이터 수.
     */
    private Long totalItems;

    /**
     * 전체 페이지 수.
     */
    private Integer totalPages;

    /**
     * 현재 페이지 번호.<br/>
     * 페이지는 0부터 시작합니다.
     */
    private Integer page;

    /**
     * 페이징 크기.<br/>
     * 페이지 당 포함할 데이터 수에 해당하는 값으로, 전체 데이터 수가 아님에 유의해야 합니다.
     */
    private Integer size;

    /**
     * 응답 생성 시점의 타임스탬프.
     */
    private LocalDateTime timestamp;

    public PagedApiResponse(
            String status,
            List<T> data,
            String message,
            Long totalItems,
            Integer totalPages,
            Integer page,
            Integer size) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        this.page = page;
        this.size = size;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 요청을 성공적으로 처리한 공통 API 응답 데이터를 생성합니다.
     * @param data 응답 데이터
     * @return ApiResponse
     * @param <T> 응답 데이터 타입
     */
    public static <T> PagedApiResponse<T> success(
            List<T> data,
            long totalItems,
            int totalPages,
            int page,
            int size) {
        return new PagedApiResponse<>(
                "success",
                data,
                null,
                totalItems,
                totalPages,
                page,
                size);
    }

    public static <T> PagedApiResponse<T> success(Page<T> page) {
        return new PagedApiResponse<>(
                "success",
                page.getContent(),
                null,
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    public static <T> PagedApiResponse<T> error(String message) {
        return new PagedApiResponse<>(
                "error",
                null, message,
                null,
                null,
                null,
                null);
    }

}
