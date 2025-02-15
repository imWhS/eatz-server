package imwhs.eatz_server.dto.ingredient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class IngredientRecipeDto {

    Long recipeId;

    Long ingredientId;

    String ingredientName;

}
