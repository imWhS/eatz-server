package imwhs.eatz_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedResponse<T> {

    // 페이징을 적용할 데이터 목록.
    private List<T> data;

    // 데이터 목록의 전체 데이터 수.
    private long totalItems;

    // 전체 페이지 수.
    private int totalPages;

    // 현재 페이지 번호. 0부터 시작합니다.
    private int page;

    // 한 페이지에 포함할 데이터 수
    private int size;

    public static <T> PagedResponse<T> of(List<T> data, long totalItems, int totalPages, int page, int size) {
        return new PagedResponse<>(data, totalItems, totalPages, page, size);
    }

}
