package imwhs.eatz_server.dto.ingredient;

import imwhs.eatz_server.domain.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IngredientCategoryResponseDto {

    private Long categoryId;

    private String categoryName;

    public IngredientCategoryResponseDto(Ingredient category) {
        this.categoryId = category.getId();
        this.categoryName = category.getName();
    }

}