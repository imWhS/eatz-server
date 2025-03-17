package imwhs.eatz_server.dto.plan;

import imwhs.eatz_server.dto.ingredient.IngredientDto;
import imwhs.eatz_server.dto.recipe.RecipeItemBasicDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class NChecklistDto {

    private final List<RecipeItemBasicDto> cookable;

    private final List<RecipeItemBasicDto> uncookable;

    private final Set<IngredientDto> missingIngredients;

}
