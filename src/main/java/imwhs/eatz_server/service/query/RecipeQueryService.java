package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.dto.recipe.RecipeDetailDto;
import imwhs.eatz_server.dto.recipe.RecipeDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.repository.recipe.RecipeQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레시피(Recipe) 관련 정보를 조회하는 RecipeQueryService 클래스입니다.
 * Recipe에 대한 읽기 전용 쿼리 메서드를 제공합니다.
 */
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
    public RecipeDto findRecipeById(Long id) {
        return recipeRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(
                () -> new RecipeNotFoundException("id가 " + id + "인 레시피를 찾을 수 없습니다."));
    }

    /**
     * 식별자로 레시피의 상세 정보를 조회합니다.
     * 식별자로 레시피와 해당 레시피를 등록한 사용자와 해당 레시피에 달린 댓글 및 평가의 요약 정보를 함께 조회합니다.
     * @param id 조회할 레시피의 식별자.
     * @return 조회된 레시피의 상세 정보를 담고 있는 RecipeDetailResponseDto.
     * @throws RecipeNotFoundException id에 해당하는 레시피가 존재하지 않는 경우.
     */
    public RecipeDetailDto findRecipeDetailsById(Long id) {
        return recipeQueryRepository.findRecipeDetailById(id)
                .orElseThrow(() -> new RecipeNotFoundException("id가 " + id + "인 레시피를 찾을 수 없습니다."));
    }

    /**
     * 모든 레시피를 조회합니다.
     * @return 조회된 레시피의 목록과 메타 데이터를 담고 있는 PagedResponseDto.
     */
    public Page<RecipeDto> findAllRecipes(Pageable pageable) {
        Page<RecipeDto> recipes = recipeRepository.findAllByDeletedAtIsNull(pageable);
        return recipes;
    }

    /**
     * 특정 사용자가 등록한 모든 레시피를 조회합니다.
     * @param userId 레시피를 등록한 사용자의 식별자.
     * @return 조회된 레시피의 목록과 메타 데이터를 담고 있는 PagedResponseDto.
     * @throws EatzUserNotFoundException userId에 해당하는 사용자가 존재하지 않는 경우.
     */
    public Page<RecipeDto> findAllRecipesByUser(Long userId, Pageable pageable) {
        Page<Recipe> foundRecipes = recipeRepository.findAllByUserIdAndDeletedAtIsNull(userId, pageable);
        return foundRecipes.map(RecipeDto::new);
    }

}
