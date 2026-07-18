package imwhs.eatz_server.dto.recipe;

import imwhs.eatz_server.domain.recipe.SavedRecipe;
import imwhs.eatz_server.dto.CreationInfoResponse;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class SavedRecipeCreationInfoResponse extends CreationInfoResponse {

    public SavedRecipeCreationInfoResponse(Long id, LocalDateTime createdAt) {
        super(id, createdAt);
    }

    public SavedRecipeCreationInfoResponse(SavedRecipe savedRecipe) {
        this(savedRecipe.getId(), savedRecipe.getCreatedAt());
    }

}
