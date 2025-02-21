package imwhs.eatz_server.repository.recipe;

import imwhs.eatz_server.dto.recipe.NRecipeDto;

import java.util.Optional;

public interface NRecipeCustomRepository {

    Optional<NRecipeDto> findRecipeWithUserIngredientsCategories(Long id);

    Optional<NRecipeDto> findRecipeWithUser(Long id);

//    List<NRecipePreviewDto> findAllRecipes(Pageable pageable);

}
