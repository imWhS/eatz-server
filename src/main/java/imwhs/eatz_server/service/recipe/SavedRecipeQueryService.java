package imwhs.eatz_server.service.recipe;

import imwhs.eatz_server.dto.CountResponse;
import imwhs.eatz_server.dto.eatzuser.EatzUserEssentialDto;
import imwhs.eatz_server.dto.recipe.RecipeBasicDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.repository.recipe.SavedRecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SavedRecipeQueryService {

    private final SavedRecipeRepository savedRecipeRepository;
    private final RecipeRepository recipeRepository;
    private final EatzUserRepository userRepository;

    public Page<RecipeBasicDto> getSavedRecipesByUserId(Long id, Pageable pageable) {
        userRepository.validateExists(id);
        return recipeRepository.findAllSavedRecipeBasicsByUserId(id, pageable);
    }

    public List<EatzUserEssentialDto> getSavedUserEssentialsByRecipe(Long id) {
        recipeRepository.validateExists(id);
        return savedRecipeRepository.findAllSavedUsersByRecipeId(id);
    }

    public CountResponse countSaveds(Long id) {
        recipeRepository.validateExists(id);
        long saveds = savedRecipeRepository.countByRecipeIdAndDeletedAtIsNull(id);
        return new CountResponse(saveds);
    }

    public boolean isRecipeSaved(Long id, String username) {
        recipeRepository.validateExists(id);

        if (!userRepository.existsByUsernameAndDeletedAtIsNull(username)) {
            throw new EatzUserNotFoundException(username);
        }

        return savedRecipeRepository.existsByRecipeIdAndUserUsernameAndDeletedAtIsNull(id, username);
    }

}
