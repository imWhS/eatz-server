package imwhs.eatz_server.dto.plan;

import imwhs.eatz_server.dto.ingredient.IngredientDto;
import imwhs.eatz_server.dto.recipe.RecipeBasicDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChecklistItemDto {

    private RecipeBasicDto recipe;

    private IngredientDto ingredient;

    private boolean isMissing;

}
