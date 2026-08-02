package imwhs.eatz_server.dto.plan;

import imwhs.eatz_server.domain.Ingredient;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChecklistIngredientDto {

    /**
     * 재료의 ID
     */
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * 상위 재료와의 커플링 여부
     */
    private boolean isParentCoupled;

    /**
     * 커플링된 상위 재료 이름
     * <ul>
     *     <li> 조회 대상 재료의 이름과 함께 접두어로 사용됩니다. </li>
     * </ul>
     */
    private String coupledParentName;

    /**
     * 재료의 이름
     */
    private String name;

    /**
     * 재료가 보관함에 추가되어 있지 않은지 여부
     */
    private boolean isMissing;

    /**
     * 재료를 좋아하는 사람 여부
     */
    private boolean isLikedByUser;

    public ChecklistIngredientDto(Long id, String name, boolean isLikedByUser) {
        this.id = id;
        this.name = name;
        this.isLikedByUser = isLikedByUser;
    }

    public ChecklistIngredientDto(Ingredient ingredient, boolean isMissing, boolean isLikedByUser) {
        this.id = ingredient.getId();
        this.name = ingredient.getName();
        this.isMissing = isMissing;
        this.isLikedByUser = isLikedByUser;
    }

}
