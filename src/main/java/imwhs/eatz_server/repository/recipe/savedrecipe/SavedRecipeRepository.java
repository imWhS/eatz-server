package imwhs.eatz_server.repository.recipe.savedrecipe;

import imwhs.eatz_server.domain.recipe.SavedRecipe;
import imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto;
import imwhs.eatz_server.dto.recipe.RecipeBasicDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SavedRecipeRepository extends JpaRepository<SavedRecipe, Long>, SavedRecipeCustomRepository {

    /**
     * 사용자의 레시피 저장 여부를 조회합니다.
     * @param recipeId 레시피 ID.
     * @param username 사용자 이름.
     * @return 사용자의 레시피 저장 여부.
     */
    boolean existsByRecipeIdAndUserUsername(Long recipeId, String username);

    /**
     * 레시피의 저장 수를 조회합니다.
     * @param id 레시피 ID.
     * @return 저장 수.
     */
    long countByRecipeId(Long id);

    @Query("select new imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto(u.id, u.username, u.email, u.imageUrl) " +
            "from SavedRecipe sr join sr.user u " +
            "where sr.recipe.id = :id")
    List<EatzUserBasicDto> findUsersByRecipeId(@Param("id") Long id);

    @Query("select new imwhs.eatz_server.dto.recipe.RecipeBasicDto(r.id, r.title, r.imageUrl) " +
            "from SavedRecipe sr " +
            "join sr.recipe r " +
            "join sr.user " +
            "where sr.user.username = :username")
    List<RecipeBasicDto> findRecipesByUserUsername(@Param("username") String username);

}
