package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.SavedRecipe;
import imwhs.eatz_server.dto.savedRecipe.SavedRecipeCreateDto;
import imwhs.eatz_server.dto.savedRecipe.SavedRecipeScheduledDateUpdateDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.exception.SavedRecipeNotFoundException;
import imwhs.eatz_server.exception.UnauthorizedAccessException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.repository.savedRecipe.SavedRecipeQueryRepository;
import imwhs.eatz_server.repository.savedRecipe.SavedRecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SavedRecipeService {

    private final SavedRecipeRepository savedRecipeRepository;

    private final RecipeRepository recipeRepository;

    private final EatzUserRepository userRepository;
    private final SavedRecipeQueryRepository savedRecipeQueryRepository;

    /**
     * 레시피를 저장합니다.
     * @param dto 레시피를 저장하기 위해 필요한 정보를 담고 있는 DTO
     * @return 저장된 레시피(SavedRecipe 엔티티) ID.
     */
    @Transactional
    public Long saveRecipe(SavedRecipeCreateDto dto) {
        Long recipeId = dto.getRecipeId();
        Recipe recipe = getRecipe(recipeId);

        Long userId = dto.getUserId();
        EatzUser user = getEatzUser(userId);

        LocalDate scheduledDate = dto.getScheduledDate();

        // 레시피와 사용자 엔티티를 이용해, 이미 해당 사용자가 해당 레시피를 저장했는지 확인합니다.
        Optional<SavedRecipe> existingSavedRecipe = savedRecipeQueryRepository
                .findByRecipeAndUser(recipe, user);
        if (existingSavedRecipe.isPresent()) {
            // 이미 해당 사용자가 해당 레시피를 저장했다면, 추가 레시피 저장 로직을 더 이상 실행하지 않고 기존의 저장된 레시피 ID를 반환합니다.
            return existingSavedRecipe.get().getId();
        }

        SavedRecipe savedRecipe = new SavedRecipe(recipe, user, scheduledDate);
        savedRecipeRepository.save(savedRecipe);

        return savedRecipe.getId();
    }

    /**
     * 저장된 레시피에 설정되어 있던 날짜를 업데이트합니다.
     * @param dto 저장된 레시피의 날짜를 변경하기 위해 필요한 정보를 담고 있는 DTO
     */
    @Transactional
    public void updateScheduledDate(SavedRecipeScheduledDateUpdateDto dto) {
        Long savedRecipeId = dto.getSavedRecipeId();
        SavedRecipe savedRecipe = savedRecipeQueryRepository.findWithUserAndRecipeById(savedRecipeId).orElseThrow(
                () -> new SavedRecipeNotFoundException("id " + savedRecipeId + "에 해당하는 저장된 레시피를 찾을 수 없습니다."));

        Long userId = dto.getUserId();
        EatzUser user = getEatzUser(userId);
        LocalDate scheduledDate = dto.getScheduledDate();

        // SavedRecipe를 생성한 사용자가 업데이트 요청을 했는지에 대한 유효성을 검증합니다.
        validateDeleteSavedRecipeAccessAuthorize(userId, savedRecipe);

        savedRecipe.updateScheduledDate(scheduledDate);
    }

    /**
     * 레시피 ID와 사용자 ID로 저장된 레시피(SavedRecipe 엔티티)를 삭제합니다.<br/>
     * 즉, 사용자가 해당 레시피를 더 이상 저장해두지 않습니다.
     * @param recipeId 더 이상 저장해두지 않으려고 하는 레시피 ID.
     * @param userId 저장된 레시피(SavedRecipe 엔티티)를 삭제 요청한 사용자 ID.
     */
    @Transactional
    public void deleteSavedRecipeByRecipeId(Long recipeId, Long userId) {
        Recipe recipe = getRecipe(recipeId);
        EatzUser user = getEatzUser(userId);

        SavedRecipe savedRecipe = savedRecipeQueryRepository.findByRecipeAndUser(recipe, user)
                .orElseThrow(() -> new SavedRecipeNotFoundException(
                        "올바르지 않은 요청입니다. " +
                                "ID가 " + userId + "인 사용자가 ID가 " + recipeId + " 인 레시피를 저장하지 않았습니다."));

        validateDeleteSavedRecipeAccessAuthorize(userId, savedRecipe);
        savedRecipeRepository.deleteByRecipeAndUser(recipe, user);
    }

    /**
     * 저장된 레시피(SavedRecipe 엔티티)를 삭제합니다.
     * @param savedRecipeId 저장된 레시피(SavedRecipe 엔티티) ID.
     * @param userId 저장된 레시피(SavedRecipe 엔티티)를 삭제 요청한 사용자 ID.
     */
    @Transactional
    public void deleteSavedRecipe(Long savedRecipeId, Long userId) {
        SavedRecipe savedRecipe = savedRecipeQueryRepository.findWithUserAndRecipeById(savedRecipeId).orElseThrow(
                () -> new SavedRecipeNotFoundException("id " + savedRecipeId + "에 해당하는 저장된 레시피를 찾을 수 없습니다."));
        EatzUser user = getEatzUser(userId);

        // SavedRecipe를 생성한 사용자가 삭제 요청을 했는지에 대한 유효성을 검증합니다.
        validateDeleteSavedRecipeAccessAuthorize(userId, savedRecipe);

        savedRecipeRepository.deleteById(savedRecipe.getId());
    }

    private void validateDeleteSavedRecipeAccessAuthorize(Long userId, SavedRecipe savedRecipe) {
        if (!Objects.equals(savedRecipe.getUser().getId(), userId)) {
            throw new UnauthorizedAccessException("레시피를 저장한 사용자가 아니어서, 권한이 없습니다.");
        }
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
