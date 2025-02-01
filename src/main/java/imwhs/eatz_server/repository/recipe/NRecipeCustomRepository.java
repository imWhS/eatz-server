package imwhs.eatz_server.repository.recipe;

import imwhs.eatz_server.dto.recipe.NRecipeDto;

import java.util.Optional;

public interface NRecipeCustomRepository {

    Optional<NRecipeDto> findRecipeById(Long id);

//    List<NRecipePreviewDto> findAllRecipes(Pageable pageable);

}
