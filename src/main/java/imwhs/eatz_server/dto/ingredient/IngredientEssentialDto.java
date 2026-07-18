package imwhs.eatz_server.dto.ingredient;

import imwhs.eatz_server.domain.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 재료의 핵심 정보를 전달할 때 사용합니다.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Data
public class IngredientEssentialDto {

    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    public static IngredientEssentialDto create(Ingredient ingredient) {
        if (ingredient == null) { return null; }
        return new IngredientEssentialDto(ingredient.getId(), ingredient.getName());
    }

}
