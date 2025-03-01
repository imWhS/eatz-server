package imwhs.eatz_server.repository.rating;

import imwhs.eatz_server.domain.recipe.Rating;
import imwhs.eatz_server.dto.rating.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long>, RatingCustomRepository {

    boolean existsByRecipeIdAndUserUsername(Long recipeId, String username);

    Optional<Rating> findByIdAndDeletedAtIsNull(Long id);

    @Query("select r from Rating r " +
            "join fetch r.user u " +
            "join fetch r.recipe rc " +
            "where r.id = :ratingId")
    Optional<Rating> findJoinUserRecipeById(@Param("ratingId") Long id);

    @Query("select new imwhs.eatz_server.dto.rating.RatingsSummaryDistributionDto(" +
            "new imwhs.eatz_server.dto.rating.RatingSummaryDto(count(r), avg(r.score))," +
            "SUM(CASE WHEN r.score = 5 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN r.score = 4 THEN 1 ELSE 0 END)," +
            "SUM(CASE WHEN r.score = 3 THEN 1 ELSE 0 END)," +
            "SUM(CASE WHEN r.score = 2 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN r.score = 1 THEN 1 ELSE 0 END)" +
            ") " +
            "from Rating r " +
            "where r.recipe.id = :recipeId and r.deletedAt is null")
    RatingsSummaryDistributionDto findAverageScoreByRecipeId(@Param("recipeId") Long recipeId);

    @Query("select r from Rating r " +
            "join fetch r.user u " +
            "join fetch r.recipe rc " +
            "where r.user.id = :userId and r.recipe.id = :recipeId")
    Optional<Rating> findJoinUserRecipeByUserIdAndRecipeId(@Param("userId") Long userId, @Param("recipeId") Long recipeId);

    @Query("select r from Rating r " +
            "join fetch r.user u " +
            "join fetch r.recipe rc " +
            "where rc.id = :recipeId")
    Page<Rating> findJoinUserRecipeByRecipeId(@Param("recipeId") Long recipeId, Pageable pageable);

    @Query("select r from Rating r " +
            "join fetch r.user u " +
            "join fetch r.recipe rc " +
            "where u.id = :userId")
    Page<Rating> findJoinUserRecipeByUserId(@Param("userId") Long userId, Pageable pageRequest);

    @Query("select new imwhs.eatz_server.dto.rating.RatingSummaryDto(COUNT(r), AVG(r.score)) " +
            "from Rating r " +
            "where r.recipe.id = :recipeId")
    RatingSummaryDto findRatingSummaryByRecipeId(@Param("recipeId") Long recipeId);

    // 레시피 별 평가 항목 조회
    @Query("select new imwhs.eatz_server.dto.rating.RatingSummaryByRecipeDto(r.recipe.id, COUNT(r), AVG(r.score)) " +
            "from Rating r " +
            "where r.recipe.id in :recipeIds " +
            "group by r.recipe.id")
    List<RatingSummaryByRecipeDto> findRatingSummariesByRecipeIds(@Param("recipeIds") List<Long> recipeIds);

    @Query("""
    select new imwhs.eatz_server.dto.rating.RatingItemDto(
        r.id,
        new imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto(u.id, u.username, u.email, u.imageUrl),
        r.score,
        r.content,
        r.createdAt,
        r.updatedAt
    )
    from Rating r
    join r.user u
    where r.recipe.id = :recipeId and r.deletedAt is null
    """)
    Page<RatingItemDto> findWithUserByRecipeId(@Param("recipeId") Long recipeId, Pageable pageable);

    @Query("""
    select new imwhs.eatz_server.dto.rating.RatingWithRecipeResponseDto(
        r.id,
        new imwhs.eatz_server.dto.recipe.RecipeBasicDto(re.id, re.title, re.imageUrl),
        r.score,
        r.content
    )
    from Rating r
    join r.recipe re
    where r.user.id = :id and r.deletedAt is null
    """)
    Page<RatingWithRecipeResponseDto> findWithRecipeByUserId(@Param("id") Long id, Pageable pageable);

}
