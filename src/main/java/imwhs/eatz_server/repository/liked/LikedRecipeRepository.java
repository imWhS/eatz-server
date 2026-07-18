package imwhs.eatz_server.repository.liked;

import imwhs.eatz_server.domain.liked.LikedRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * LikedRecipe 엔티티의 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 */
@Repository
public interface LikedRecipeRepository extends JpaRepository<LikedRecipe, Long> {

    @Query("SELECT l " +
            "FROM LikedRecipe l " +
            "JOIN l.recipe r ON r.deletedAt IS null " +
            "JOIN l.user u ON u.deletedAt IS null " +
            "WHERE " +
            "   l.recipe.id = :recipeId AND " +
            "   l.user.id = :userId AND " +
            "   l.deletedAt IS null ")
    Optional<LikedRecipe> findByUserIdAndRecipeId(@Param("userId") Long userId, @Param("recipeId") Long recipeId);

    /**
     * 사용자가 좋아하는 모든 레시피 ID 목록을 조회합니다.
     * <p> 삭제 처리된 레시피는 조회 대상에서 제외합니다. </p>
     * @param id 사용자의 ID
     * @return 레시피 ID 목록
     */
    @Query("SELECT r.id " +
            "FROM LikedRecipe l " +
            "INNER JOIN l.recipe r ON r.deletedAt IS null " +
            "INNER JOIN l.user u ON u.deletedAt IS null " +
            "WHERE " +
            "   u.id = :id AND " +
            "   l.isLiked = true AND " +
            "   l.deletedAt IS null ")
    Set<Long> findAllRecipeIdsByUserIdAndIsLikedIsTrue(@Param("id") Long id);

    /**
     * 레시피의 좋아요 수를 조회 후 집계합니다.
     * <p>
     *     탈퇴한 사용자가 표시한 좋아요도 집계 대상에 포함합니다.
     * </p>
     * @param id 레시피의 ID
     * @return 좋아요 수
     */
    @Query("SELECT count(l)" +
            "FROM LikedRecipe l " +
            "INNER JOIN l.recipe r ON r.deletedAt IS null " +
            "WHERE " +
            "   r.id = :id AND " +
            "   l.isLiked = true AND " +
            "   l.deletedAt IS null ") // TODO: 좋아요 한 사용자 중 탈퇴한 사용자 좋아요 수도 포함 여부 결정 필요
    long countAllByRecipeIdAndIsLikedIsTrue(@Param("id") Long id);

    /**
     * 사용자가 좋아하는 레시피 수를 조회 후 집계합니다.
     * @param id 사용자의 ID
     * @return 레시피 수
     */
    @Query("SELECT count(l)" +
            "FROM LikedRecipe l " +
            "INNER JOIN l.recipe r ON r.deletedAt IS null " +
            "INNER JOIN l.user u ON u.deletedAt IS null " +
            "WHERE " +
            "   u.id = :id AND " +
            "   l.isLiked = true AND " +
            "   l.deletedAt IS null AND " +
            "   r.deletedAt IS null AND " +
            "   u.deletedAt IS null")
    long countAllByUserIdAndIsLikedIsTrue(@Param("id") Long id);

}
