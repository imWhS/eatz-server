package imwhs.eatz_server.dto.ingredient;

import imwhs.eatz_server.domain.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * IngredientResponseDto 클래스입니다.<br/>
 * 재료 정보를 전달하기 위해 사용합니다.
 */
@Data
public class IngredientResponseDto {

    private Long id;

    private String name;

    private IngredientCategoryResponseDto category;

    private List<IngredientChildResponseDto> children;

    public IngredientResponseDto(Ingredient ingredient) {
        this.id = ingredient.getId();
        this.name = ingredient.getName();

        this.category = (ingredient.getCategory() != null)
                ? new IngredientCategoryResponseDto(ingredient.getCategory())
                : null;
        this.children = ingredient.getChildren().stream().map(IngredientChildResponseDto::new).collect(Collectors.toList());
    }

}
