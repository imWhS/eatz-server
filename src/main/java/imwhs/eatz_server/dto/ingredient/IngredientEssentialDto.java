package imwhs.eatz_server.dto.ingredient;

import imwhs.eatz_server.domain.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * 재료의 핵심 정보를 전달할 때 사용합니다.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Data
public class IngredientEssentialDto {

    @EqualsAndHashCode.Include
    private Long id;

    private boolean isParentCoupled;

    private String coupledParentName;

    private String name;

    public IngredientEssentialDto(Long id, boolean isParentCoupled, String name) {
        this.id = id;
        this.isParentCoupled = isParentCoupled;
        this.name = name;
    }

    public static IngredientEssentialDto create(Ingredient ingredient) {
        if (ingredient == null) { return null; }
        return new IngredientEssentialDto(
                ingredient.getId(),
                Objects.nonNull(ingredient.getParent()) && ingredient.getIsParentCoupled(),
                ingredient.getName());
    }

    public void setCoupledParentName(String coupledParentName) {
        if (Objects.isNull(coupledParentName)) { return; }
        this.coupledParentName = coupledParentName;
    }

}
