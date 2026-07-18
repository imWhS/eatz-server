package imwhs.eatz_server.dto.recipe;

import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.CreationInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RecipeCreationInfoDto extends CreationInfoResponse {

    public RecipeCreationInfoDto(Long id, LocalDateTime createdAt) {
        super(id, createdAt);
    }

    public RecipeCreationInfoDto(Recipe recipe) {
        this(recipe.getId(), recipe.getCreatedAt());
    }

}
