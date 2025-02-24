package imwhs.eatz_server.repository.recipe;

import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.recipe.RecipeItemDto;
import imwhs.eatz_server.dto.recipe.RecipeDto;
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

    @Query("select new imwhs.eatz_server.dto.recipe.RecipeItemDto(" +
            "r.id, r.title, r.imageUrl, r.createdAt, r.updatedAt, " +
            "new imwhs.eatz_server.dto.eatzuser.NEatzUserEssentialsDto(u.id, u.username, u.imageUrl)," +
            "(select case when count(l) > 0 then true else false end from Liked l where l.entityId = r.id and l.type = 'RECIPE' and l.user.id = :userId))" +
            "from Recipe r " +
            "join r.user u " +
            "where r.deletedAt is null")
    Page<RecipeItemDto> findAllItemsWithUser(@Param("userId") Long userId, Pageable pageable);

    Long countByUserIdAndDeletedAtIsNull(Long userId);

    /**
     * 모든 레시피 조회.
     * <p>
     *     모든 Recipe 엔티티를 페이징 적용해 조회합니다.
     *     삭제 처리된 레시피는 조회 대상에서 제외됩니다.
     * </p>
     * @param pageable 페이징 설정 정보.
     * @return 페이징 적용된 모든 Recipe 컬렉션.
     */
    @Query("select new imwhs.eatz_server.dto.recipe.RecipeDto(r, u, COUNT(l.id)) " +
            "from Recipe r " +
            "left join Liked l on r.id = l.entityId and l.type = 'RECIPE' " +
            "left join EatzUser u on r.user.id = u.id " +
            "where r.deletedAt IS NULL " +
            "group by r")
    Page<RecipeDto> findAllByDeletedAtIsNull(Pageable pageable);

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
    @Query("select new imwhs.eatz_server.dto.recipe.RecipeDto(r, u, COUNT(l.id)) " +
            "from Recipe r " +
            "left join Liked l on r.id = l.entityId and l.type = 'RECIPE' " +
            "left join EatzUser u on r.user.id = u.id " +
            "where r.user.id = :userId and r.deletedAt IS NULL " +
            "group by r")
    Page<RecipeDto> findAllByUserIdAndDeletedAtIsNull(@Param("userId") Long userId, Pageable pageable);


}
