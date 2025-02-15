package imwhs.eatz_server.service.query;

import imwhs.eatz_server.dto.ingredient.IngredientDto;
import imwhs.eatz_server.dto.ingredient.IngredientRecipeDto;
import imwhs.eatz_server.dto.recipe.*;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.repository.ingredient.IngredientRecipeRepository;
import imwhs.eatz_server.repository.recipe.RecipeCategoryRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.repository.recipe.RecipeQueryRepository;
import imwhs.eatz_server.service.ingredient.IngredientRecipeService;
import imwhs.eatz_server.service.recipe.RecipeCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 레시피(Recipe) 관련 정보를 조회하는 RecipeQueryService 클래스입니다.
 * Recipe에 대한 읽기 전용 쿼리 메서드를 제공합니다.
 */
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RecipeQueryService {

    private final RecipeRepository recipeRepository;

    private final RecipeQueryRepository recipeQueryRepository;

    private final RecipeCategoryService recipeCategoryService;

    private final CommentQueryService commentQueryService;

    private final LikeQueryService likeQueryService;

    private final RatingQueryService ratingQueryService;

    private final IngredientRecipeService ingredientRecipeService;

    private final IngredientRecipeRepository ingredientRecipeRepository;
    private final RecipeCategoryRepository recipeCategoryRepository;

    /**
     * 식별자로 레시피를 조회합니다.
     * @param id 조회할 레시피의 식별자.
     * @return 조회된 레시피 정보를 담고 있는 RecipeResponseDto.
     * @throws RecipeNotFoundException id에 해당하는 레시피가 존재하지 않는 경우.
     */
    public NRecipeDto findRecipeById(Long id) {
        NRecipeDto recipeDto = recipeRepository.findRecipeWithUser(id).orElseThrow(
                () -> new RecipeNotFoundException(id));

        recipeDto.setIngredients(ingredientRecipeService.ingredientsOfRecipe(id));
        recipeDto.setCategories(recipeCategoryService.categoriesOfRecipe(id));
        recipeDto.setCommentCount(commentQueryService.countCommentByRecipeId(id));
        recipeDto.setLikeCount(likeQueryService.countLikeOfRecipe(id));
        recipeDto.setRating(ratingQueryService.findRatingSummaryByRecipeId(id));

        return recipeDto;
    }

    /**
     * 등록된 모든 레시피 목록을 조회하고, 각 레시피와 연관 관계인 상세 정보를 가져옵니다.
     * @param pageable
     * @return
     */
    public Page<NRecipeItemDto> findAllRecipeItems(String username, Pageable pageable) {
        Page<NRecipeItemDto> items = recipeRepository.findAllItemsWithUser(pageable);

        /*
        1. IngredientRecipeRepository에서 레시피 ID 별 재료 목록 조회 후 결합: NRecipeWithIngredientListDto
        2. RecipeCategoryRepository에서 레시피 ID 별 카테고리 목록 조회 후 결합
        3. CommentRepository에서 레시피 ID 별 댓글 수 조회 후 결합
        4. LikedRepository에서 레시피 ID 별 좋아요 수 조회 후 결합
        5. LikedRepository에서 레시피 ID 별 조회 요청한 사용자의 좋아요 여부 조회 후 결합
        6. SavedRecipeRepository에서 레시피 ID 별 저장 수 조회 후 결합
        7. SavedRecipeRepository에서 레시피 ID 별 조회 요청한 사용자의 저장 여부 조회 후 결합
        8. 평가
         */

        List<Long> recipeIds = items.getContent().stream().map(NRecipeItemDto::getId).toList();

        // 레시피 별 재료 정보를 조회합니다.
        List<IngredientRecipeDto> ingredientsByRecipeIds = ingredientRecipeRepository.findIngredientsByRecipeIds(recipeIds);
        Map<Long, List<IngredientRecipeDto>> ingredientsByRecipeIdMap = ingredientsByRecipeIds.stream()
                .collect(Collectors.groupingBy(IngredientRecipeDto::getRecipeId));

        // 레시피 별 카테고리 정보를 조회합니다.
        List<RecipeCategoryDto> categoriesByRecipeIds = recipeCategoryRepository.findCategoriesByRecipeIds(recipeIds);
        Map<Long, List<RecipeCategoryDto>> categoriesByRecipeIdMap = categoriesByRecipeIds.stream()
                .collect(Collectors.groupingBy(RecipeCategoryDto::getRecipeId));


        // 각 레시피에 대해 조회한 상세 정보를 결합합니다.
        for (NRecipeItemDto item : items) {
            Long recipeId = item.getId();

            List<IngredientDto> ingredientDtos = Optional.ofNullable(ingredientsByRecipeIdMap.get(recipeId))
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(ingredient -> new IngredientDto(ingredient.getIngredientId(), ingredient.getIngredientName()))
                    .collect(Collectors.toList());

            List<CategoryDto> categoryDtos = Optional.ofNullable(categoriesByRecipeIdMap.get(recipeId))
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(category -> new CategoryDto(category.getCategoryId(), category.getCategoryName()))
                    .collect(Collectors.toList());

            item.setIngredients(ingredientDtos);
            item.setCategories(categoryDtos);
        }

        return items;
    }

    /**
     * 모든 레시피를 조회합니다.
     * @return 조회된 레시피의 목록과 메타 데이터를 담고 있는 PagedResponseDto.
     */
//    public Page<RecipeDto> findAllRecipes(Pageable pageable) {
//        Page<RecipeDto> recipes = recipeRepository.findAllByDeletedAtIsNull(pageable);
//        return recipes;
//    }

    /**
     * 특정 사용자가 등록한 모든 레시피를 조회합니다.
     * @param userId 레시피를 등록한 사용자의 식별자.
     * @return 조회된 레시피의 목록과 메타 데이터를 담고 있는 PagedResponseDto.
     * @throws EatzUserNotFoundException userId에 해당하는 사용자가 존재하지 않는 경우.
     */
    public Page<RecipeDto> findAllRecipesByUser(Long userId, Pageable pageable) {
        Page<RecipeDto> foundRecipes = recipeRepository.findAllByUserIdAndDeletedAtIsNull(userId, pageable);
        return foundRecipes;
    }

}
