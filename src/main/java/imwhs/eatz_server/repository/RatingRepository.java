package imwhs.eatz_server.repository;

import imwhs.eatz_server.domain.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    boolean existsByRecipeIdAndUserId(Long recipeId, Long userId);

    Optional<Rating> findByIdAndDeletedAtIsNull(Long id);

    @Query("select r from Rating r " +
            "join fetch r.user u " +
            "join fetch r.recipe rc " +
            "where r.id = :ratingId")
    Optional<Rating> findRatingJoinUserRecipeById(@Param("ratingId") Long id);

    @Query("select r from Rating r " +
            "join fetch r.user u " +
            "join fetch r.recipe rc " +
            "where r.user.id = :userId and r.recipe.id = :recipeId")
    Optional<Rating> findRatingJoinUserRecipeByUserIdAndRecipeId(
            @Param("userId") Long userId,
            @Param("recipeId") Long recipeId);

    @Query("select r from Rating r " +
            "join fetch r.user u " +
            "join fetch r.recipe rc " +
            "where rc.id = :recipeId")
    Page<Rating> findAllRatingsJoinUserRecipeByRecipeId(@Param("recipeId") Long recipeId, Pageable pageable);

}
