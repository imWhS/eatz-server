package imwhs.eatz_server.repository.rating;

import imwhs.eatz_server.domain.Rating;
import imwhs.eatz_server.dto.rating.*;
import imwhs.eatz_server.exception.CommentNotFoundException;
import imwhs.eatz_server.exception.RatingDuplicatedException;
import imwhs.eatz_server.exception.RatingNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Rating 엔티티의 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 */
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long>, RatingQueryRepository {

    default Rating get(Long id) {
        if (id == null) { throw new IllegalArgumentException("평가의 ID가 필요해요."); }
        return findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new RatingNotFoundException(id));
    }

    default Rating getWithRecipe(Long id) {
        if (id == null) { throw new IllegalArgumentException("평가의 ID가 필요해요."); }
        return findWithRecipeByRatingId(id).orElseThrow(() -> new RatingNotFoundException(id));
    }

    default void validateExists(Long id) {
        if (id == null) { throw new IllegalArgumentException("평가의 ID가 필요해요."); }
        if (existsByIdAndDeletedAtIsNull(id))  { throw new RatingNotFoundException(id); }
    }

    default void validateDuplicates(Long recipeId, Long userId) {
        if (recipeId == null) { throw new IllegalArgumentException("레시피의 ID가 필요해요."); }
        if (userId == null) { throw new IllegalArgumentException("사용자의 ID가 필요해요."); }
        if (existsByRecipeIdAndAuthorIdAndDeletedAtIsNull(recipeId, userId)) {
            throw new RatingDuplicatedException();
        }
    }

    Optional<Rating> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByRecipeIdAndAuthorIdAndDeletedAtIsNull(Long recipeId, Long authorId);

    boolean existsByIdAndDeletedAtIsNull(Long id);

    Optional<Rating> findByRecipeIdAndAuthorIdAndDeletedAtIsNull(Long recipeId, Long authorId);

    /**
     * 평가를 작성자(EatzUser)와 레시피 엔티티까지 함께 조회합니다.
     * @param id 평가의 ID
     * @return Rating 엔티티
     */
    @Query("SELECT r " +
            "FROM Rating r " +
            "JOIN FETCH r.author a " +
            "JOIN FETCH r.recipe rc " +
            "WHERE " +
            "   r.id = :id AND " +
            "   r.deletedAt IS null")
    Optional<Rating> findWithAuthorAndRecipe(@Param("id") Long id);

    /**
     * 평가를 레시피 엔티티와 함께 조회합니다.
     * @param id 평가의 ID
     * @return Rating 엔티티
     */
    @Query("SELECT r " +
            "FROM Rating r " +
            "JOIN FETCH r.recipe rr " +
            "WHERE " +
            "   r.id = :id AND " +
            "   r.deletedAt IS null")
    Optional<Rating> findWithRecipeByRatingId(@Param("id") Long id);

    /**
     * 레시피에 달린 모든 평가의 지표 정보를 조회합니다.
     * @param id 레시피의 ID
     * @return 레시피에 달린 모든 평가의 지표 정보
     */
    @Query("SELECT new imwhs.eatz_server.dto.rating.RatingIndicatorDto(" +
            "new imwhs.eatz_server.dto.rating.RatingIndicatorSummaryDto(" +
            "   COALESCE(AVG(r.score), 0.0), " + // avg가 null일 경우 0.0으로 대체합니다.
            "   COUNT(r)), " +
            "new imwhs.eatz_server.dto.rating.RatingIndicatorScoresDistributionDto(" +
                // SUM이 null일 경우 0L으로 대체합니다.
            "   COALESCE(SUM(CASE WHEN r.score = 5 THEN 1 ELSE 0 END), 0L), " +
            "   COALESCE(SUM(CASE WHEN r.score = 4 THEN 1 ELSE 0 END), 0L), " +
            "   COALESCE(SUM(CASE WHEN r.score = 3 THEN 1 ELSE 0 END), 0L), " +
            "   COALESCE(SUM(CASE WHEN r.score = 2 THEN 1 ELSE 0 END), 0L), " +
            "   COALESCE(SUM(CASE WHEN r.score = 1 THEN 1 ELSE 0 END), 0L))) " +
            "FROM Rating r " +
            "WHERE " +
            "   r.recipe.id = :id AND " +
            "   r.deletedAt IS null")
    RatingIndicatorDto findIndicatorByRecipeId(@Param("id") Long id);

    /**
     * 사용자(작성자)가 특정 레시피에 등록한 평가의 기본 정보를 조회합니다.
     * @param recipeId 레시피의 ID
     * @param authorId 평가 작성자의 ID
     * @return 작성자가 레시피에 등록한 평가의 기본 정보
     */
    @Query("""
    SELECT new imwhs.eatz_server.dto.rating.RatingBasicDto(
        r.id,
        new imwhs.eatz_server.dto.eatzuser.EatzUserEssentialDto(
            a.id, 
            a.username, 
            a.imageUrl),
        r.score,
        r.content,
        r.createdAt,
        r.updatedAt)
    FROM Rating r
    JOIN r.author a
    WHERE 
        r.recipe.id = :recipeId AND 
        r.author.id = :authorId AND 
        r.deletedAt IS null
    """)
    Optional<RatingBasicDto> findBasicByRecipeIdAndAuthorId(
            @Param("recipeId") Long recipeId,
            @Param("authorId") Long authorId);

    /**
     * 레시피에 달린 모든 평가의 기본 정보 목록을 조회합니다.
     * <ul>
     *     <li> 차단한 사용자가 작성한 평가는 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param id 레시피의 ID
     * @param pageable 페이징 정보
     * @return 모든 평가의 기본 정보 목록 및 페이징 정보
     */
    @Query("""
        SELECT new imwhs.eatz_server.dto.rating.RatingBasicDto(
            r.id,
            new imwhs.eatz_server.dto.eatzuser.EatzUserEssentialDto(
                a.id,
                a.username,
                a.imageUrl),
            r.score,
            r.content,
            r.createdAt,
            r.updatedAt)
        FROM Rating r
        JOIN r.author a
        WHERE 
            r.recipe.id = :id AND 
            r.deletedAt IS null
    """)
    Page<RatingBasicDto> findAllBasicsByRecipeIdOld(@Param("id") Long id, Pageable pageable);


    /**
     * 사용자가 작성한 모든 평가의 핵심 정보 목록을 조회합니다.
     * @param id 평가 작성자의 ID
     * @param pageable 페이징 정보
     * @return 작성자가 등록한 모든 평가의 핵심 정보 목록 및 페이징 정보
     */
    @Query("""
    SELECT new imwhs.eatz_server.dto.rating.RatingEssentialWithRecipeDto(
        r.id,
        new imwhs.eatz_server.dto.recipe.RecipeEssentialDto(
            re.id, 
            re.title, 
            re.imageUrl),
        r.score,
        r.content
    )
    FROM Rating r
    JOIN r.recipe re
    WHERE 
        r.author.id = :id AND 
        r.deletedAt IS null
    """)
    Page<RatingEssentialWithRecipeDto> findAllEssentialsWithRecipeByAuthorId(@Param("id") Long id, Pageable pageable);

}
