package imwhs.eatz_server.dto.recipe.savedrecipe;

import imwhs.eatz_server.domain.recipe.SavedRecipe;
import imwhs.eatz_server.domain.recipe.SavedRecipeSchedule;
import imwhs.eatz_server.dto.recipe.RecipeBasicDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class SavedRecipeDto {

    private Long id;

    private Long userId;

    private LocalDateTime createdAt;

    private RecipeBasicDto recipe;

    private List<LocalDate> scheduledDates;

    public SavedRecipeDto(SavedRecipe savedRecipe) {
        this.id = savedRecipe.getId();
        this.userId = savedRecipe.getUser().getId();
        this.createdAt = savedRecipe.getCreatedAt();
        this.recipe = new RecipeBasicDto(savedRecipe.getRecipe());
        this.scheduledDates = savedRecipe.getSchedules().stream().map(SavedRecipeSchedule::getDate).toList();
    }

}
