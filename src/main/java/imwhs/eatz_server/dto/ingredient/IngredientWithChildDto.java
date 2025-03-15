package imwhs.eatz_server.dto.ingredient;

import imwhs.eatz_server.domain.ingredient.Ingredient;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * IngredientWithChildDto 클래스입니다.<br/>
 * 재료 및 재료의 하위 재료 목록 정보를 전달하기 위해 사용합니다.
 */
@Data
public class IngredientWithChildDto {

    private Long id;

    private String name;

    private List<IngredientChildDto> children;

    public IngredientWithChildDto(Ingredient ingredient) {
        this.id = ingredient.getId();
        this.name = ingredient.getName();
        this.children = ingredient.getChildren()
                .stream().map(IngredientChildDto::new).collect(Collectors.toList());
    }

}
