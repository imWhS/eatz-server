package imwhs.eatz_server.dto.recipe.savedrecipe;

import imwhs.eatz_server.domain.recipe.SavedRecipe;
import imwhs.eatz_server.dto.recipe.RecipeSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SavedRecipeDto {

    private Long id;

    private RecipeSummaryDto recipe;

    private LocalDate scheduledDate;

    private Long userId;

    private LocalDateTime createdAt;

    public SavedRecipeDto(SavedRecipe savedRecipe) {
        this.id = savedRecipe.getId();
        this.recipe = new RecipeSummaryDto(savedRecipe.getRecipe());
        this.userId = savedRecipe.getUser().getId();
        this.createdAt = savedRecipe.getCreatedAt();
    }

}
