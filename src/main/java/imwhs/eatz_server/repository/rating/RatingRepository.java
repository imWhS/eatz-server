package imwhs.eatz_server.repository.rating;

import imwhs.eatz_server.domain.recipe.Rating;
import imwhs.eatz_server.dto.rating.RatingRecipeDto;
import imwhs.eatz_server.dto.rating.RatingSummaryByRecipeDto;
import imwhs.eatz_server.dto.rating.RatingSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    boolean existsByRecipeIdAndUserUsername(Long recipeId, String username);

    Optional<Rating> findByIdAndDeletedAtIsNull(Long id);

    @Query("select r from Rating r " +
            "join fetch r.user u " +
            "join fetch r.recipe rc " +
            "where r.id = :ratingId")
    Optional<Rating> findJoinUserRecipeById(@Param("ratingId") Long id);

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
            "where r.recipe.id in :recipeId")
    List<RatingSummaryByRecipeDto> findRatingSummariesByRecipeIds(@Param("recipeId") List<Long> recipeId);

}
