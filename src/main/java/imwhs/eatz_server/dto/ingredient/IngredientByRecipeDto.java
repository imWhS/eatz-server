package imwhs.eatz_server.dto.ingredient;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IngredientByRecipeDto {

    Long recipeId;

    Long ingredientId;

    String ingredientName;

}
