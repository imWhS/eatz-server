package imwhs.eatz_server.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class Paged<T> {

    private List<T> content;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;

    private boolean hasPrevious;

    private boolean hasNext;

    public Paged(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.hasPrevious = page.hasPrevious();
        this.hasNext = page.hasNext();
    }

}
