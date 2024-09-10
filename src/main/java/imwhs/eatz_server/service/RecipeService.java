package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.dto.CreateRecipeDto;
import imwhs.eatz_server.dto.PagedResponseDto;
import imwhs.eatz_server.dto.RecipeResponseDto;
import imwhs.eatz_server.dto.UpdateRecipeDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.exception.UnauthorizedAccessException;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeService {

    private final RecipeRepository recipeRepository;

    private final EatzUserRepository userRepository;

    /**
     * 새 레시피 등록.
     * @param dto    등록할 레시피 정보를 담고 있는 CreateRecipeDto
     * @param userId 새 레시피를 등록하려는 사용자의 ID
     * @return 등록 완료된 레시피 정보를 담고 있는 RecipeResponseDto
     * @throws EatzUserNotFoundException userId에 해당하는 사용자가 존재하지 않는 경우
     */
    @Transactional
    public RecipeResponseDto registerRecipe(CreateRecipeDto dto, Long userId) {
        EatzUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EatzUserNotFoundException("id가 " + userId + "인 사용자가 존재하지 않습니다."));

        Recipe recipe = Recipe.create(user, dto.getTitle(), dto.getUrl(), dto.getImageUrl(), dto.getDescription());
        recipeRepository.save(recipe);

        return new RecipeResponseDto(recipe);
    }

    /**
     * 레시피 수정.
     * @param id 수정할 레시피 ID
     * @param dto 수정할 레시피 정보를 담고 있는 UpdateRecipeDto
     * @param userId 레시피 수정을 요청한 사용자 ID
     * @return 수정 완료된 레시피 정보를 담고 있는 RecipeResponseDto
     * @throws RecipeNotFoundException id에 해당하는 레시피가 존재하지 않는 경우
     * @throws UnauthorizedAccessException 레시피 삭제 처리를 요청한 사용자 ID와 레시피를 등록한 사용자 ID가 다른 경우
     */
    @Transactional
    public RecipeResponseDto updateRecipe(Long id, UpdateRecipeDto dto, Long userId) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() ->
                        new RecipeNotFoundException("id가 " + id + "인 레시피가 존재하지 않습니다."));

        if (!recipe.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("해당 레시피를 등록한 사용자가 아니어서, 레시피를 수정할 권한이 없습니다.");
        }

        recipe.update(dto);

        return new RecipeResponseDto(recipe);
    }

    /**
     * 레시피 삭제 처리.
     * @param id 삭제 처리할 레시피 ID
     * @param userId 레시피 삭제 처리를 요청한 사용자 ID
     * @throws RecipeNotFoundException id에 해당하는 레시피가 존재하지 않는 경우
     * @throws UnauthorizedAccessException 레시피 삭제 처리를 요청한 사용자 ID와 레시피를 등록한 사용자 ID가 다른 경우
     */
    @Transactional
    public void deleteRecipe(Long id, Long userId) {
        Recipe recipe = recipeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RecipeNotFoundException("id가 " + id + "인 레시피가 존재하지 않습니다."));

        if (!recipe.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("해당 레시피를 등록한 사용자가 아니어서, 레시피를 삭제할 권한이 없습니다.");
        }

        recipe.markAsDeleted();
    }

}
