package imwhs.eatz_server.repository.recipe.kitchenware;

import imwhs.eatz_server.domain.recipe.RecipeKitchenware;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.recipe.kitchenware.KitchenwareRequirementDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RecipeKitchenware 엔티티의 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 */
@Repository
public interface RecipeKitchenwareRepository extends
        JpaRepository<RecipeKitchenware, Long>, RecipeKitchenwareQueryRepository {

    @Query("SELECT k.id " +
            "FROM RecipeKitchenware rk " +
            "JOIN rk.kitchenware k " +
            "WHERE " +
            "   k.deletedAt IS null AND " +
            "   rk.recipe = :recipe AND " +
            "   rk.deletedAt IS null")
    List<Long> findAllKitchenwareIdsByRecipe(@Param("recipe") Recipe recipe);

    @Query("SELECT k.id " +
            "FROM RecipeKitchenware rk " +
            "JOIN rk.kitchenware k " +
            "WHERE " +
            "   k.deletedAt IS null AND " +
            "   rk.recipe.id = :recipeId AND " +
            "   rk.deletedAt IS null")
    List<Long> findAllKitchenwareIdsByRecipeId(@Param("recipeId") Long recipeId);

    boolean existsByRecipeIdAndKitchenwareId(Long recipeId, Long kitchenwareId);

    @Query("SELECT kr " +
            "FROM RecipeKitchenware kr " +
            "JOIN FETCH kr.kitchenware " +
            "WHERE " +
            "   kr.recipe.id IN :ids AND " +
            "   kr.deletedAt IS null")
    List<RecipeKitchenware> findAllWithKitchenwareByRecipeIdsOld(@Param("ids") List<Long> ids);

    @Query("SELECT new imwhs.eatz_server.dto.recipe.kitchenware.KitchenwareRequirementDto(" +
            "   k.id, " +
            "   k.name," +
            "   k.imageUrl, " +
            "   (SELECT 0 < count(pk.id) " +
            "       FROM PantryKitchenware pk " +
            "       WHERE pk.user.id = :userId " +
            "           AND pk.kitchenware = k " +
            "           AND pk.deletedAt IS null)) " +
            "FROM RecipeKitchenware rk " +
            "JOIN rk.kitchenware k ON " +
            "   k.deletedAt IS null " +
            "WHERE rk.recipe.id = :recipeId AND " +
            "   rk.deletedAt IS null")
    List<KitchenwareRequirementDto> findAllRecipeKitchenwaresByRecipeIdAndUserId(
            @Param("recipeId") Long recipeId,
            @Param("userId") Long userId);

    @Query("SELECT new imwhs.eatz_server.dto.recipe.kitchenware.KitchenwareRequirementDto(" +
            "   k.id, " +
            "   k.name, " +
            "   k.imageUrl, " +
            "   false) " +
            "FROM RecipeKitchenware rk " +
            "JOIN rk.kitchenware k ON " +
            "   k.deletedAt IS null " +
            "WHERE rk.recipe.id = :id AND " +
            "   rk.deletedAt IS null ")
    List<KitchenwareRequirementDto> findAllRecipeKitchenwaresByRecipeId(@Param("id") Long id);

}
