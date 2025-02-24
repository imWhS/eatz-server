package imwhs.eatz_server.dto.recipe;

import com.fasterxml.jackson.annotation.JsonFormat;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

// TODO: EatzUser의 DTO 필드 추가
@AllArgsConstructor
@Data
public class RecipeDto {

    private Long id;

    private String title;

    private String description;

    private String url;

    private String imageUrl;

    private Long likedCount = null;

    private EatzUserBasicDto user;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;

    public RecipeDto(Recipe recipe) {
        this.id = recipe.getId();
        this.title = recipe.getTitle();
        this.description = recipe.getDescription();
        this.url = recipe.getUrl();
        this.imageUrl = recipe.getImageUrl();
        this.createdAt = recipe.getCreatedAt();
        this.updatedAt = recipe.getUpdatedAt();
        this.deletedAt = recipe.getDeletedAt();
    }

    public RecipeDto(Recipe recipe, EatzUser user, Long likedCount) {
        this.id = recipe.getId();
        this.title = recipe.getTitle();
        this.description = recipe.getDescription();
        this.url = recipe.getUrl();
        this.imageUrl = recipe.getImageUrl();
        this.user = new EatzUserBasicDto(user);
        this.likedCount = likedCount;
        this.createdAt = recipe.getCreatedAt();
        this.updatedAt = recipe.getUpdatedAt();
        this.deletedAt = recipe.getDeletedAt();
    }

    public RecipeDto(Recipe recipe, EatzUser user) {
        this.id = recipe.getId();
        this.title = recipe.getTitle();
        this.description = recipe.getDescription();
        this.url = recipe.getUrl();
        this.imageUrl = recipe.getImageUrl();
        this.user = new EatzUserBasicDto(user);
        this.likedCount = likedCount;
        this.createdAt = recipe.getCreatedAt();
        this.updatedAt = recipe.getUpdatedAt();
        this.deletedAt = recipe.getDeletedAt();
    }

}
