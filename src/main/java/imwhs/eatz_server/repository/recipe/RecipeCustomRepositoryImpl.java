package imwhs.eatz_server.repository.recipe;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.QCategory;
import imwhs.eatz_server.domain.ingredient.QIngredient;
import imwhs.eatz_server.domain.ingredient.QIngredientRecipe;
import imwhs.eatz_server.domain.eatzuser.QEatzUser;
import imwhs.eatz_server.domain.liked.EntityType;
import imwhs.eatz_server.domain.liked.QLiked;
import imwhs.eatz_server.domain.recipe.*;
import imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto;
import imwhs.eatz_server.dto.ingredient.IngredientDto;
import imwhs.eatz_server.dto.recipe.category.CategoryBasicDto;
import imwhs.eatz_server.dto.recipe.RecipeDto;
import imwhs.eatz_server.dto.recipe.RecipeItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RecipeCustomRepositoryImpl implements RecipeCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<RecipeDto> findRecipeWithUserIngredients(Long id) {
        QRecipe recipe = QRecipe.recipe;
        QEatzUser user = QEatzUser.eatzUser;
        QIngredientRecipe ingredientRecipe = QIngredientRecipe.ingredientRecipe;
        QIngredient ingredient = QIngredient.ingredient;

        RecipeDto recipeDto = queryFactory
                .select(
                        Projections.constructor(RecipeDto.class,
                                recipe.id,
                                recipe.title,
                                recipe.description,
                                recipe.imageUrl,
                                recipe.createdAt,
                                recipe.updatedAt,
                                recipe.viewCount,
                                Projections.constructor(RecipeDto.AuthorOfRecipeDto.class,
                                        user.id,
                                        user.username,
                                        user.imageUrl,
                                        JPAExpressions
                                                .select(recipe.count())
                                                .from(recipe)
                                                .where(recipe.author.eq(user))
                                ),
                                Projections.constructor(IngredientDto.class,
                                        ingredient.id,
                                        ingredient.name
                                )
                        )
                )
                .from(recipe)
                .join(recipe.author, user)
                .join(recipe.ingredientRecipes, ingredientRecipe)
                .join(ingredientRecipe.ingredient, ingredient)
                .where(recipe.id.eq(id).and(recipe.deletedAt.isNull()))
                .fetchOne();

        return Optional.ofNullable(recipeDto);
    }

    @Override
    public Optional<RecipeDto> findRecipeWithUser(Long id) {
        QRecipe recipe = QRecipe.recipe;
        QEatzUser user = QEatzUser.eatzUser;

        RecipeDto recipeDto = queryFactory
                .select(
                        Projections.constructor(RecipeDto.class,
                                recipe.id,
                                recipe.title,
                                recipe.description,
                                recipe.imageUrl,
                                recipe.createdAt,
                                recipe.updatedAt,
                                recipe.cookingTime,
                                recipe.prepTime,
                                recipe.viewCount,
                                Projections.constructor(RecipeDto.AuthorOfRecipeDto.class,
                                        user.id,
                                        user.username,
                                        user.imageUrl,
                                        JPAExpressions
                                                .select(recipe.count())
                                                .from(recipe)
                                                .where(recipe.author.eq(user))
                                )
                        ))
                .from(recipe)
                .join(recipe.author, user)
                .where(recipe.id.eq(id).and(recipe.deletedAt.isNull()))
                .fetchOne();

        return Optional.ofNullable(recipeDto);
    }

    @Override
    public Optional<RecipeDto> findRecipeWithUserIngredientsCategories(Long id) {
        QRecipe recipe = QRecipe.recipe;
        QEatzUser user = QEatzUser.eatzUser;
        QIngredientRecipe ingredientRecipe = QIngredientRecipe.ingredientRecipe;
        QIngredient ingredient = QIngredient.ingredient;
        QRecipeCategory recipeCategory = QRecipeCategory.recipeCategory;
        QCategory category = QCategory.category;

        RecipeDto recipeDto = queryFactory
                .select(
                        Projections.constructor(RecipeDto.class,
                                recipe.id,
                                recipe.title,
                                recipe.description,
                                recipe.imageUrl,
                                recipe.createdAt,
                                recipe.updatedAt,
                                recipe.cookingTime,
                                recipe.prepTime,
                                recipe.viewCount,
                                Projections.constructor(RecipeDto.AuthorOfRecipeDto.class,
                                        user.id,
                                        user.username,
                                        user.imageUrl,
                                        JPAExpressions
                                                .select(recipe.count())
                                                .from(recipe)
                                                .where(recipe.author.eq(user))
                                ),
                                Projections.constructor(IngredientDto.class,
                                        ExpressionUtils.as(
                                                JPAExpressions.select(ingredient.id, ingredient.name)
                                                        .from(ingredientRecipe)
                                                        .join(ingredientRecipe.ingredient, ingredient)
                                                        .where(ingredientRecipe.recipe.eq(recipe)),
                                                "ingredients"
                                        )
                                        ),
                                Projections.constructor(CategoryBasicDto.class,
                                        ExpressionUtils.as(
                                                JPAExpressions.select(category.id)
                                                        .from(recipeCategory)
                                                        .join(recipeCategory.category, category)
                                                        .where(recipeCategory.recipe.eq(recipe)),
                                                "categories"
                                        )
                                )
                        ))
                .from(recipe)
                .join(recipe.author, user)
                .join(recipe.ingredientRecipes, ingredientRecipe)
                .join(recipe.recipeCategories, recipeCategory)
                .where(recipe.id.eq(id).and(recipe.deletedAt.isNull()))
                .fetchOne();

        return Optional.ofNullable(recipeDto);
    }

    @Override
    public Page<RecipeItemDto> searchRecipeItems(
            RecipeItemSortType sortType,
            Long currentUserId,
            Long categoryId,
            String keyword,
            List<Long> ingredientIds,
            List<Long> requiredIngredientIds,
            Long authorId,
            Pageable pageable) {
        QRecipe recipe = QRecipe.recipe;
        QEatzUser user = QEatzUser.eatzUser;
        QLiked liked = QLiked.liked;
        QComment comment = QComment.comment;
        QSavedRecipe savedRecipe = QSavedRecipe.savedRecipe;
        QIngredientRecipe ingredientRecipe = QIngredientRecipe.ingredientRecipe;
        QRating rating = QRating.rating;
        QRecipeCategory recipeCategory = QRecipeCategory.recipeCategory;

        // 공통 서브쿼리: 게시물 별 댓글 수 조회
        Expression<Long> commentCount = JPAExpressions.select(comment.count().longValue())
                .from(comment)
                .where(comment.recipe.id.eq(recipe.id).and(recipe.deletedAt.isNull()));

        // 공통 서브쿼리: 게시물 별 저장 수 조회
        Expression<Long> savedCount = JPAExpressions.select(savedRecipe.count().longValue())
                .from(savedRecipe)
                .where(savedRecipe.recipe.eq(recipe));

        // 공통 서브쿼리: 게시물 별 평가 수 조회
        Expression<Long> ratingCount = JPAExpressions.select(rating.count().longValue())
                .from(rating)
                .where(rating.recipe.id.eq(recipe.id).and(rating.isHidden.isFalse()).and(rating.deletedAt.isNull()));

        // 공통 서브쿼리: 게시물 별 좋아요 수 조회
        NumberExpression<Long> likedCount = Expressions.numberTemplate(
                Long.class,
                "({0})",
                JPAExpressions
                        .select(liked.count().longValue())
                        .from(liked)
                        .where(liked.entityId.eq(recipe.id).and(liked.type.eq(EntityType.RECIPE)).and(liked.isLiked.isTrue()))
        );

        // 공통 서브쿼리: 게시물 별 평균 점수 조회
        NumberTemplate<Double> averageRatingScore = Expressions.numberTemplate(
                Double.class,
                "({0})",
                JPAExpressions
                        .select(rating.score.avg().doubleValue())
                        .from(rating)
                        .where(rating.recipe.id.eq(recipe.id).and(rating.isHidden.isFalse()).and(rating.deletedAt.isNull()))
        );

        // 기본 쿼리: 레시피와 레시피를 등록한 사용자를 함께 조회합니다.
        JPAQuery<RecipeItemDto> query = queryFactory
                .select(
                        Projections.constructor(RecipeItemDto.class,
                                recipe.id,
                                recipe.title,
                                recipe.imageUrl,
                                recipe.createdAt,
                                recipe.updatedAt,
                                recipe.cookingTime,
                                recipe.prepTime,
                                recipe.viewCount,
                                Projections.constructor(EatzUserBasicDto.class,
                                        user.id,
                                        user.username,
                                        user.imageUrl
                                ),
                                JPAExpressions
                                        .select(new CaseBuilder()
                                                .when(liked.count().gt(0))
                                                .then(true)
                                                .otherwise(false))
                                        .from(liked)
                                        .where(liked.entityId.eq(recipe.id)
                                                .and(liked.type.eq(EntityType.RECIPE))
                                                .and(liked.user.id.eq(currentUserId))
                                                .and(liked.isLiked.isTrue())),
                                commentCount,
                                likedCount,
                                savedCount,
                                ratingCount,
                                averageRatingScore
                        )
                )
                .from(recipe)
                .join(recipe.author, user);

        BooleanBuilder predicate = new BooleanBuilder(recipe.deletedAt.isNull());

        // 기본적으로 삭제 처리되지 않은 레시피로 필터링합니다.
        applyFilter(query, predicate, recipe, keyword, categoryId, recipeCategory, ingredientIds, requiredIngredientIds, ingredientRecipe);

        applySorting(query, recipe, sortType, likedCount, averageRatingScore);

        // 페이징 적용한 조회 결과를 반환하기 위한 인스턴스를 가져옵니다.
        List<RecipeItemDto> result = query.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();

        // 페이징 처리를 위해 전체 레시피 수를 조회합니다.
        Long totalCount = queryFactory
                .select(recipe.count())
                .from(recipe)
                .leftJoin(recipe.recipeCategories, recipeCategory)
                .leftJoin(recipe.ingredientRecipes, ingredientRecipe)
                .where(predicate)
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    /**
     * 쿼리에 필터를 설정합니다.
     * 설정한 필터 값은 BooleanBuilder를 통해 보관합니다.
     * @param query
     * @param recipe
     * @param keyword
     * @param ingredientIds
     * @param requiredIngredientIds
     * @param ingredientRecipe
     */
    private void applyFilter(
            JPAQuery<RecipeItemDto> query,
            BooleanBuilder predicate,
            QRecipe recipe,
            String keyword,
            Long categoryId,
            QRecipeCategory recipeCategory,
            List<Long> ingredientIds,
            List<Long> requiredIngredientIds,
            QIngredientRecipe ingredientRecipe) {
        // 제목 또는 내용의 검색 키워드 포함 여부로 레시피를 필터링합니다.
        if (StringUtils.hasText(keyword)) {
            predicate.and(
                    recipe.title.containsIgnoreCase(keyword)
                            .or(recipe.description.containsIgnoreCase(keyword))
            );
        }

        // 특정 카테고리에 포함되는 레시피를 필터링합니다.
        if (categoryId != null) {
            query.join(recipe.recipeCategories, recipeCategory);
            predicate.and(recipeCategory.category.id.eq(categoryId));
        }

        // 특정 재료 포함 여부로 레시피를 필터링합니다.
        if (ingredientIds != null && !ingredientIds.isEmpty()) {
            query.join(recipe.ingredientRecipes, ingredientRecipe);
            predicate.and(ingredientRecipe.ingredient.id.in(ingredientIds));
        }

        // 특정 재료만 포함하는 레시피를 필터링합니다.
        if (requiredIngredientIds != null && !requiredIngredientIds.isEmpty()) {
            // 레시피가 갖고 있는 재료 ID가 모두 onlyIngredientIds에 포함되는지 검사
            predicate.and(
                    recipe.id.notIn(
                            JPAExpressions.select(ingredientRecipe.recipe.id)
                                    .from(ingredientRecipe)
                                    .where(ingredientRecipe.ingredient.id.notIn(requiredIngredientIds))
                    )
            );
        }

        // 레시피 기준으로 레코드를 그룹핑 후, 필터 옵션에 따라 추가되는 조건을 쿼리에 적용합니다.
        query.groupBy(recipe.id).where(predicate);
    }

    /**
     * 쿼리에 정렬 옵션을 적용합니다.
     * @param query
     * @param recipe
     * @param sortType
     * @param likedCount
     * @param averageRatingScore
     */
    private void applySorting(
            JPAQuery<RecipeItemDto> query,
            QRecipe recipe,
            RecipeItemSortType sortType,
            NumberExpression<Long> likedCount,
            NumberTemplate<Double> averageRatingScore) {
        switch (sortType) {
            case MOST_LIKED:
                query.orderBy(likedCount.desc());
                break;
            case HIGHEST_RATED:
                query.orderBy(averageRatingScore.desc());
                break;
            case LATEST:
            default:
                query.orderBy(recipe.createdAt.desc());
        }
    }

}
