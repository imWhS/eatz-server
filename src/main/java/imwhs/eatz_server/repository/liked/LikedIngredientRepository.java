package imwhs.eatz_server.repository.liked;

import imwhs.eatz_server.domain.liked.LikedIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * LikedIngredient 엔티티의 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 */
@Repository
public interface LikedIngredientRepository extends JpaRepository<LikedIngredient, Long> {

    @Query("SELECT l " +
            "FROM LikedIngredient l " +
            "JOIN l.ingredient r ON r.deletedAt IS null " +
            "JOIN l.user u ON u.deletedAt IS null " +
            "WHERE " +
            "   l.ingredient.id = :ingredientId AND " +
            "   l.user.id = :userId AND " +
            "   l.deletedAt IS null")
    Optional<LikedIngredient> findByUserIdAndIngredientId(
            @Param("userId") Long userId, @Param("ingredientId") Long ingredientId);

    /**
     * 사용자가 좋아하는 모든 재료 ID 목록을 조회합니다.
     * @param id 사용자의 ID
     * @return 재료 ID 목록
     */
    @Query("SELECT l.ingredient.id " +
            "FROM LikedIngredient l " +
            "INNER JOIN l.ingredient i ON i.deletedAt IS null " +
            "INNER JOIN l.user u ON u.deletedAt IS null " +
            "WHERE " +
            "   l.isLiked = true AND " +
            "   l.user.id = :id AND " +
            "   l.deletedAt IS null")
    Set<Long> findAllIngredientIdsByUserIdAndIsLikedIsTrue(@Param("id") Long id);

    /**
     * 재료의 좋아요 수를 조회 후 집계합니다.
     * <p>
     *     탈퇴한 사용자가 표시한 좋아요도 집계 대상에 포함합니다.
     * </p>
     * @param id 재료의 ID
     * @return 좋아요 수
     */
    @Query("SELECT count(l)" +
            "FROM LikedIngredient l " +
            "INNER JOIN l.ingredient i ON " +
            "   i.id = :id AND " +
            "   i.deletedAt IS null " +
            "WHERE " +
            "   l.isLiked = true AND " +
            "   l.deletedAt IS null") // 좋아요 한 사용자 중 탈퇴한 사용자 좋아요 수도 포함 여부 결정 필요
    long countAllByIngredientIdAndIsLikedIsTrue(@Param("id") Long id);

    /**
     * 사용자가 좋아하는 재료 수를 조회 후 집계합니다.
     * @param id 사용자의 ID
     * @return 재료 수
     */
    @Query("SELECT count(l)" +
            "FROM LikedIngredient l " +
            "INNER JOIN l.ingredient i ON i.deletedAt IS null " +
            "INNER JOIN l.user u ON u.id = :id AND u.deletedAt IS null " +
            "WHERE " +
            "   l.isLiked = true AND " +
            "   l.deletedAt IS null")
    long countAllByUserIdAndIsLikedIsTrue(@Param("id") Long id);

}
