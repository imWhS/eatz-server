package imwhs.eatz_server.repository.recipe.ingredient;

import imwhs.eatz_server.dto.ingredient.IngredientEssentialDto;
import imwhs.eatz_server.dto.recipe.ingredient.IngredientRequirementDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

public interface RecipeIngredientQueryRepository {

    /**
     * 레시피의 ID 목록에서 재료 ID 목록으로 필터링을 적용해서, 레시피 별로 부족한 재료 목록을 조회한 후 레시피 ID 별로 그룹핑합니다.
     * @param recipeIds 레시피의 ID 목록
     * @param ingredientIds 재료의 ID 목록
     * @return 레시피 ID가 key이며, 해당 레시피를 요리하기에 부족한 재료(IngredientEssentialDto) 목록을
     *         value로 갖는 Map. 레시피의 ID 목록이 null이거나 비어있으면 빈 Map을 반환합니다.
     */
    Map<Long, List<IngredientEssentialDto>> findAllMissingEssentials(
            List<Long> recipeIds,
            List<Long> ingredientIds
            );

    /**
     * 레시피의 ID 목록에서, 레시피 별 요리하기 위해 준비해야 할 재료 목록을 조회한 후 레시피 ID 별로 그룹핑합니다.
     * @param ids 레시피의 ID 목록
     * @return 레시피 ID가 key이며, 해당 레시피를 요리하기 위해 준비해야 할 재료(IngredientEssentialDto) 목록을
     *         value로 갖는 Map. 레시피의 ID 목록이 null이거나 비어있으면 빈 Map을 반환합니다.
     */
    Map<Long, List<IngredientEssentialDto>> findAllEssentialsByRecipeIds(List<Long> ids);

    /**
     * 레시피를 요리하기 위해 준비해야 할 재료 정보 목록을 조회합니다.
     * <ul>
     *     <li> 사용자의 ID를 전달받으면, 재료에 대한 해당 사용자의 context를 포함합니다. </li>
     * </ul>
     * @param id 레시피의 ID
     * @param userId 사용자의 ID
     * @return 레시피를 요리하기 위해 준비해야 할 재료 정보 목록
     */
    List<IngredientRequirementDto> findAllIngredientRequirementsByRecipeId(Long id, Long userId);

}
