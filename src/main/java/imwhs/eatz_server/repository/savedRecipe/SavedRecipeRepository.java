package imwhs.eatz_server.repository.savedRecipe;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.SavedRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * SavedRecipeRepository 클래스입니다.<br/>
 * SavedRecipe 엔티티의 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 */
@Repository
public interface SavedRecipeRepository extends JpaRepository<SavedRecipe, Long> {

    @Query("select s from SavedRecipe s " +
            "left join fetch s.user u " +
            "left join fetch s.recipe r " +
            "where s.id = :id")
    Optional<SavedRecipe> findWithUserAndRecipeById(@Param("id") Long id);

    // 특정 회원이 특정 레시피를 저장했는지에 대한 여부를 조회합니다.
    Optional<SavedRecipe> findByRecipeAndUser(Recipe recipe, EatzUser user);

    // 특정 회원이 특정 레시피에 대한 저장을 취소합니다.
    void deleteByRecipeAndUser(Recipe recipe, EatzUser user);

}
