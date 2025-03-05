package imwhs.eatz_server.dto.ingredient;

import imwhs.eatz_server.domain.ingredient.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IngredientCategoryDto {

    private Long categoryId;

    private String categoryName;

    public IngredientCategoryDto(Ingredient category) {
        this.categoryId = category.getId();
        this.categoryName = category.getName();
    }

}