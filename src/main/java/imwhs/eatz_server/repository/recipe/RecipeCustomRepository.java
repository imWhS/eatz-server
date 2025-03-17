package imwhs.eatz_server.repository.recipe;

import imwhs.eatz_server.domain.recipe.RecipeItemSortType;
import imwhs.eatz_server.dto.recipe.RecipeDto;
import imwhs.eatz_server.dto.recipe.RecipeItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RecipeCustomRepository {

    Optional<RecipeDto> findRecipeWithUser(Long id);

    Optional<RecipeDto> findRecipeWithUserIngredientsCategories(Long id);

    Page<RecipeItemDto> searchRecipeItems(RecipeItemSortType sortType, Long currentUserId, String keyword, Long categoryId, List<Long> ingredientIds, List<Long> requiredIngredientIds, Pageable pageable);

    Optional<RecipeDto> findRecipeWithUserIngredients(Long id);

}
