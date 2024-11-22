package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.dto.recipe.RecipeCreateDto;
import imwhs.eatz_server.dto.recipe.RecipeUpdateDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.exception.UnauthorizedAccessException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private EatzUserRepository userRepository;

    /**
     * 새 레시피를 등록합니다.
     * @param dto    등록할 레시피 정보를 담고 있는 CreateRecipeDto.
     * @param userId 새 레시피를 등록하려는 사용자의 식별자.
     * @return 등록 완료된 레시피 정보를 담고 있는 RecipeResponseDto.
     * @throws EatzUserNotFoundException userId에 해당하는 사용자가 존재하지 않는 경우.
     */
    @Transactional
    public Long registerRecipe(RecipeCreateDto dto, Long userId) {
        EatzUser user = getEatzUser(userId);
        Recipe recipe = dto.toEntity(user);
        recipeRepository.save(recipe);
        return recipe.getId();
    }

    /**
     * 레시피를 업데이트합니다.
     * @param id 업데이트할 레시피 식별자.
     * @param dto 업데이트할 레시피 정보를 담고 있는 UpdateRecipeDto.
     * @param userId 레시피 업데이트를 요청한 사용자 식별자.
     * @throws RecipeNotFoundException id에 해당하는 레시피가 존재하지 않는 경우.
     * @throws UnauthorizedAccessException 레시피 삭제 처리를 요청한 사용자 식별자와 레시피를 등록한 사용자 식별자가 다른 경우.
     */
    @Transactional
    public void updateRecipe(Long id, RecipeUpdateDto dto, Long userId) {
        Recipe recipe = getRecipe(id);
        EatzUser user = getEatzUser(userId);
        if (!recipe.getUser().equals(user)) {
            throw new UnauthorizedAccessException("해당 레시피를 등록한 사용자가 아니어서, 레시피를 업데이트할 권한이 없습니다.");
        }

        recipe.update(dto);
    }

    /**
     * 레시피를 삭제 처리합니다.
     * @param id 삭제 처리할 레시피 식별자.
     * @param userId 레시피 삭제 처리를 요청한 사용자 식별자.
     * @throws RecipeNotFoundException id에 해당하는 레시피가 존재하지 않는 경우.
     * @throws UnauthorizedAccessException 레시피 삭제 처리를 요청한 사용자 식별자.와 레시피를 등록한 사용자 식별자가 다른 경우.
     */
    @Transactional
    public void deleteRecipe(Long id, Long userId) {
        Recipe recipe = getRecipe(id);
        EatzUser user = getEatzUser(userId);
        if (!recipe.getUser().equals(user)) {
            throw new UnauthorizedAccessException("해당 레시피를 등록한 사용자가 아니어서, 레시피를 업데이트할 권한이 없습니다.");
        }

        recipe.markAsDeleted();
    }

    private EatzUser getEatzUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EatzUserNotFoundException("id가 " + userId + "인 사용자를 찾을 수 없습니다."));
    }

    private Recipe getRecipe(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException("id가 " + id + "인 레시피를 찾을 수 없습니다."));
    }

}
