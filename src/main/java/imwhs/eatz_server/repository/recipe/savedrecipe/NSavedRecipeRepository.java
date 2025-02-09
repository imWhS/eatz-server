package imwhs.eatz_server.repository.recipe.savedrecipe;

import imwhs.eatz_server.domain.recipe.NSavedRecipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NSavedRecipeRepository extends JpaRepository<NSavedRecipe, Long>, NSavedRecipeCustomRepository {

    /**
     * 사용자의 레시피 저장 여부를 조회합니다.
     * @param username 사용자 이름.
     * @param recipeId 레시피 ID.
     * @return 사용자의 레시피 저장 여부.
     */
    boolean existsByUserUsernameAndRecipeId(String username, Long recipeId);

    /**
     * 레시피의 저장 수를 조회합니다.
     * @param id 레시피 ID.
     * @return 저장 수.
     */
    long countByRecipeId(Long id);

}
