package imwhs.eatz_server.service.recipe;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.SavedRecipe;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.recipe.SavedRecipeCreationInfoResponse;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.repository.recipe.SavedRecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SavedRecipeService {

    private final SavedRecipeRepository savedRecipeRepository;
    private final EatzUserRepository userRepository;
    private final RecipeRepository recipeRepository;

    @Transactional(rollbackFor = Exception.class)
    public SavedRecipeCreationInfoResponse save(Long id, Long userId) {
        Recipe recipe = recipeRepository.getReference(id);
        EatzUser user = userRepository.getReference(userId);

        Optional<SavedRecipe> savedRecipeOptional = savedRecipeRepository.findByRecipeIdAndUserIdAndDeletedAtIsNull(id, userId);
        SavedRecipe savedRecipe = savedRecipeOptional.orElseGet(() -> {
            SavedRecipe savedRecipeNew = SavedRecipe.create(recipe, user);
            return savedRecipeRepository.save(savedRecipeNew);
        });

        return new SavedRecipeCreationInfoResponse(savedRecipe);
    }

    @Transactional(rollbackFor = Exception.class)
    public void unsave(Long id, Long userId) {
        recipeRepository.validateExists(id);
        savedRecipeRepository.deleteByUserIdAndRecipeId(userId, id);
    }

}
