package imwhs.eatz_server.repository.recipe;

import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.QRating;
import imwhs.eatz_server.domain.eatzuser.QEatzUser;
import imwhs.eatz_server.domain.liked.QLikedRecipe;
import imwhs.eatz_server.domain.recipe.*;
import imwhs.eatz_server.dto.rating.RatingIndicatorSummaryDto;
import imwhs.eatz_server.dto.recipe.*;
import imwhs.eatz_server.dto.recipe.cookable.CookableRecipeDto;
import imwhs.eatz_server.dto.recipe.cookable.CookableRecipesRequest;
import imwhs.eatz_server.dto.recipe.explore.ExploreRecipeDto;
import imwhs.eatz_server.dto.recipe.explore.ExploreRecipesRequest;
import imwhs.eatz_server.repository.util.BlockedQueryUtil;
import imwhs.eatz_server.repository.util.RecipeQueryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class RecipeQueryRepositoryImpl implements RecipeQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ExploreRecipeDto> findAllExploreRecipes(
            ExploreRecipesRequest request,
            Long userId,
            Pageable pageable) {
        QRecipe recipe = QRecipe.recipe;
        QEatzUser author = QEatzUser.eatzUser;

        // Recipe 시작점인 메인 쿼리를 생성합니다.
        Predicate[] filters = {
                recipe.deletedAt.isNull(),
                // Recipe 레코드 별 필터를 적용하기 위해 메인 쿼리에 where 절을 추가합니다.
                createExploreRecipesSearchFilter(request, recipe),
                RecipeQueryUtil.createRecipeTagFilter(recipe, request.getTagId()),
                BlockedQueryUtil.createBlockedUserFilter(userId, recipe.author.id)};

        JPAQuery<ExploreRecipeDto> mainQuery = queryFactory
                .select(Projections.constructor(ExploreRecipeDto.class,
                        recipe.id,
                        recipe.title,
                        recipe.imageUrl,
                        recipe.servings,
                        recipe.cookingTime,
                        recipe.prepTime,
                        author.id,
                        author.username,
                        recipe.isCommentEnabled,
                        RecipeQueryUtil.getLikedCount(recipe),
                        RecipeQueryUtil.getCommentCount(recipe),
                        RecipeQueryUtil.getRatingCount(recipe),
                        RecipeQueryUtil.getRatingAverageScore(recipe),
                        RecipeQueryUtil.createIsAuthorExpression(recipe, userId),
                        RecipeQueryUtil.createIsLikedExpression(recipe, userId),
                        RecipeQueryUtil.createIsSavedExpression(recipe, userId)))
                .from(recipe)
                .leftJoin(recipe.author, author)
                .where(filters);

        // 정렬을 적용합니다.
        List<OrderSpecifier<?>> orderSpecifiers = createOrderSpecifiersByRecipeSort(request.getSort(), recipe);
        mainQuery.orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new));

        // 페이징을 적용해 메인 쿼리의 설계를 완성합니다.
        List<ExploreRecipeDto> content = mainQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 페이징 데이터를 위해 카운트 쿼리를 추가로 생성한 후, 설계까지 완성합니다.
        JPAQuery<Long> countQuery = queryFactory.select(recipe.count()).from(recipe);
        countQuery.where(filters);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<CookableRecipeDto> findAllCookableRecipes(
            CookableRecipesRequest request,
            Long userId,
            List<Long> ingredientIdsByUser,
            List<Long> kitchenwareIdsByUser,
            Pageable pageable) {
        QRecipe recipe = QRecipe.recipe;
        QEatzUser author = QEatzUser.eatzUser;

        Predicate[] filters = {recipe.deletedAt.isNull(),
                // Recipe 레코드 별 필터를 적용하기 위해 메인 쿼리에 where 절을 추가합니다.
                createCookableRecipesSearchFilter(
                        request,
                        recipe,
                        ingredientIdsByUser,
                        kitchenwareIdsByUser),
                BlockedQueryUtil.createBlockedUserFilter(userId, recipe.author.id)};

        JPAQuery<CookableRecipeDto> mainQuery = queryFactory.select(Projections.constructor(CookableRecipeDto.class,
                        recipe.id,
                        recipe.title,
                        recipe.imageUrl,
                        recipe.servings,
                        recipe.cookingTime,
                        recipe.prepTime,
                        author.id,
                        author.username,
                        RecipeQueryUtil.getLikedCount(recipe),
                        RecipeQueryUtil.getCommentCount(recipe),
                        RecipeQueryUtil.getRatingCount(recipe),
                        RecipeQueryUtil.getRatingAverageScore(recipe),
                        RecipeQueryUtil.createIsLikedExpression(recipe, userId),
                        RecipeQueryUtil.createIsSavedExpression(recipe, userId),
                        RecipeQueryUtil.getMissingIngredientCount(recipe, ingredientIdsByUser),
                        RecipeQueryUtil.getMissingKitchenwareCount(recipe, kitchenwareIdsByUser)))
                .from(recipe)
                .leftJoin(recipe.author, author)
                .where(filters);

        // 정렬을 적용합니다.
        List<OrderSpecifier<?>> orderSpecifiers = createCookableRecipesOrderSpecifiersByRecipeSort(
                request.getSort(),
                recipe,
                ingredientIdsByUser,
                kitchenwareIdsByUser);
        mainQuery.orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new));

        // 페이징을 적용해 메인 쿼리의 설계를 완성합니다.
        List<CookableRecipeDto> content = mainQuery.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();

        // 페이징 데이터를 위해 카운트 쿼리를 추가로 생성한 후, 설계까지 완성합니다.
        JPAQuery<Long> countQuery = queryFactory.select(recipe.count()).from(recipe);
        countQuery.where(filters);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<RecipeBasicDto> findAllBasics(String keyword, Long userId, Pageable pageable) {
        QRecipe recipe = QRecipe.recipe;
        QEatzUser author = QEatzUser.eatzUser;

        // Recipe 시작점인 메인 쿼리를 생성합니다.
        JPAQuery<RecipeBasicDto> mainQuery = queryFactory
                .select(Projections.constructor(RecipeBasicDto.class,
                        recipe.id,
                        recipe.title,
                        recipe.imageUrl,
                        recipe.servings,
                        recipe.cookingTime,
                        recipe.prepTime,
                        author.id,
                        author.username,
                        RecipeQueryUtil.getRatingCount(recipe),
                        RecipeQueryUtil.getRatingAverageScore(recipe),
                        RecipeQueryUtil.createIsAuthorExpression(recipe, userId),
                        RecipeQueryUtil.createIsLikedExpression(recipe, userId),
                        RecipeQueryUtil.createIsSavedExpression(recipe, userId)))
                .from(recipe)
                .leftJoin(recipe.author, author);

        // Recipe 레코드 별 필터를 정의합니다.
        BooleanExpression recipeSearchFilter = Expressions.allOf(
                RecipeQueryUtil.createRecipeKeywordFilter(recipe, keyword),
                recipe.deletedAt.isNull()
        );

        // Recipe 레코드 별 필터를 적용하기 위해 메인 쿼리에 where 절을 추가합니다.
        mainQuery.where(
                recipeSearchFilter,
                BlockedQueryUtil.createBlockedUserFilter(userId, recipe.author.id)
        );

        // createdAt 내림차순, title 오름차순으로 정렬을 적용합니다.
        mainQuery.orderBy(recipe.createdAt.desc(), recipe.title.asc());

        // 페이징을 적용해 메인 쿼리의 설계를 완성합니다.
        List<RecipeBasicDto> content = mainQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 페이징 데이터를 위해 카운트 쿼리를 추가로 생성한 후, 설계까지 완성합니다.
        JPAQuery<Long> countQuery = queryFactory.select(recipe.count()).from(recipe);
        countQuery.where(
                recipeSearchFilter,
                BlockedQueryUtil.createBlockedUserFilter(userId, recipe.author.id)
        );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<RecipeBasicDto> findAllLikedBasicsByUserId(Long id, Pageable pageable) {
        QRecipe recipe = QRecipe.recipe;
        QLikedRecipe liked = QLikedRecipe.likedRecipe;
        QEatzUser author = QEatzUser.eatzUser;

        // Recipe가 시작점인 메인 쿼리를 생성합니다.
        JPAQuery<RecipeBasicDto> mainQuery = queryFactory
                .select(Projections.constructor(RecipeBasicDto.class,
                        recipe.id,
                        recipe.title,
                        recipe.imageUrl,
                        recipe.servings,
                        recipe.cookingTime,
                        recipe.prepTime,
                        author.id,
                        author.username,
                        RecipeQueryUtil.getRatingCount(recipe),
                        RecipeQueryUtil.getRatingAverageScore(recipe),
                        RecipeQueryUtil.createIsAuthorExpression(recipe, id),
                        Expressions.asBoolean(true),
                        RecipeQueryUtil.createIsSavedExpression(recipe, id)))
                .from(recipe)
                .leftJoin(recipe.author, author)
                .where(
                        recipe.deletedAt.isNull(),
                        BlockedQueryUtil.createBlockedUserFilter(id, recipe.author.id)
                );

        // 사용자가 좋아하는 Recipe 레코드만 남기고, 레코드 별 연관 관계인 Liked.updatedAt을 정렬 기준으로 쓰기 위해서
        // 사용자 id에 해당하는 Liked와 inner join 합니다.
        innerJoinUserLiked(mainQuery, recipe, liked, id);

        // Inner join 한 Liked의 updatedAt 내림차순, Recipe.title 오름차순으로 정렬을 적용합니다.
        mainQuery.orderBy(liked.updatedAt.desc(), recipe.title.asc());

        // 페이징을 적용해 메인 쿼리의 설계를 완성합니다.
        List<RecipeBasicDto> content = mainQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 페이징 데이터를 위해 카운트 쿼리를 추가로 생성한 후, 설계까지 완성합니다.
        JPAQuery<Long> countQuery = queryFactory.select(recipe.count()).from(recipe);
        countQuery.where(
                recipe.deletedAt.isNull(),
                BlockedQueryUtil.createBlockedUserFilter(id, recipe.author.id)
        );
        innerJoinUserLiked(countQuery, recipe, liked, id);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<RecipeBasicDto> findAllRatedRecipeBasicsByAuthorId(Long id, Pageable pageable) {
        QRecipe recipe = QRecipe.recipe;
        QRating rating = QRating.rating;
        QEatzUser author = QEatzUser.eatzUser;

        // Recipe가 시작점인 메인 쿼리를 생성합니다.
        JPAQuery<RecipeBasicDto> mainQuery = queryFactory.select(Projections.constructor(RecipeBasicDto.class,
                        recipe.id,
                        recipe.title,
                        recipe.imageUrl,
                        recipe.servings,
                        recipe.cookingTime,
                        recipe.prepTime,
                        author.id,
                        author.username,
                        RecipeQueryUtil.getRatingCount(recipe),
                        RecipeQueryUtil.getRatingAverageScore(recipe),
                        RecipeQueryUtil.createIsAuthorExpression(recipe, id),
                        RecipeQueryUtil.createIsLikedExpression(recipe, id),
                        RecipeQueryUtil.createIsSavedExpression(recipe, id)))
                .from(recipe)
                .leftJoin(recipe.author, author)
                .where(
                        recipe.deletedAt.isNull(),
                        BlockedQueryUtil.createBlockedUserFilter(id, recipe.author.id)
                );

        // 사용자가 평가한 Recipe 레코드만 남기고, 레코드 별 연관 관계인 Rating.updatedAt을 정렬 기준으로 쓰기 위해서
        // 사용자 id에 해당하는 Rating과 inner join 합니다.
        // 사용자는 하나의 레시피에 하나의 평가만 등록할 수 있기 때문에,
        // 1:N 연관 관계지만 데이터 뻥튀기가 발생하지 않으므로 distinct/groupBy를 적용하지 않습니다.
        innerJoinUserRated(mainQuery, recipe, rating, id);

        // Inner join 한 Rating의 updatedAt 내림차순, Recipe.title 오름차순으로 정렬을 적용합니다.
        mainQuery.orderBy(rating.updatedAt.desc(), recipe.title.asc());

        // 페이징을 적용해 메인 쿼리의 설계를 완성합니다.
        List<RecipeBasicDto> content = mainQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 페이징 데이터를 위해 카운트 쿼리를 추가로 생성한 후, 설계까지 완성합니다.
        JPAQuery<Long> countQuery = queryFactory.select(recipe.count()).from(recipe);
        countQuery.where(
                recipe.deletedAt.isNull(),
                BlockedQueryUtil.createBlockedUserFilter(id, recipe.author.id)
        );
        innerJoinUserRated(countQuery, recipe, rating, id);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<RecipeBasicDto> findAllSavedRecipeBasicsByUserId(Long id, Pageable pageable) {
        QRecipe recipe = QRecipe.recipe;
        QSavedRecipe savedRecipe = QSavedRecipe.savedRecipe;
        QEatzUser author = QEatzUser.eatzUser;

        // Recipe가 시작점인 메인 쿼리를 생성합니다.
        JPAQuery<RecipeBasicDto> mainQuery = queryFactory.select(Projections.constructor(RecipeBasicDto.class,
                        recipe.id,
                        recipe.title,
                        recipe.imageUrl,
                        recipe.servings,
                        recipe.cookingTime,
                        recipe.prepTime,
                        author.id,
                        author.username,
                        RecipeQueryUtil.getRatingCount(recipe),
                        RecipeQueryUtil.getRatingAverageScore(recipe),
                        RecipeQueryUtil.createIsAuthorExpression(recipe, id),
                        RecipeQueryUtil.createIsLikedExpression(recipe, id),
                        Expressions.asBoolean(true)))
                .from(recipe)
                .leftJoin(recipe.author, author)
                .where(
                        recipe.deletedAt.isNull(),
                        BlockedQueryUtil.createBlockedUserFilter(id, recipe.author.id)
                );

        // 사용자가 저장한 Recipe 레코드만 남기고, 레코드 별 연관 관계인 SavedRecipe.updatedAt을 정렬 기준으로 쓰기 위해서
        // 사용자 id에 해당하는 SavedRecipe와 inner join 합니다.
        innerJoinUserSaved(mainQuery, recipe, savedRecipe, id);

        // Inner join 한 SavedRecipe의 updatedAt 내림차순, Recipe.title 오름차순으로 정렬을 적용합니다.
        mainQuery.orderBy(savedRecipe.updatedAt.desc(), recipe.title.asc());

        // 페이징을 적용해 메인 쿼리의 설계를 완성합니다.
        List<RecipeBasicDto> content = mainQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 페이징 데이터를 위해 카운트 쿼리를 추가로 생성한 후, 설계까지 완성합니다.
        JPAQuery<Long> countQuery = queryFactory.select(recipe.count())
                .from(recipe)
                .where(
                        recipe.deletedAt.isNull(),
                        BlockedQueryUtil.createBlockedUserFilter(id, recipe.author.id)
                );
        innerJoinUserSaved(countQuery, recipe, savedRecipe, id);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<RecipeBasicDto> findAllBasicsByAuthorId(Long id, Pageable pageable) {
        QRecipe recipe = QRecipe.recipe;
        QEatzUser author = QEatzUser.eatzUser;

        Predicate[] filters = {recipe.author.id.eq(id),
                recipe.deletedAt.isNull(),
                BlockedQueryUtil.createBlockedUserFilter(id, recipe.author.id)};

        JPAQuery<RecipeBasicDto> mainQuery = queryFactory.select(Projections.constructor(RecipeBasicDto.class,
                        recipe.id,
                        recipe.title,
                        recipe.imageUrl,
                        recipe.servings,
                        recipe.cookingTime,
                        recipe.prepTime,
                        author.id,
                        author.username,
                        RecipeQueryUtil.getRatingCount(recipe),
                        RecipeQueryUtil.getRatingAverageScore(recipe),
                        Expressions.asBoolean(true),
                        RecipeQueryUtil.createIsLikedExpression(recipe, id),
                        RecipeQueryUtil.createIsSavedExpression(recipe, id)))
                .from(recipe)
                .leftJoin(recipe.author, author);

        // 메인 쿼리에 Recipe.authorId, Recipe.deletedAt 기준으로 필터링하기 위한 where 절을 추가합니다.
        mainQuery.where(filters);

        // updatedAt 내림차순, Recipe.title 오름차순으로 정렬을 적용합니다.
        mainQuery.orderBy(recipe.createdAt.desc(), recipe.title.asc());

        // 페이징을 적용해 메인 쿼리의 설계를 완성합니다.
        List<RecipeBasicDto> content = mainQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 페이징 데이터를 위해 카운트 쿼리를 추가로 생성한 후, 설계까지 완성합니다.
        JPAQuery<Long> countQuery = queryFactory.select(recipe.count()).from(recipe);
        countQuery.where(
                recipe.author.id.eq(id),
                recipe.deletedAt.isNull(),
                BlockedQueryUtil.createBlockedUserFilter(id, recipe.author.id));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Optional<RecipeDetailDto> findDetail(Long id, Long userId) {
        QRecipe recipe = QRecipe.recipe;
        QEatzUser author = new QEatzUser("author");

        RecipeDetailDto dto = queryFactory.select(Projections.constructor(RecipeDetailDto.class,
                        recipe.id,
                        recipe.title,
                        recipe.description,
                        recipe.imageUrl,
//                        recipe.url,
                        recipe.cookingTime,
                        recipe.prepTime,
                        recipe.creatorName,
                        recipe.creatorUrl,
                        recipe.servings,
                        recipe.createdAt,
                        recipe.updatedAt,
                        recipe.isCommentEnabled,
                        recipe.viewCount,
                        Projections.constructor(RecipeDetailDto.RecipeDetailAuthorDto.class,
                                author.id,
                                author.username,
                                author.imageUrl,
                                author.bio,
                                RecipeQueryUtil.getRecipeCount(author)),
                        RecipeQueryUtil.getCommentCount(recipe),
                        RecipeQueryUtil.getLikedCount(recipe),
                        RecipeQueryUtil.createIsLikedExpression(recipe, userId),
                        RecipeQueryUtil.createIsSavedExpression(recipe, userId),
                        Projections.constructor(RatingIndicatorSummaryDto.class,
                                RecipeQueryUtil.getRatingAverageScore(recipe),
                                RecipeQueryUtil.getRatingCount(recipe))))
                .from(recipe)
                .where(
                        recipe.id.eq(id).and(recipe.deletedAt.isNull()),
                        BlockedQueryUtil.createBlockedUserFilter(userId, recipe.author.id))
                .innerJoin(recipe.author, author)
                .fetchOne();

        return Optional.ofNullable(dto);
    }

    /**
     * Recipe와 특정 ID의 사용자에 해당하는 Liked를 inner join 합니다.
     *
     * @param query 수정할 JPQLQuery
     * @param recipe Liked와 join 할 QRecipe
     * @param userId 사용자의 ID
     */
    private void innerJoinUserLiked(JPQLQuery<?> query, QRecipe recipe, QLikedRecipe liked, Long userId) {
        query.innerJoin(liked)
                .on(liked.recipe.id.eq(recipe.id)
                        .and(liked.user.id.eq(userId))
                        .and(liked.isLiked.isTrue())
                        .and(liked.deletedAt.isNull()));
    }

    /**
     * Recipe와 평가 작성자 ID에 해당하는 Rating을 inner join 합니다.
     *
     * @param query 수정할 JPQLQuery
     * @param recipe Liked와 join 할 QRecipe
     * @param authorId 평가 작성자의 ID
     */
    private void innerJoinUserRated(JPQLQuery<?> query, QRecipe recipe, QRating rating, Long authorId) {
        query.innerJoin(rating)
                .on(rating.recipe.eq(recipe)
                        .and(rating.author.id.eq(authorId))
                        .and(rating.deletedAt.isNull())
                        .and(rating.isHidden.isFalse()));
    }

    /**
     * Recipe와 특정 사용자 ID에 해당하는 SavedRecipe을 inner join 합니다.
     * @param query 수정할 JPQLQuery
     * @param recipe SavedRecipe와 join 할 QRecipe
     * @param userId 사용자의 ID
     */
    private void innerJoinUserSaved(JPAQuery<?> query, QRecipe recipe, QSavedRecipe savedRecipe, Long userId) {
        query.innerJoin(savedRecipe)
                .on(savedRecipe.recipe.eq(recipe)
                        .and(savedRecipe.user.id.eq(userId))
                        .and(savedRecipe.deletedAt.isNull()));
    }


    private List<OrderSpecifier<?>> createCookableRecipesOrderSpecifiersByRecipeSort(
            CookableRecipesSort sort,
            QRecipe recipe,
            List<Long> ingredientIdsByUser,
            List<Long> kitchenwareIdsByUser) {
        if (sort == CookableRecipesSort.FEWEST_MISSING_REQUIREMENTS) {
            return createFewestMissingRequirementsOrderByRecipe(recipe,  ingredientIdsByUser, kitchenwareIdsByUser);
        } else {
            return createOrderSpecifiersByRecipeSort(sort, recipe);
        }
    }

    /**
     * RecipeSort의 case 별로 QueryDSL의 쿼리에 적용할 수 있는 OrderSpecifier 목록를 생성합니다.
     * @param sort 레시피 정렬 옵션
     * @param recipe 정렬 대상 QRecipe
     * @return
     */
    private List<OrderSpecifier<?>> createOrderSpecifiersByRecipeSort(RecipeSort sort, QRecipe recipe) {
        if (sort instanceof ExploreRecipesSort) {
            switch ((ExploreRecipesSort) sort) {
                case LATEST: return createLatestOrderByRecipe(recipe);
                case HIGHEST_RATED: return createHighestRatedOrderByRecipe(recipe);
                case MOST_LIKED: return createMostLikedOrderByRecipe(recipe);
                case TRENDING: return createTrendingOrderByRecipe(recipe);
            }
        } else if (sort instanceof CookableRecipesSort) {
            switch ((CookableRecipesSort) sort) {
                case LATEST: return createLatestOrderByRecipe(recipe);
                case HIGHEST_RATED: return createHighestRatedOrderByRecipe(recipe);
                case MOST_LIKED: return createMostLikedOrderByRecipe(recipe);
                case TRENDING: return createTrendingOrderByRecipe(recipe);
            }
        }

        throw new IllegalArgumentException("올바르지 않은 정렬 조건이에요.");
    }

    /**
     * Recipe를 준비물 수 오름차순으로 정렬하기 위한 OrderSpecifier를 생성합니다.
     * 준비물 수가 동일한 경우, title 오름차순으로 추가 정렬합니다.
     *
     * @param recipe 정렬 대상 QRecipe
     * @param ingredientIdsByUser 사용자가 보관함에 추가한 재료 ID 목록
     * @param kitchenwareIdsByUser 사용자가 보관함에 추가한 도구 ID 목록
     * @return 준비물 수 오름차순, title 오름차순으로 정렬하기 위한 OrderSpecifier 목록
     */
    private List<OrderSpecifier<?>> createFewestMissingRequirementsOrderByRecipe(
            QRecipe recipe,
            List<Long> ingredientIdsByUser,
            List<Long> kitchenwareIdsByUser) {
        NumberExpression<Long> missingIngredientCount = RecipeQueryUtil.getMissingIngredientCount(recipe, ingredientIdsByUser);
        NumberExpression<Long> missingKitchenwareCount = RecipeQueryUtil.getMissingKitchenwareCount(recipe, kitchenwareIdsByUser);
        NumberExpression<Long> requirementCount = missingIngredientCount.add(missingKitchenwareCount);
        return List.of(requirementCount.asc(), recipe.title.asc());
    }

    /**
     * Recipe를 createdAt 내림차순으로 정렬하기 위한 OrderSpecifier를 생성합니다.
     * createdAt이 동일한 경우, title 오름차순으로 추가 정렬합니다.
     * @param recipe 정렬 대상 QRecipe
     * @return createdAt 내림차순, title 오름차순으로 정렬하기 위한 OrderSpecifier 목록
     */
    private List<OrderSpecifier<?>> createLatestOrderByRecipe(QRecipe recipe) {
        return List.of(recipe.createdAt.desc(), recipe.title.asc());
    }

    /**
     * Recipe를 평가 평균 점수 내림차순으로 정렬하기 위한 OrderSpecifier를 생성합니다.
     * 평가 평균 점수가 동일한 경우, title 내림차순으로 추가 정렬합니다.
     *
     * @param recipe 정렬 대상 QRecipe
     * @return 평가 평균 점수 내림차순으로, title 오름차순으로 정렬하기 위한 OrderSpecifier 목록
     */
    private List<OrderSpecifier<?>> createHighestRatedOrderByRecipe(QRecipe recipe) {
        return List.of(RecipeQueryUtil.getRatingAverageScore(recipe).desc(), recipe.title.asc());
    }

    /**
     * Recipe를 좋아하는 사람 수 내림차순으로 정렬하기 위한 OrderSpecifier를 생성합니다.
     * 좋아하는 사람 수가 동일한 경우, title 내림차순으로 추가 정렬합니다.
     * @param recipe 정렬 대상 QRecipe
     * @return 좋아하는 사람 수 내림차순으로, title 오름차순으로 정렬하기 위한 OrderSpecifier 목록
     */
    private List<OrderSpecifier<?>> createMostLikedOrderByRecipe(QRecipe recipe) {
        return List.of(RecipeQueryUtil.getLikedCount(recipe).desc(), recipe.title.asc());
    }

    /**
     * Recipe를 추천 점수 내림차순으로 정렬하기 위한 OrderSpecifier를 생성합니다.
     * 추천 점수 동일한 경우, createdAt 내림차순으로 추가 정렬합니다.
     *
     * @param recipe 정렬 대상 QRecipe
     * @return 추천 점수 내림차순으로, createdAt 내림차순으로 정렬하기 위한 OrderSpecifier 목록
     */
    private List<OrderSpecifier<?>> createTrendingOrderByRecipe(QRecipe recipe) {
        NumberPath<Long> viewCount = recipe.viewCount;
        DateTimePath<LocalDateTime> createdAt = recipe.createdAt;

        NumberTemplate<Double> doubleNumberTemplate = Expressions.numberTemplate(
                Double.class, "(" +
                        "(COALESCE({0}, 0) * 1.0) + " +
                        "(COALESCE({1}, 0) * 0.1) + " +
                        "(COALESCE({2}, 0) * 1.0) + " +
                        "(COALESCE({3}, 0) * 2.0) - " +
                        "(TIMESTAMPDIFF(DAY, {4}, NOW()) * 0.5)" +
                        ")",
                RecipeQueryUtil.getLikedCount(recipe),
                viewCount,
                RecipeQueryUtil.getRatingAverageScore(recipe),
                RecipeQueryUtil.getRatingCount(recipe),
                createdAt
        );

        return List.of(doubleNumberTemplate.desc(), createdAt.desc());
    }

    /**
     * '지금 요리'에서 사용할 레시피 목록 조회 시, 검색 요청 조건에 따라 Recipe 필터링을 위한 표현식을 생성합니다.
     * @param request 검색 요청 조건
     * @param recipe 비교 대상 QRecipe
     * @param ingredientIdsByUser 사용자의 보관함에 추가되어 있는 재료 ID 목록
     * @param kitchenwareIdsByUser 사용자의 보관함에 추가되어 있는 도구 ID 목록
     * @return Recipe 필터링을 위한 표현식. 조건이 없으면 null을 반환할 수도 있습니다.
     */
    private BooleanExpression createCookableRecipesSearchFilter(
            CookableRecipesRequest request,
            QRecipe recipe,
            List<Long> ingredientIdsByUser,
            List<Long> kitchenwareIdsByUser) {
        // 레시피를 키워드로 필터링합니다.
        BooleanExpression recipeKeywordFilter = RecipeQueryUtil.createRecipeKeywordFilter(recipe, request.getKeyword());

        // 레시피를 최대 소요 시간으로 필터링합니다.
        BooleanExpression recipeMaxTotalTimeFilter = RecipeQueryUtil.createRecipeMaxTotalTimeFilter(recipe, request.getMaxTotalTime());

        // 레시피를 제공량으로 필터링합니다.
        BooleanExpression recipeServingsFilter = RecipeQueryUtil.createRecipeServingsFilter(recipe, request.getServings());

        // 지금 요리 가능한 레시피만 보기 옵션을 활성화한 경우,
        BooleanExpression recipeCookableFilter = request.getIsCookableOnly()
                ? createRecipeCookableFilter(
                        recipe,
                        ingredientIdsByUser,
                        kitchenwareIdsByUser)
                : null;

        // 각 필터 생성 메서드는 조건이 없을 경우 null을 반환할 수 있기 때문에,
        // null 값을 제외하고 AND로 묶어줍니다.
        return Expressions.allOf(
                recipeKeywordFilter,
                recipeServingsFilter,
                recipeMaxTotalTimeFilter,
                recipeCookableFilter);
    }

    /**
     * Recipe를 요리하기 위해 사용자가 추가해야 할 준비물이 없는지에 대한 여부를 확인하는 표현식을 생성합니다.
     * @param recipe 비교 대상 QRecipe
     * @param ingredientIdsByUser 사용자가 보관함에 추가한 재료 ID 목록
     * @param kitchenwareIdsByUser 사용자가 보관함에 추가한 도구 ID 목록
     * @return Recipe를 요리하기 위해 사용자가 추가해야 할 준비물이 없는지에 대한 여부
     */
    private BooleanExpression createRecipeCookableFilter(
            QRecipe recipe,
            List<Long> ingredientIdsByUser,
            List<Long> kitchenwareIdsByUser) {
        NumberExpression<Long> missingIngredientCount = RecipeQueryUtil.getMissingIngredientCount(recipe, ingredientIdsByUser);
        NumberExpression<Long> missingKitchenwareCount = RecipeQueryUtil.getMissingKitchenwareCount(recipe, kitchenwareIdsByUser);

        return missingIngredientCount.add(missingKitchenwareCount).eq(0L);
    }

    /**
     * '둘러보기'에서 사용할 레시피 목록 조회 시, 검색 요청 조건에 따라 Recipe 필터링을 위한 표현식을 생성합니다.
     *
     * @param request 검색 요청 조건
     * @param recipe  비교 대상 QRecipe
     * @return Recipe 필터링을 위한 표현식. 조건이 없으면 null을 반환할 수도 있습니다.
     */
    private BooleanExpression createExploreRecipesSearchFilter(ExploreRecipesRequest request, QRecipe recipe) {
        // 레시피를 키워드로 필터링합니다.
        BooleanExpression recipeKeywordFilter = RecipeQueryUtil.createRecipeKeywordFilter(recipe, request.getKeyword());

        // 레시피를 최대 소요 시간으로 필터링합니다.
        BooleanExpression recipeMaxTotalTimeFilter = RecipeQueryUtil.createRecipeMaxTotalTimeFilter(recipe, request.getMaxTotalTime());

        // 레시피를 제공량으로 필터링합니다.
        BooleanExpression recipeServingsFilter = RecipeQueryUtil.createRecipeServingsFilter(recipe, request.getServings());

        // 각 필터 생성 메서드는 조건이 없을 경우 null을 반환할 수 있기 때문에,
        // null 값을 제외하고 AND로 묶어줍니다.
        return Expressions.allOf(
                recipeKeywordFilter,
                recipeServingsFilter,
                recipeMaxTotalTimeFilter);
    }

}
