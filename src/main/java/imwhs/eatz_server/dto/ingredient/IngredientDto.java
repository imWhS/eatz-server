package imwhs.eatz_server.dto.ingredient;

import imwhs.eatz_server.domain.Ingredient;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * IngredientDto 클래스입니다.<br/>
 * 재료 정보를 전달하기 위해 사용합니다.
 */
@Data
public class IngredientDto {

    private Long id;

    private String name;

    private IngredientCategoryDto category;

    private List<IngredientChildDto> children;

    public IngredientDto(Ingredient ingredient) {
        this.id = ingredient.getId();
        this.name = ingredient.getName();

        this.category = (ingredient.getCategory() != null)
                ? new IngredientCategoryDto(ingredient.getCategory())
                : null;
        this.children = ingredient.getChildren()
                .stream().map(IngredientChildDto::new).collect(Collectors.toList());
    }

}
