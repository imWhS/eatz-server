package imwhs.eatz_server.repository.recipe.savedrecipe;

import imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto;
import imwhs.eatz_server.dto.recipe.RecipeDto;

import java.util.List;

public interface NSavedRecipeCustomRepository {

    /**
     * ID에 해당하는 사용자가 저장한 모든 레시피 목록을 조회합니다.
     * @param username 사용자 이름.
     * @return 레시피 목록.
     */
//    List<RecipeDto> findSavedRecipesByUsername(String username);

    /**
     * ID에 해당하는 레시피를 저장한 모든 사용자 목록을 조회합니다.
     * @param id 레시피 ID.
     * @return 사용자 목록.
     */
//    List<EatzUserBasicDto> findSavedUsersByRecipeId(Long id);

    /**
     * 저장된 레시피를 삭제합니다.
     * @param userId 사용자 ID.
     * @param id 저장된 레시피 ID.
     * @return 삭제 여부.
     */
    Boolean deleteSavedRecipe(Long userId, Long id);

    /**
     * 저장된 레시피를 삭제합니다.
     * @param username 사용자 이름.
     * @param id 저장된 레시피 ID.
     * @return 삭제 여부.
     */
    Boolean deleteSavedRecipe(String username, Long id);

}
