package imwhs.eatz_server.dto.recipe.ingredient;

import lombok.Data;

import java.util.List;

@Data
public class RemoveIngredientDto {

    List<Long> ingredientIds;

}
