package imwhs.eatz_server.dto.recipe;

import lombok.Data;

import java.util.List;

@Data
public class IngredientRemoveDto {

    List<Long> ingredientIds;

}
