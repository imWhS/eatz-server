package imwhs.eatz_server.repository.recipe.savedrecipe;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.domain.recipe.SavedRecipe;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * SavedRecipeRepository 클래스입니다.<br/>
 * SavedRecipe 엔티티의 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 */
@Repository
public interface SavedRecipeRepository extends JpaRepository<SavedRecipe, Long>, SavedRecipeCustomRepository {

    // 특정 회원이 특정 레시피에 대한 저장을 취소합니다.
    void deleteByRecipeAndUser(Recipe recipe, EatzUser user);


    @Query("select s from SavedRecipe s " +
            "left join fetch s.user u " +
            "left join fetch s.recipe r " +
            "where s.id = :id")
    Optional<SavedRecipe> findWithUserAndRecipeById(@Param("id") Long id);

    /**
     * 특정 레시피에 대한 특정 사용자의 저장된 레시피를 조회합니다.<br/>
     * Optional이 반환되지 않는다면, 해당 사용자가 해당 레시피를 저장했음을 나타냅니다.
     * @param recipe 레시피 엔티티.
     * @param user 사용자 엔티티.
     * @return Optional로 wrapping 된 저장된 레시피 인스턴스.
     */
    Optional<SavedRecipe> findByRecipeAndUser(Recipe recipe, EatzUser user);

    /**
     * 특정 사용자가 특정 날짜를 지정한 후 저장한 모든 레시피 목록을 날짜 기준 내림차순으로 조회합니다.
     */

    @Query("select sr from SavedRecipe sr left join fetch sr.schedules s where sr.user = :user and s.date = :date")
    List<SavedRecipe> findByUserAndScheduledDate(@Param("user") EatzUser user, @Param("date") LocalDate date);


}
