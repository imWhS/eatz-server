package imwhs.eatz_server.dto.ingredient;

import imwhs.eatz_server.domain.Ingredient;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 조회 대상 상위 재료에 포함되어 있는 모든 재료(Ingredient)의 상세한 정보를 전달할 때 사용하는 DTO입니다.
 * <ul>
 *      <li> 조회 대상 상위 재료의 재료로서의 기본 정보와, 상위 재료가 포함되어 있는 더 상위의 상위 재료의 핵심 정보,
 *           상위 재료에 포함되어져 있는 모든 재료의 기본 정보 목록 등과 같은 연관 관계 엔티티 정보를 포함합니다. </li>
 *      <li> 조회 대상 상위 재료에 포함되어져 있는 재료의 기본 정보에는 로그인한 사용자의 context도 포함합니다. </li>
 *      <li> 단, 조회 대상 상위 재료에 포함되어져 있는 재료 목록과 같이 1:N 연관 관계를 설정하는 컬렉션 필드는,
 *           성능 최적화(카테시안 곱 방지)를 위해 생성자가 아닌 setter를 통해 별도로 초기화해야 합니다. </li>
 * </ul>
 */
@Data
public class IngredientBasicsInParentDto {

    /**
     * 조회 대상 상위 재료의 ID
     */
    private Long id;

    /**
     * 조회 대상 상위 재료의 이름
     */
    private String name;

    /**
     * 조회 대상 상위 재료가 포함되어 있는, 더 상위 상위 재료의 핵심 정보
     * <p> 상위 재료가 최상위 계층(root)에 해당할 경우 null을 가집니다. </p>
     */
    private IngredientEssentialDto parent;

    /**
     * 조회 대상 상위 재료에 포함되어져 있는 모든 재료의 기본 정보 목록
     */
    private List<IngredientBasicDto> ingredients;

    public IngredientBasicsInParentDto(Ingredient parent) {
        this.id = parent.getId();
        this.name = parent.getName();
        this.parent = IngredientEssentialDto.create(parent.getParent());
        this.ingredients = Collections.emptyList();
    }

}
