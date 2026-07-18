package imwhs.eatz_server.dto.ingredient;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 재료 정보를 전달할 때 사용합니다. 해당 재료의 모든 하위 계층(hierarchy) 재료의 정보도 포함합니다.
 * <li> 단, tree와 같이 1:N 연관 관계를 설정하는 컬렉션 필드는,
 *      성능 최적화(카테시안 곱 방지)를 위해 생성자가 아닌 setter를 통해 별도로 초기화해야 합니다. </li>
 */
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class IngredientHierarchyDto {

    private Long id;

    private String name;

    private IngredientEssentialDto parent;

    private List<IngredientHierarchyDto> tree = new ArrayList<>();

    public IngredientHierarchyDto(
            Long id,
            String name,
            IngredientEssentialDto parent) {
        this.id = id;
        this.name = name;
        this.parent = parent;
    }

}
