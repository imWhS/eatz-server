package imwhs.eatz_server.repository.pantry;

import imwhs.eatz_server.domain.pantry.PantryIngredient;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.repository.pantry.ingredient.PantryIngredientQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * PantryIngredient 엔티티의 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 */
@Repository
public interface PantryIngredientRepository
        extends JpaRepository<PantryIngredient, Long>, PantryIngredientQueryRepository {

    /**
     * 사용자가 보관함에 추가한 재료의 ID 목록을 조회합니다.
     * @param user 사용자의 EatzUser 엔티티
     * @return 사용자가 보관함에 추가한 재료의 ID 목록
     */
    @Query("SELECT i.id " +
            "FROM PantryIngredient pi " +
            "JOIN pi.ingredient i ON i.deletedAt IS null " +
            "WHERE " +
            "   pi.user = :user AND " +
            "   pi.deletedAt IS null")
    List<Long> findAllIngredientIdsByUser(@Param("user") EatzUser user);

    /**
     * 사용자가 보관함에 추가한 재료의 수를 집계합니다.
     * @param user 사용자의 EatzUser 엔티티
     * @return 사용자가 보관함에 추가한 재료의 수
     */
    @Query("SELECT count(pi) " +
            "FROM PantryIngredient pi " +
            "JOIN pi.ingredient i ON i.deletedAt IS null " +
            "WHERE " +
            "   pi.user = :user AND " +
            "   pi.deletedAt IS null ")
    long countIngredientUserByUser(@Param("user") EatzUser user);

//    /**
//     * 사용자가 보관함에 추가한 모든 재료의 기본 정보 목록을 조회합니다.
//     * @param user 사용자의 EatzUser 엔티티
//     * @param pageable 페이징 정보
//     * @return 모든 재료의 기본 정보 목록과 페이징 정보
//     */
//    @Query(value =
//            "SELECT new imwhs.eatz_server.dto.ingredient.IngredientBasicDto(" +
//                    "i.id, " +
//                    "i.name, " +
//                    "(CASE WHEN count(c.id) > 0 THEN true ELSE false END), " +
//                    "true, " +
//                    "(CASE WHEN count(l.id) > 0 THEN true ELSE false END)) " +
//            "FROM PantryIngredient pi " +
//            "JOIN pi.ingredient i ON " +
//                    "i.deletedAt IS null " +
//            "LEFT JOIN i.children c ON " +
//                    "c.deletedAt IS null " +
//            "LEFT JOIN LikedIngredient l ON " +
//                    "l.ingredient = i AND " +
//                    "l.isLiked = true AND " +
//                    "l.deletedAt IS null AND " +
//                    "l.user = :user " +
//            "WHERE pi.user = :user AND " +
//                    "pi.deletedAt IS null " +
//            "GROUP BY i.id, i.name",
//            countQuery =
//                    "SELECT count(pi) " +
//                    "FROM PantryIngredient pi " +
//                    "WHERE " +
//                            "pi.user = :user AND " +
//                            "pi.deletedAt IS null")
//    Page<IngredientBasicDto> findAllByUserOld(@Param("user") EatzUser user, Pageable pageable);

    /**
     * 특정 재료들을 사용자의 보관함에서 제거합니다.
     * @param user 사용자의 EatzUser 엔티티
     * @param ingredientIds 사용자의 보관함에서 제거할 재료 ID 목록
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM PantryIngredient pi " +
            "WHERE " +
            "pi.user = :user AND " +
            "pi.ingredient.id IN :ingredientIds")
    void deleteByUserAndIngredientIds(@Param("user") EatzUser user, @Param("ingredientIds") List<Long> ingredientIds);

    /**
     * 사용자의 보관함에서 모든 도구를 제거합니다.
     * @param user 사용자의 EatzUser 엔티티
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM PantryIngredient pi " +
            "WHERE pi.user = :user")
    void deleteAllByUser(EatzUser user);

}
