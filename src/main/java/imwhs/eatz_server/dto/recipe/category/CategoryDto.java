package imwhs.eatz_server.dto.recipe.category;

import com.fasterxml.jackson.annotation.JsonFormat;
import imwhs.eatz_server.domain.Category;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * CategoryDetailDto 클래스입니다.
 */
@Data
public class CategoryDto {

    private Long id;

    private String name;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public CategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.createdAt = category.getCreatedAt();
        this.updatedAt = category.getUpdatedAt();
    }

}
