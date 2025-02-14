package imwhs.eatz_server.dto.ingredient;

import imwhs.eatz_server.domain.Ingredient;
import lombok.Data;

@Data
public class IngredientDto {

    private Long id;

    private String name;

    public IngredientDto(Ingredient ingredient) {
        this.id = ingredient.getId();
        this.name = ingredient.getName();
    }

}
