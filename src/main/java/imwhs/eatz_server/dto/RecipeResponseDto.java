package imwhs.eatz_server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import imwhs.eatz_server.domain.Recipe;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecipeResponseDto {

    private Long id;

    private String title;

    private String description;

    private String url;

    private String imageUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;

    public RecipeResponseDto(Recipe recipe) {
        this.id = recipe.getId();
        this.title = recipe.getTitle();
        this.description = recipe.getDescription();
        this.url = recipe.getUrl();
        this.imageUrl = recipe.getImageUrl();
        this.createdAt = recipe.getCreatedAt();
        this.updatedAt = recipe.getUpdatedAt();
        this.deletedAt = recipe.getDeletedAt();
    }

}
