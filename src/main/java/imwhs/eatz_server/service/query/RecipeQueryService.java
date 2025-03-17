package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.RecipeItemSortType;
import imwhs.eatz_server.dto.ingredient.IngredientDto;
import imwhs.eatz_server.dto.ingredient.IngredientByRecipeDto;
import imwhs.eatz_server.dto.recipe.*;
import imwhs.eatz_server.dto.recipe.category.CategoryByRecipeDto;
import imwhs.eatz_server.dto.recipe.category.CategoryBasicDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.ingredient.IngredientRecipeRepository;
import imwhs.eatz_server.repository.ingredient.IngredientUserRepository;
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

    private final IngredientUserRepository ingredientUserRepository;

    private final EatzUserRepository eatzUserRepository;

    /**
     * 식별자로 레시피를 조회합니다.
     * @param id 조회할 레시피의 식별자.
     * @return 조회된 레시피 정보를 담고 있는 RecipeDto.
     * @throws RecipeNotFoundException id에 해당하는 레시피가 존재하지 않는 경우.
     */
    public RecipeDto findById(Long id, Long userId) {
        // 레시피 ID에 대한 Recipe와 해당 레시피를 등록한 사용자에 대한 User를 페치 조인을 통해 함께 가져옵니다.
        RecipeDto recipeDto = recipeRepository.findRecipeWithUser(id).orElseThrow(
                () -> new RecipeNotFoundException(id));

        // 레시피 ID에 대한 모든 Ingredient를 IngredientRecipe와의 페치 조인을 통해 함께 가져옵니다.
        ingredientRecipeRepository.findIngredientsByRecipe(id).forEach(ingredient -> {
            recipeDto.getIngredients().add(ingredient);
        });

        // 레시피 정보를 요청한 사용자의 레시피 재료 별 보유 여부를 가져옵니다.
        if (userId != null) {
            EatzUser user = eatzUserRepository.findById(userId).orElseThrow(() -> new EatzUserNotFoundException(userId));
            List<Long> ingredientIdsByUser = ingredientUserRepository.findIngredientIdsByUser(user);
            recipeDto.getIngredients().forEach(ingredient -> {
                ingredient.setOwnedByUser(ingredientIdsByUser.contains(ingredient.getId()));
            });
        }

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
    public Page<RecipeByUserDto> findByUserId(Long userId, Pageable pageable) {
        Page<RecipeByUserDto> recipes = recipeRepository.findByAuthorIdAndDeletedAtIsNull(userId, pageable);
        return recipes;
    }

    /**
     * 필터링을 적용해 레시피를 검색하거나, 모든 레시피 목록을 조회합니다.
     * @param sortType 레시피 정렬 기준.
     * @param userId 요청 사용자 ID.
     * @param keyword 레시피를 필터링 할 제목 및 내용 키워드.
     * @param categoryId 레시피를 필터링 할 카테고리 ID.
     * @param ingredientIds
     * @param requiredIngredientIds
     * @param pageable
     * @return
     */
    public Page<RecipeItemDto> search(
            RecipeItemSortType sortType,
            Long userId,
            String keyword,
            Long categoryId,
            List<Long> ingredientIds,
            List<Long> requiredIngredientIds,
            Pageable pageable) {
        Page<RecipeItemDto> items = recipeRepository.searchRecipeItems(sortType, userId, keyword, categoryId, ingredientIds, requiredIngredientIds, pageable);
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

            List<CategoryBasicDto> categoryBasicDtos = Optional.ofNullable(categoriesByRecipeIdMap.get(recipeId))
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(category -> new CategoryBasicDto(category.getCategoryId(), category.getCategoryName()))
                    .collect(Collectors.toList());

            item.setIngredients(ingredientDtos);
            item.setCategories(categoryBasicDtos);
        }

        return items;
    }

}
