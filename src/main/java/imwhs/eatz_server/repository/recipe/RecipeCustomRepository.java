package imwhs.eatz_server.repository.recipe;

import imwhs.eatz_server.dto.recipe.NRecipeDto;
import imwhs.eatz_server.dto.recipe.NRecipeItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RecipeCustomRepository {

    Optional<NRecipeDto> findRecipeWithUser(Long id);

    Optional<NRecipeDto> findRecipeWithUserIngredientsCategories(Long id);

    Page<NRecipeItemDto> findAllRecipes(Long userId, Pageable pageable);

}
