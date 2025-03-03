package imwhs.eatz_server.repository.recipe;

import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.recipe.RecipeByUserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 * RecipeRepository 클래스입니다.<br/>
 * Recipe 엔티티의 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>, RecipeCustomRepository {

    Long countByUserIdAndDeletedAtIsNull(Long userId);

    /**
     * 특정 사용자가 등록한 모든 레시피 조회.
     * <p>
     *     특정 EatzUser의 식별자로 모든 Recipe 엔티티를 페이징 적용해 조회합니다.
     *     삭제 처리된 레시피는 조회 대상에서 제외됩니다.
     * </p>
     * @param userId 사용자 식별자.
     * @param pageable 페이징 설정 정보.
     * @return 페이징 적용된 모든 Recipe 컬렉션.
     */
    @Query("select new imwhs.eatz_server.dto.recipe.RecipeByUserDto(" +
            "r.id, r.title, r.description, r.url, r.imageUrl, r.createdAt, r.updatedAt) " +
            "from Recipe r " +
            "where r.user.id = :userId and r.deletedAt IS NULL ")
    Page<RecipeByUserDto> findAllByUserIdAndDeletedAtIsNull(@Param("userId") Long userId, Pageable pageable);


}
