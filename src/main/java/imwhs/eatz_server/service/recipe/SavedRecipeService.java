package imwhs.eatz_server.service.recipe;

import imwhs.eatz_server.auth.EatzUserAuthUtil;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.domain.recipe.SavedRecipe;
import imwhs.eatz_server.dto.recipe.savedrecipe.SavedRecipeScheduledDateUpdateDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.exception.SavedRecipeNotFoundException;
import imwhs.eatz_server.exception.UnauthorizedAccessException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.repository.recipe.savedrecipe.SavedRecipeCustomRepositoryImpl;
import imwhs.eatz_server.repository.recipe.savedrecipe.SavedRecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SavedRecipeService {

    private final SavedRecipeRepository savedRecipeRepository;

    private final RecipeRepository recipeRepository;

    private final EatzUserRepository userRepository;

    private final SavedRecipeCustomRepositoryImpl savedRecipeCustomRepositoryImpl;

    /**
     * 레시피를 저장합니다.
     * @param recipeId 저장하려는 레시피 ID.
     * @param schedules 일정 정보.
     * @return 저장된 레시피 ID.
     */
    @Transactional
    public Long saveRecipe(Long recipeId, List<LocalDate> schedules) {
        EatzUser user = getUser();
        Recipe recipe = getRecipe(recipeId);

        // 레시피와 사용자 엔티티를 이용해, 이미 해당 사용자가 해당 레시피를 저장했는지 확인합니다.
        Optional<SavedRecipe> existingSavedRecipe = savedRecipeRepository.findByRecipeAndUser(recipe, user);
        if (existingSavedRecipe.isPresent()) {
            // 이미 해당 사용자가 해당 레시피를 저장했다면, 추가 레시피 저장 로직을 더 이상 실행하지 않고 기존의 저장된 레시피 ID를 반환합니다.
            return existingSavedRecipe.get().getId();
        }

        SavedRecipe savedRecipe = new SavedRecipe(recipe, user, schedules);
        savedRecipeRepository.save(savedRecipe);

        return savedRecipe.getId();
    }

    /**
     * 저장된 레시피에 설정되어 있던 날짜를 업데이트합니다.
     * @param dto 저장된 레시피의 날짜를 변경하기 위해 필요한 정보를 담고 있는 DTO
     */
    @Transactional
    public void updateScheduledDate(SavedRecipeScheduledDateUpdateDto dto) {
        EatzUser user = getUser();
        Long savedRecipeId = dto.getSavedRecipeId();

        SavedRecipe savedRecipe = savedRecipeRepository.findWithUserAndRecipeById(savedRecipeId).orElseThrow(
                () -> new SavedRecipeNotFoundException("id " + savedRecipeId + "에 해당하는 저장된 레시피를 찾을 수 없습니다."));

        // SavedRecipe를 생성한 사용자가 업데이트 요청을 했는지에 대한 유효성을 검증합니다.
        validateDeleteSavedRecipeAccessAuthorize(user, savedRecipe);

        LocalDate scheduledDate = dto.getScheduledDate();

//        savedRecipe.updateScheduledDate(scheduledDate);
    }

    /**
     * 레시피 ID와 사용자 ID로 저장된 레시피(SavedRecipe 엔티티)를 삭제합니다.<br/>
     * 즉, 사용자가 해당 레시피를 더 이상 저장해두지 않습니다.
     * @param recipeId 더 이상 저장해두지 않으려고 하는 레시피 ID.
     * @param userId 저장된 레시피(SavedRecipe 엔티티)를 삭제 요청한 사용자 ID.
     */
    @Transactional
    public void deleteSavedRecipeByRecipeId(Long recipeId, Long userId) {
        EatzUser user = getUser();

        Recipe recipe = getRecipe(recipeId);

        SavedRecipe savedRecipe = savedRecipeRepository.findByRecipeAndUser(recipe, user)
                .orElseThrow(() -> new SavedRecipeNotFoundException(
                        "올바르지 않은 요청입니다. " +
                                "ID가 " + userId + "인 사용자가 ID가 " + recipeId + " 인 레시피를 저장하지 않았습니다."));

        validateDeleteSavedRecipeAccessAuthorize(user, savedRecipe);
        savedRecipeRepository.deleteByRecipeAndUser(recipe, user);
    }

    /**
     * 저장된 레시피(SavedRecipe 엔티티)를 삭제합니다.
     * @param savedRecipeId 저장된 레시피(SavedRecipe 엔티티) ID.
     * @param userId 저장된 레시피(SavedRecipe 엔티티)를 삭제 요청한 사용자 ID.
     */
    @Transactional
    public void deleteSavedRecipe(Long savedRecipeId, Long userId) {
        EatzUser user = getUser();
        SavedRecipe savedRecipe = savedRecipeRepository.findWithUserAndRecipeById(savedRecipeId).orElseThrow(
                () -> new SavedRecipeNotFoundException("id " + savedRecipeId + "에 해당하는 저장된 레시피를 찾을 수 없습니다."));

        // SavedRecipe를 생성한 사용자가 삭제 요청을 했는지에 대한 유효성을 검증합니다.
        validateDeleteSavedRecipeAccessAuthorize(user, savedRecipe);

        savedRecipeRepository.deleteById(savedRecipe.getId());
    }

    private EatzUser getUser() {
        String username = EatzUserAuthUtil.getUsername();
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EatzUserNotFoundException("사용자 이름 '" + username + "'에 해당하는 사용자가 존재하지 않아요."));
    }

    private void validateDeleteSavedRecipeAccessAuthorize(EatzUser user, SavedRecipe savedRecipe) {
        if (!Objects.equals(savedRecipe.getUser().getId(), user.getId())) {
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
