package imwhs.eatz_server.repository.recipe.ingredient;

import imwhs.eatz_server.domain.recipe.RecipeIngredient;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.recipe.ingredient.IngredientRequirementDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RecipeIngredient 엔티티의 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 */
@Repository
public interface RecipeIngredientRepository extends
        JpaRepository<RecipeIngredient, Long>, RecipeIngredientQueryRepository {

    @Query("SELECT i.id " +
            "FROM RecipeIngredient ri " +
            "JOIN ri.ingredient i " +
            "WHERE " +
            "   i.deletedAt IS null AND " +
            "   ri.recipe = :recipe AND " +
            "   ri.deletedAt IS null")
    List<Long> findAllIngredientIdsByRecipe(@Param("recipe") Recipe recipe);

    @Query("SELECT i.id " +
            "FROM RecipeIngredient ri " +
            "JOIN ri.ingredient i " +
            "WHERE " +
            "   i.deletedAt IS null AND " +
            "   ri.recipe.id = :recipeId AND " +
            "   ri.deletedAt IS null")
    List<Long> findAllIngredientIdsByRecipeId(@Param("recipeId") Long recipeId);

    boolean existsByRecipeIdAndIngredientId(Long recipeId, Long ingredientId);

    /**
     * 레시피 ID 목록에서 레시피 별로 준비해야 할 재료 목록을 조회합니다.
     * @param ids
     * @return
     */
    @Query("SELECT ri " +
            "FROM RecipeIngredient ri " +
            "JOIN fetch ri.ingredient " +
            "WHERE " +
            "   ri.recipe.id IN :ids AND " +
            "   ri.deletedAt IS null")
    List<RecipeIngredient> findAllByRecipeIdsOld(@Param("ids") List<Long> ids);

    @Query("SELECT new imwhs.eatz_server.dto.recipe.ingredient.IngredientRequirementDto(" +
            "   i.id, " +
            "   i.name, " +
            "   (SELECT 0 < count(pi.id) " +
            "       FROM PantryIngredient pi " +
            "       WHERE pi.user.id = :userId " +
            "           AND pi.ingredient = i " +
            "           AND pi.deletedAt IS null), " +
            "   (SELECT 0 < count(l.id) " +
            "       FROM LikedIngredient l " +
            "       WHERE l.user.id = :userId " +
            "           AND l.ingredient = i " +
            "           AND l.isLiked = true " +
            "           AND l.deletedAt IS null)) " +
            "FROM RecipeIngredient ir " +
            "JOIN ir.ingredient i " +
            "WHERE " +
            "   ir.recipe.id = :recipeId AND " +
            "   ir.deletedAt IS null")
    List<IngredientRequirementDto> findAllRecipeIngredientsByRecipeIdAndUserId(
            @Param("recipeId") Long recipeId,
            @Param("userId") Long userId);

    @Query("SELECT new imwhs.eatz_server.dto.recipe.ingredient.IngredientRequirementDto(" +
            "   i.id, " +
            "   i.name, " +
            "   false, " +
            "   false)" +
            "FROM RecipeIngredient ri " +
            "JOIN ri.ingredient i " +
            "WHERE " +
            "   ri.recipe.id = :id AND " +
            "   ri.deletedAt IS null")
    List<IngredientRequirementDto> findAllRecipeIngredientsByRecipeId(@Param("id") Long id);

}
