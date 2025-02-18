package imwhs.eatz_server.dto.recipe;

import lombok.Data;

import java.util.List;

@Data
public class IngredientAddDto {

    List<Long> ingredientIds;

}
