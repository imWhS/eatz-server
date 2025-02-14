package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.NSavedRecipe;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto;
import imwhs.eatz_server.dto.recipe.RecipeBasicDto;
import imwhs.eatz_server.exception.DuplicatedSavedRecipeException;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.repository.recipe.savedrecipe.NSavedRecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class NSavedRecipeService {

    private final NSavedRecipeRepository savedRecipeRepository;

    private final EatzUserRepository userRepository;

    private final RecipeRepository recipeRepository;

    @Transactional
    public Long save(Long id, String username) {
        Recipe recipe = getRecipe(id);
        EatzUser user = getUser(username);

        if (savedRecipeRepository.existsByRecipeIdAndUserUsername(id, username)) {
            throw new DuplicatedSavedRecipeException(id, username);
        }

        NSavedRecipe savedRecipe = NSavedRecipe.of(recipe, user);
        savedRecipeRepository.save(savedRecipe);
        return savedRecipe.getId();
    }

    @Transactional
    public Boolean unsave(Long id, String username) {
        validateRecipe(id);
        return savedRecipeRepository.deleteSavedRecipe(username, id);
    }

    public List<RecipeBasicDto> getSavedRecipesByUser(Long id) {
        validateUser(id);
        return savedRecipeRepository.findRecipesByUserId(id);
    }

    public List<EatzUserBasicDto> getSavedUsersByRecipe(Long id) {
        validateRecipe(id);
        return savedRecipeRepository.findUsersByRecipeId(id);
    }

    public Long countSaveds(Long id) {
        validateRecipe(id);
        return savedRecipeRepository.countByRecipeId(id);
    }

    public boolean isRecipeSaved(Long id, String username) {
        validateRecipe(id);

        if (!userRepository.existsByUsername(username)) {
            throw new EatzUserNotFoundException(username);
        }

        return savedRecipeRepository.existsByRecipeIdAndUserUsername(id, username);
    }

    private EatzUser getUser(String username) {
        EatzUser user = userRepository.findByUsername(username).orElseThrow(
                () -> new EatzUserNotFoundException(username));
        return user;
    }

    private Recipe getRecipe(Long id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(
                () -> new RecipeNotFoundException(id));
        return recipe;
    }

    private void validateUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EatzUserNotFoundException(id);
        }
    }

    private void validateRecipe(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new RecipeNotFoundException(id);
        }
    }

}
