package imwhs.eatz_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedResponseDto<T> {

    private List<T> items;

    private long totalItems;

    private int totalPages;

    private int currentPage;

    private int pageSize;

    public static <T> PagedResponseDto<T> of(Page<T> page) {
        return new PagedResponseDto<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

}
