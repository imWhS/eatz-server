package imwhs.eatz_server.dto.ingredient;

import imwhs.eatz_server.domain.ingredient.Ingredient;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * IngredientTreeDto 클래스입니다.<br/>
 * 재료 정보를 전달하기 위해 사용합니다. 해당 재료의 모든 하위 계층(hierarchy) 재료의 정보도 포함합니다.
 */
@Data
public class IngredientTreeDto {

    private Long id;

    private String name;

    private IngredientCategoryDto category;

    private List<IngredientTreeDto> children;

    public IngredientTreeDto(Ingredient ingredient) {
        this.id = ingredient.getId();
        this.name = ingredient.getName();
        this.category = new IngredientCategoryDto(ingredient.getCategory());
        this.children = ingredient.getChildren().stream().map(IngredientTreeDto::new).collect(Collectors.toList());
    }

    public IngredientTreeDto(
            Long id,
            String name,
            IngredientCategoryDto category,
            List<IngredientTreeDto> children) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.children = children;
    }

    public IngredientTreeDto(
            Long id,
            String name,
            IngredientCategoryDto category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

}
