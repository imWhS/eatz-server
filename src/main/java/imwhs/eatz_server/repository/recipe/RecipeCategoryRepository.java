package imwhs.eatz_server.repository.recipe;

import imwhs.eatz_server.domain.recipe.RecipeCategory;
import imwhs.eatz_server.dto.recipe.CategoryByRecipeDto;
import imwhs.eatz_server.dto.recipe.CategoryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeCategoryRepository extends JpaRepository<RecipeCategory, Long> {

    @Query("select new imwhs.eatz_server.dto.recipe.CategoryByRecipeDto(rc.recipe.id, rc.category.id, rc.category.name) " +
            "from RecipeCategory rc " +
            "where rc.recipe.id in :recipeIds")
    List<CategoryByRecipeDto> findCategoriesByRecipeIds(@Param("recipeIds") List<Long> recipeIds);

    @Query("select new imwhs.eatz_server.dto.recipe.CategoryDto(c.id, c.name) " +
            "from RecipeCategory rc " +
            "join rc.category c " +
            "where rc.recipe.id = :recipeId")
    List<CategoryDto> findCategoriesByRecipe(@Param("recipeId") Long recipeId);

}
