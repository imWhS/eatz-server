package imwhs.eatz_server.repository;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * EatzUserRepository 리포지토리.<br/>
 * <p>
 * EatzUser 엔티티에 대해 CRUD를 포함한 데이터 처리 작업을 수행합니다.
 */
@Repository
public interface EatzUserRepository extends JpaRepository<EatzUser, Long> {

    /**
     * 사용자 이름으로 엔티티 조회.
     * <p>
     * 사용자 이름으로 단일 EatzUser 엔티티를 조회합니다.
     * 조회할 엔티티가 없으면 Optional을 반환합니다.
     *
     * @param username 조회할 사용자 이름.
     * @return 사용자 이름에 해당하는 EatzUser 엔티티(Optional).
     */
    Optional<EatzUser> findByUsername(String username);

    /**
     * 레시피 ID로 엔티티 조회.
     * <p>
     * 연관 관계가 있는 Recipe의 ID로 단일 EatzUser 엔티티를 조회합니다.
     * 조회할 엔티티가 없으면 Optional을 반환합니다.
     *
     * @param recipeId 연관 관계가 있는 Recipe의 ID.
     * @return Recipe 엔티티와 연관 관계가 있는 EatzUser 엔티티(Optional).
     */
    @Query("SELECT u FROM Recipe r JOIN r.user u JOIN FETCH u.recipeList WHERE r.id = :recipeId")
    Optional<EatzUser> findByRecipeId(@Param("recipeId") Long recipeId);

}
