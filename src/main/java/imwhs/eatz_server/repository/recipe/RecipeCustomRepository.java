package imwhs.eatz_server.repository.recipe;

import imwhs.eatz_server.domain.recipe.RecipeItemSortType;
import imwhs.eatz_server.dto.recipe.NRecipeDto;
import imwhs.eatz_server.dto.recipe.RecipeItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RecipeCustomRepository {

    Optional<NRecipeDto> findRecipeWithUser(Long id);

    Optional<NRecipeDto> findRecipeWithUserIngredientsCategories(Long id);

    Page<RecipeItemDto> searchRecipeItems(RecipeItemSortType sortType, Long currentUserId, Long categoryId, String title, List<Long> ingredientIds, List<Long> exactIngredientIds, Long authorId, Pageable pageable);

    Optional<NRecipeDto> findRecipeWithUserIngredients(Long id);

}
