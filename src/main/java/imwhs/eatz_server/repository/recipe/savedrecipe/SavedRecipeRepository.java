package imwhs.eatz_server.repository.recipe.savedrecipe;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.domain.recipe.SavedRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * SavedRecipeRepository 클래스입니다.<br/>
 * SavedRecipe 엔티티의 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 */
@Repository
public interface SavedRecipeRepository extends JpaRepository<SavedRecipe, Long> {

    // 특정 회원이 특정 레시피에 대한 저장을 취소합니다.
    void deleteByRecipeAndUser(Recipe recipe, EatzUser user);

}
