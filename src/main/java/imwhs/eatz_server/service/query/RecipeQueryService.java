package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.recipe.RecipeItemSortType;
import imwhs.eatz_server.dto.ingredient.IngredientDto;
import imwhs.eatz_server.dto.ingredient.IngredientByRecipeDto;
import imwhs.eatz_server.dto.rating.RatingSummaryByRecipeDto;
import imwhs.eatz_server.dto.rating.RatingSummaryDto;
import imwhs.eatz_server.dto.recipe.*;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.repository.comment.CommentRepository;
import imwhs.eatz_server.repository.ingredient.IngredientRecipeRepository;
import imwhs.eatz_server.repository.liked.LikedRepository;
import imwhs.eatz_server.repository.rating.RatingRepository;
import imwhs.eatz_server.repository.recipe.RecipeCategoryRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 레시피(Recipe) 관련 정보를 조회하는 RecipeQueryService 클래스입니다.
 * Recipe에 대한 읽기 전용 쿼리 메서드를 제공합니다.
 * TODO: count, avg 관련 쿼리 최적화
 * TODO: Fetch join, DTO Projection 사용한 쿼리 최적화
 */
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RecipeQueryService {

    private final RecipeRepository recipeRepository;

    private final CommentQueryService commentQueryService;

    private final LikedQueryService likedQueryService;

    private final RatingQueryService ratingQueryService;

    private final IngredientRecipeRepository ingredientRecipeRepository;

    private final RecipeCategoryRepository recipeCategoryRepository;

    /**
     * 식별자로 레시피를 조회합니다.
     * @param id 조회할 레시피의 식별자.
     * @return 조회된 레시피 정보를 담고 있는 RecipeDto.
     * @throws RecipeNotFoundException id에 해당하는 레시피가 존재하지 않는 경우.
     */
    public NRecipeDto findRecipeById(Long id) {
        // 레시피 ID에 대한 Recipe와 해당 레시피를 등록한 사용자에 대한 User를 페치 조인을 통해 함께 가져옵니다.
        NRecipeDto recipeDto = recipeRepository.findRecipeWithUser(id).orElseThrow(
                () -> new RecipeNotFoundException(id));

        // 레시피 ID에 대한 모든 Ingredient를 IngredientRecipe와의 페치 조인을 통해 함께 가져옵니다.
        ingredientRecipeRepository.findIngredientsByRecipe(id).forEach(ingredient -> {
            recipeDto.getIngredients().add(ingredient);
        });

        // 레시피 ID에 대한 모든 Category를 RecipeCategory와의 페치 조인을 통해 함께 가져옵니다.
        recipeCategoryRepository.findCategoriesByRecipe(id).forEach(recipeCategory -> {
            recipeDto.getCategories().add(recipeCategory);
        });

        recipeDto.setCommentCount(commentQueryService.countCommentByRecipeId(id));
        recipeDto.setLikedCount(likedQueryService.countLikeOfRecipe(id));
        recipeDto.setRating(ratingQueryService.findRatingSummaryByRecipeId(id));

        return recipeDto;
    }

    /**
     * 특정 사용자가 등록한 모든 레시피를 조회합니다.
     * @param userId 레시피를 등록한 사용자의 식별자.
     * @return 조회된 레시피의 목록과 메타 데이터를 담고 있는 PagedResponseDto.
     * @throws EatzUserNotFoundException userId에 해당하는 사용자가 존재하지 않는 경우.
     */
    public Page<RecipeDto> findRecipesByUserId(Long userId, Pageable pageable) {
        Page<RecipeDto> foundRecipes = recipeRepository.findAllByUserIdAndDeletedAtIsNull(userId, pageable);
        return foundRecipes;
    }

    public Page<RecipeItemDto> searchRecipeItems(RecipeItemSortType sortType, Long userId, Long categoryId, String keyword, List<Long> ingredientIds, List<Long> exactIngredientIds, Pageable pageable) {
        Page<RecipeItemDto> items = recipeRepository.searchRecipeItems(sortType, userId, categoryId, keyword, ingredientIds, exactIngredientIds, null, pageable);

        List<Long> recipeIds = items.getContent().stream().map(RecipeItemDto::getId).toList();

        // 레시피 별 재료 정보를 조회합니다.
        List<IngredientByRecipeDto> ingredientsByRecipeIds = ingredientRecipeRepository.findIngredientsByRecipeIds(recipeIds);
        Map<Long, List<IngredientByRecipeDto>> ingredientsByRecipeIdMap = ingredientsByRecipeIds.stream()
                .collect(Collectors.groupingBy(IngredientByRecipeDto::getRecipeId));

        // 레시피 별 카테고리 정보를 조회합니다.
        List<CategoryByRecipeDto> categoriesByRecipeIds = recipeCategoryRepository.findCategoriesByRecipeIds(recipeIds);
        Map<Long, List<CategoryByRecipeDto>> categoriesByRecipeIdMap = categoriesByRecipeIds.stream()
                .collect(Collectors.groupingBy(CategoryByRecipeDto::getRecipeId));

        for (RecipeItemDto item : items) {
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

}
