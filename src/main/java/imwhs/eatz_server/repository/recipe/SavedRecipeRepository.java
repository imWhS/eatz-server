package imwhs.eatz_server.repository.recipe;

import imwhs.eatz_server.domain.recipe.SavedRecipe;
import imwhs.eatz_server.dto.eatzuser.EatzUserEssentialDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * SavedRecipe 엔티티의 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 */
public interface SavedRecipeRepository extends JpaRepository<SavedRecipe, Long> {

    /**
     * 사용자의 레시피 저장 여부(연관 SavedRecipe 존재 여부)를 조회합니다.
     * @param recipeId 레시피 ID
     * @param username 사용자 이름
     * @return 사용자의 레시피 저장 여부
     */
    boolean existsByRecipeIdAndUserUsernameAndDeletedAtIsNull(Long recipeId, String username);

    /**
     * 레시피 ID와 사용자 ID에 해당하는 SavedRecipe를 조회합니다.
     * @param recipeId 레시피의 ID
     * @param userId 사용자의 ID
     * @return SavedRecipe 엔티티
     */
    Optional<SavedRecipe> findByRecipeIdAndUserIdAndDeletedAtIsNull(Long recipeId, Long userId);

    /**
     * 레시피의 저장 수(연관 SavedRecipe 수)를 조회 후 집계합니다.
     * @param id 레시피 ID
     * @return 레시피의 저장 수
     */
    long countByRecipeIdAndDeletedAtIsNull(Long id);

    /**
     * ID에 해당하는 레시피를 저장한 모든 사용자의 기본 정보 목록을 조회합니다.
     * @param id 레시피 ID
     * @return 사용자의 기본 정보 목록
     */
    @Query("SELECT new imwhs.eatz_server.dto.eatzuser.EatzUserEssentialDto(u.id, u.username, u.imageUrl) " +
            "FROM SavedRecipe sr " +
            "JOIN sr.user u " +
            "WHERE " +
            "   sr.recipe.id = :id AND " +
            "   sr.deletedAt IS null AND " +
            "   u.deletedAt IS null")
    List<EatzUserEssentialDto> findAllSavedUsersByRecipeId(@Param("id") Long id);

    /**
     * 저장된 레시피를 삭제합니다.
     * @param userId 사용자의 ID
     * @param recipeId 저장된 레시피의 ID
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM SavedRecipe sr " +
            "WHERE " +
            "   sr.user.id = :userId AND " +
            "   sr.recipe.id = :recipeId")
    void deleteByUserIdAndRecipeId(@Param("userId") Long userId, @Param("recipeId") Long recipeId);

}
