package imwhs.eatz_server.repository.savedRecipe;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.SavedRecipe;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * SavedRecipeQueryRepository 클래스입니다.<br/>
 * SavedRecipe 엔티티 관련 데이터를 조회하기 위한 쿼리 작업을 처리합니다.
 */
@Repository
public interface SavedRecipeQueryRepository extends JpaRepository<SavedRecipe, Long> {

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
     * 특정 사용자가 저장한 모든 레시피 목록을 저장한 날짜 기준 내림차순으로 조회합니다.
     * @param user 사용자 엔티티.
     * @return 저장한 모든 레시피 목록.
     */
    @Query("select s from SavedRecipe s where s.user = :user order by s.createdAt desc ")
    List<SavedRecipe> findByUserIdOrderByCreatedAtDesc(@Param("user") EatzUser user, Pageable pageable);

}
