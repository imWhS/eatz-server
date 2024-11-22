package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.dto.recipe.RecipeDetailResponseDto;
import imwhs.eatz_server.dto.recipe.RecipeResponseDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.repository.recipe.RecipeQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RecipeQueryService {

    private final RecipeRepository recipeRepository;

    private final RecipeQueryRepository recipeQueryRepository;

    /**
     * 페이지 번호 및 크기 기본 값.
     * 응답 메시지에 포함시킬 레시피에 대해 페이징 처리를 하기 위해 정의합니다.
     */
    private static final int DEFAULT_PAGE_NUMBER = 0;
    private static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 식별자로 레시피를 조회합니다.
     * @param id 조회할 레시피의 식별자.
     * @return 조회된 레시피 정보를 담고 있는 RecipeResponseDto.
     * @throws RecipeNotFoundException id에 해당하는 레시피가 존재하지 않는 경우.
     */
    public RecipeResponseDto findRecipeById(Long id) {
        Recipe recipe = recipeRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(
                () -> new RecipeNotFoundException("id가 " + id + "인 레시피를 찾을 수 없습니다."));

        return new RecipeResponseDto(recipe);
    }

    /**
     * 식별자로 레시피의 상세 정보를 조회합니다.
     * 식별자로 레시피와 해당 레시피를 등록한 사용자와 해당 레시피에 달린 댓글 및 평가의 요약 정보를 함께 조회합니다.
     * @param id 조회할 레시피의 식별자.
     * @return 조회된 레시피의 상세 정보를 담고 있는 RecipeDetailResponseDto.
     * @throws RecipeNotFoundException id에 해당하는 레시피가 존재하지 않는 경우.
     */
    public RecipeDetailResponseDto findRecipeDetailsById(Long id) {
        return recipeQueryRepository.findRecipeDetailById(id)
                .orElseThrow(() -> new RecipeNotFoundException("id가 " + id + "인 레시피를 찾을 수 없습니다."));
    }

    /**
     * 모든 레시피를 조회합니다.
     * @param pageNumber 페이징 처리 시, 조회할 페이지 인덱스. 0부터 시작하며 선택 사항입니다.
     * @param pageSize 페이징 처리 시, 하나의 페이지에 포함할 레시피 수. 선택 사항입니다.
     * @return 조회된 레시피의 목록과 메타 데이터를 담고 있는 PagedResponseDto.
     */
    public Page<RecipeResponseDto> findAllRecipes(Integer pageNumber, Integer pageSize) {
        int number = (pageNumber == null) ? DEFAULT_PAGE_NUMBER : pageNumber;
        int size = (pageSize == null) ? DEFAULT_PAGE_SIZE : pageSize;

        PageRequest pageRequest = PageRequest.of(number, size);
        Page<Recipe> recipes = recipeRepository.findAllByDeletedAtIsNull(pageRequest);

        return recipes.map(RecipeResponseDto::new);
    }

    /**
     * 특정 사용자가 등록한 모든 레시피를 조회합니다.
     * @param userId 레시피를 등록한 사용자의 식별자.
     * @param pageNumber 페이징 처리 시, 조회할 페이지 인덱스. 0부터 시작하며 선택 사항입니다.
     * @param pageSize 페이징 처리 시, 하나의 페이지에 포함할 레시피 수. 선택 사항입니다.
     * @return 조회된 레시피의 목록과 메타 데이터를 담고 있는 PagedResponseDto.
     * @throws EatzUserNotFoundException userId에 해당하는 사용자가 존재하지 않는 경우.
     */
    public Page<RecipeResponseDto> findAllRecipesByUser(Long userId, Integer pageNumber, Integer pageSize) {
        int number = (pageNumber == null) ? DEFAULT_PAGE_NUMBER : pageNumber;
        int size = (pageSize == null) ? DEFAULT_PAGE_SIZE : pageSize;

        PageRequest pageRequest = PageRequest.of(number, size);
        Page<Recipe> foundRecipes = recipeRepository.findAllByUserIdAndDeletedAtIsNull(userId, pageRequest);

        return foundRecipes.map(RecipeResponseDto::new);
    }

}
