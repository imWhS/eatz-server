package imwhs.eatz_server.dto.ingredient;

import imwhs.eatz_server.domain.Ingredient;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 조회 대상 재료의 핵심 정보와 모든 하위 재료의 기본 정보 목록을 전달할 때 사용하는 DTO입니다.
 * <ul>
 *      <li> 조회 대상 재료에 포함되어져 있는 모든 하위 재료의 기본 정보에는 로그인한 사용자의 context도 포함합니다. </li>
 *      <li> 단, 모든 하위 재료 목록과 같이 1:N 연관 관계를 설정하는 컬렉션 필드는
 *           성능 최적화(카테시안 곱 방지)를 위해 생성자가 아닌 setter를 통해 별도로 초기화해야 합니다. </li>
 * </ul>
 */
@Data
public class IngredientEssentialWithChildrenBasicsDto {

    /**
     * 조회 대상 재료의 ID
     */
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
     * 조회 대상 재료의 이름
     */
    private String name;

    /**
     * 조회 대상 재료에 포함되어져 있는 모든 하위 재료의 기본 정보 목록
     */
    private List<IngredientBasicDto> children;

    /**
     * 조회 대상 재료의 엔티티를 이용해 DTO 인스턴스를 생성합니다.
     * <ul>
     *     <li> 성능 최적화를 위해 Ingredient.parent.name이 null이 아니거나, Ingredient.parent까지 페치 조인되어 있어야 합니다. </li>
     *     <li> 성능 최적화를 위해 children은 empty인 상태로 초기화되므로, setter를 통해 값을 할당해주어야 합니다. </li>
     * </ul>
     * @param ingredient Ingredient 인스턴스
     */
    public IngredientEssentialWithChildrenBasicsDto(Ingredient ingredient) {
        this.id = ingredient.getId();
        this.isParentCoupled = ingredient.getIsParentCoupled();
        this.coupledParentName = Objects.nonNull(ingredient.getParent()) ? ingredient.getParent().getName() : null;
        this.name = ingredient.getName();
        this.children = Collections.emptyList();
    }

}
