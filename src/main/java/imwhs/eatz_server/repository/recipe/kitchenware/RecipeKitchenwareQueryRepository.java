package imwhs.eatz_server.repository.recipe.kitchenware;

import imwhs.eatz_server.dto.kitchenware.KitchenwareEssentialDto;
import imwhs.eatz_server.dto.recipe.kitchenware.KitchenwareRequirementDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

public interface RecipeKitchenwareQueryRepository {

    /**
     * 레시피의 ID 목록에서 도구 ID 목록으로 필터링을 적용해서, 레시피 별로 부족한 도구 목록을 조회한 후 레시피 ID 별로 그룹핑합니다.
     * @param recipeIds 레시피의 ID 목록
     * @param kitchenwareIds 도구의 ID 목록
     * @return 레시피 ID가 key이며, 해당 레시피를 요리하기에 부족한 도구(KitchenwareEssentialDto) 목록을
     *         value로 갖는 Map 컬렉션. 레시피의 ID 목록이 null이거나 비어있으면, 빈 Map 컬렉션을 반환합니다.
     */
    Map<Long, List<KitchenwareEssentialDto>> findAllMissingEssentials(
            List<Long> recipeIds,
            List<Long> kitchenwareIds
    );

    /**
     * 레시피의 ID 목록에서, 레시피 별 요리하기 위해 준비해야 할 도구 목록을 조회한 후 레시피 ID 별로 그룹핑합니다.
     * @param ids 레시피의 ID 목록
     * @return 레시피 ID가 key이며, 해당 레시피의 요구 도구(KitchenwareEssentialDto) 목록을
     *         value로 갖는 Map 컬렉션. 레시피의 ID 목록이 null이거나 비어있으면, 빈 Map을 반환합니다.
     */
    Map<Long, List<KitchenwareEssentialDto>> findAllEssentialsByRecipeIds(List<Long> ids);

    /**
     * 레시피의 요구 도구 정보 목록을 조회합니다.
     * <ul>
     *     <li> 사용자의 ID를 전달받으면, 도구에 대한 해당 사용자의 context를 포함합니다. </li>
     * </ul>
     * @param id 레시피의 ID
     * @param userId 사용자의 ID
     * @return 레시피의 요구 도구 정보 목록
     */
    List<KitchenwareRequirementDto> findAllKitchenwareRequirementsByRecipeId(Long id, Long userId);

}
