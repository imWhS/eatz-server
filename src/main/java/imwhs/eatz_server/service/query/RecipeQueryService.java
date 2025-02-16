package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.likes.LikesType;
import imwhs.eatz_server.dto.comment.CommentCountByRecipeDto;
import imwhs.eatz_server.dto.ingredient.IngredientDto;
import imwhs.eatz_server.dto.ingredient.IngredientByRecipeDto;
import imwhs.eatz_server.dto.likes.LikeCountByEntityDto;
import imwhs.eatz_server.dto.rating.RatingSummaryByRecipeDto;
import imwhs.eatz_server.dto.rating.RatingSummaryDto;
import imwhs.eatz_server.dto.recipe.*;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.repository.comment.CommentRepository;
import imwhs.eatz_server.repository.ingredient.IngredientRecipeRepository;
import imwhs.eatz_server.repository.like.LikeRepository;
import imwhs.eatz_server.repository.rating.RatingRepository;
import imwhs.eatz_server.repository.recipe.RecipeCategoryRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.service.ingredient.IngredientRecipeService;
import imwhs.eatz_server.service.recipe.RecipeCategoryService;
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

    private final RecipeCategoryService recipeCategoryService;

    private final CommentQueryService commentQueryService;

    private final LikeQueryService likeQueryService;

    private final RatingQueryService ratingQueryService;

    private final IngredientRecipeService ingredientRecipeService;

    private final IngredientRecipeRepository ingredientRecipeRepository;

    private final RecipeCategoryRepository recipeCategoryRepository;

    private final CommentRepository commentRepository;

    private final LikeRepository likeRepository;

    private final RatingRepository ratingRepository;

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

        List<Long> recipeIds = items.getContent().stream().map(NRecipeItemDto::getId).toList();

        // 레시피 별 재료 정보를 조회합니다.
        List<IngredientByRecipeDto> ingredientsByRecipeIds = ingredientRecipeRepository.findIngredientsByRecipeIds(recipeIds);
        Map<Long, List<IngredientByRecipeDto>> ingredientsByRecipeIdMap = ingredientsByRecipeIds.stream()
                .collect(Collectors.groupingBy(IngredientByRecipeDto::getRecipeId));

        // 레시피 별 카테고리 정보를 조회합니다.
        List<CategoryByRecipeDto> categoriesByRecipeIds = recipeCategoryRepository.findCategoriesByRecipeIds(recipeIds);
        Map<Long, List<CategoryByRecipeDto>> categoriesByRecipeIdMap = categoriesByRecipeIds.stream()
                .collect(Collectors.groupingBy(CategoryByRecipeDto::getRecipeId));

        // 레시피 별 댓글 수를 조회합니다.
        List<CommentCountByRecipeDto> commentCountsByRecipeIds = commentRepository.countByRecipeIds(recipeIds);
        Map<Long, Long> commentCountsByRecipeIdMap = commentCountsByRecipeIds.stream()
                .collect(Collectors.toMap(
                        CommentCountByRecipeDto::getRecipeId,
                        CommentCountByRecipeDto::getCommentCount
                ));

        // 레시피 별 좋아요 수를 조회합니다.
        List<LikeCountByEntityDto> likeCountsByEntityDtos = likeRepository.countByEntityIdsAndType(recipeIds, LikesType.RECIPE);
        Map<Long, Long> likeCountsByRecipeIdMap = likeCountsByEntityDtos.stream()
                .collect(Collectors.toMap(
                        LikeCountByEntityDto::getEntityId,
                        LikeCountByEntityDto::getLikeCount
                ));

        // 요청한 사용자의 레시피 별 좋아요 여부를 조회합니다.
        Set<Long> likedRecipeIds = new HashSet<>(likeRepository.findLikesByEntityIdsAndTypeAndUserUsername(recipeIds, LikesType.RECIPE, username));

        // 레시피 별 평가 정보를 조회합니다.
        List<RatingSummaryByRecipeDto> ratingSummariesByRecipeIds = ratingRepository.findRatingSummariesByRecipeIds(recipeIds);
        Map<Long, RatingSummaryDto> ratingSummariesByRecipeIdMap = ratingSummariesByRecipeIds.stream()
                .collect(Collectors.toMap(
                        RatingSummaryByRecipeDto::getRecipeId,
                        ratingSummary ->
                                new RatingSummaryDto(ratingSummary.getRatingCount(), ratingSummary.getAverageRatingScore())));

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
            item.setCommentCount(commentCountsByRecipeIdMap.get(recipeId));
            item.setLikeCount(likeCountsByRecipeIdMap.get(recipeId));
            item.setLikedByUser(likedRecipeIds.contains(recipeId));
            item.setRating(ratingSummariesByRecipeIdMap.get(recipeId));
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
    public Page<RecipeDto> findAllRecipesByUserId(Long userId, Pageable pageable) {
        Page<RecipeDto> foundRecipes = recipeRepository.findAllByUserIdAndDeletedAtIsNull(userId, pageable);
        return foundRecipes;
    }

}
