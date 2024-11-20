package imwhs.eatz_server.dto.ingredient;

import imwhs.eatz_server.domain.Ingredient;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * IngredientResponseDto 클래스입니다.<br/>
 * 재료 정보를 전달하기 위해 사용합니다. 해당 재료의 모든 하위 계층(hierarchy) 재료의 정보도 포함합니다.
 */
@Data
public class IngredientTreeResponseDto {

    private Long id;

    private String name;

    private IngredientCategoryResponseDto category;

    private List<IngredientTreeResponseDto> children;

    public IngredientTreeResponseDto(Ingredient ingredient) {
        this.id = ingredient.getId();
        this.name = ingredient.getName();
        this.category = new IngredientCategoryResponseDto(ingredient.getCategory());
        this.children = ingredient.getChildren().stream().map(IngredientTreeResponseDto::new).collect(Collectors.toList());
    }

    public IngredientTreeResponseDto(
            Long id,
            String name,
            IngredientCategoryResponseDto category,
            List<IngredientTreeResponseDto> children) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.children = children;
    }

    public IngredientTreeResponseDto(
            Long id,
            String name,
            IngredientCategoryResponseDto category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

}
