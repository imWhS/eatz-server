package imwhs.eatz_server.repository.ingredient;

import imwhs.eatz_server.domain.IngredientRecipe;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.ingredient.IngredientByRecipeDto;
import imwhs.eatz_server.dto.ingredient.IngredientDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRecipeRepository extends JpaRepository<IngredientRecipe, Long> {

    @Query("select ir from IngredientRecipe ir join fetch ir.ingredient where ir.recipe.id = :id")
    List<IngredientRecipe> findIngredientsByRecipeIdWithIngredient(@Param("id") Long id);

    @Query("select i.id from IngredientRecipe ir join ir.ingredient i where ir.recipe = :recipe")
    List<Long> findIngredientIdsByRecipe(@Param("recipe") Recipe recipe);

    boolean existsByRecipeIdAndIngredientId(Long recipeId, Long ingredientId);

    @Query("select new imwhs.eatz_server.dto.ingredient.IngredientByRecipeDto(" +
            "ir.recipe.id, ir.ingredient.id, ir.ingredient.name) " +
            "from IngredientRecipe ir " +
            "where ir.recipe.id in :recipeIds")
    List<IngredientByRecipeDto> findIngredientsByRecipeIds(@Param("recipeIds") List<Long> recipeIds);

    @Query("select new imwhs.eatz_server.dto.ingredient.IngredientDto(i.id, i.name)" +
            "from IngredientRecipe ir " +
            "join ir.ingredient i " +
            "where ir.recipe.id = :recipeId ")
    List<IngredientDto> findIngredientsByRecipe(@Param("recipeId") Long recipeId);

}
