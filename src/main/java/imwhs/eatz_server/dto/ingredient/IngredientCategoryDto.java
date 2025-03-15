package imwhs.eatz_server.dto.ingredient;

import imwhs.eatz_server.domain.ingredient.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IngredientCategoryDto {

    private Long id;

    private String name;

    public IngredientCategoryDto(Ingredient category) {
        this.id = category.getId();
        this.name = category.getName();
    }

}