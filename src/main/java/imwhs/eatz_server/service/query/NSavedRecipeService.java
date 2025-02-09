package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.NSavedRecipe;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto;
import imwhs.eatz_server.dto.recipe.RecipeDto;
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
        Recipe recipe = recipeRepository.findById(id).orElseThrow(
                () -> new RecipeNotFoundException(id));

        EatzUser user = userRepository.findByUsername(username).orElseThrow(
                () -> new EatzUserNotFoundException(id));

        NSavedRecipe savedRecipe = NSavedRecipe.of(recipe, user);
        savedRecipeRepository.save(savedRecipe);
        return savedRecipe.getId();
    }

    @Transactional
    public Boolean unsave(Long id, String username) {
        if (!recipeRepository.existsById(id)) {
            throw new RecipeNotFoundException(id);
        }

        EatzUser user = userRepository.findByUsername(username).orElseThrow(
                () -> new EatzUserNotFoundException(id));

        return savedRecipeRepository.deleteSavedRecipe(user.getId(), id);
    }

    public List<RecipeDto> getSavedRecipesByUser(String username) {
        if (!userRepository.existsByUsername(username)) {
            throw new EatzUserNotFoundException(username);
        }

        return savedRecipeRepository.findSavedRecipesByUsername(username);
    }

    public List<EatzUserBasicDto> getSavedUsersByRecipeId(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new RecipeNotFoundException(id);
        }
        return savedRecipeRepository.findSavedUsersByRecipeId(id);
    }

    public Long countSaveds(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new RecipeNotFoundException(id);
        }

        long count = savedRecipeRepository.countByRecipeId(id);
        System.out.println("count = " + count);
        return count;
    }

    public boolean isRecipeSaved(Long id, String username) {
        if (!recipeRepository.existsById(id)) {
            throw new RecipeNotFoundException(id);
        }

        if (!userRepository.existsByUsername(username)) {
            throw new EatzUserNotFoundException(username);
        }

        return savedRecipeRepository.existsByUserUsernameAndRecipeId(username, id);
    }

}
