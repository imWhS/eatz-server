package imwhs.eatz_server.dto.recipe;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 레시피 생성 DTO
 */
@Data
@AllArgsConstructor
public class CreateRecipeDto {

    @NotNull
    private String title;

    @NotNull
    private String url;

    @NotNull
    private String imageUrl;

    private String description;

    public Recipe toEntity(EatzUser user) {
        return Recipe.of(user, title, url, imageUrl, description);
    }

}
