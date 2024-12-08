package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.SavedRecipe;
import imwhs.eatz_server.dto.savedRecipe.SaveRecipeRequestDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.repository.savedRecipe.SavedRecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SavedRecipeService {

    private final SavedRecipeRepository savedRecipeRepository;

    private final RecipeRepository recipeRepository;

    private final EatzUserRepository userRepository;

    /**
     * 레시피를 저장합니다.
     * @param saveRecipeRequestDto 레시피를 저장하기 위해 필요한 정보를 담고 있는 DTO
     * @return
     */
    public Long saveRecipe(SaveRecipeRequestDto saveRecipeRequestDto) {
        Long recipeId = saveRecipeRequestDto.getRecipeId();
        Recipe recipe = getRecipe(recipeId);

        Long userId = saveRecipeRequestDto.getUserId();
        EatzUser user = getEatzUser(userId);

        LocalDate scheduledDate = saveRecipeRequestDto.getScheduledDate();

        // 레시피와 사용자 엔티티를 이용해, 이미 해당 사용자가 해당 레시피를 저장했는지 확인합니다.
        Optional<SavedRecipe> existingSavedRecipe = savedRecipeRepository
                .findByRecipeAndUser(recipe, user);
        if (existingSavedRecipe.isPresent()) {
            // 이미 해당 사용자가 해당 레시피를 저장했다면, 추가 레시피 저장 로직을 더 이살 실행하지 않고 기존의 저장된 레시피 ID를 반환합니다.
            return existingSavedRecipe.get().getId();
        }

        SavedRecipe savedRecipe = new SavedRecipe(recipe, user, scheduledDate);
        savedRecipeRepository.save(savedRecipe);

        return savedRecipe.getId();
    }

    /**
     * 레시피 ID와 사용자 ID로 저장된 레시피를 삭제합니다.<br/>
     * 즉, 레시피를 더 이상 저장해두지 않습니다.
     */
    public void deleteSavedRecipe(Long recipeId, Long userId) {
        Recipe recipe = getRecipe(recipeId);
        EatzUser user = getEatzUser(userId);

        savedRecipeRepository.deleteByRecipeAndUser(recipe, user);
    }

    private Recipe getRecipe(Long recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("id가 " + recipeId + "인 레시피를 찾지 못했습니다."));
    }

    private EatzUser getEatzUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EatzUserNotFoundException("id가 " + userId + "인 사용자를 찾지 못했습니다."));
    }

}
