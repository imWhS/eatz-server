package imwhs.eatz_server.repository.util;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import imwhs.eatz_server.domain.QComment;
import imwhs.eatz_server.domain.QRating;
import imwhs.eatz_server.domain.eatzuser.QEatzUser;
import imwhs.eatz_server.domain.recipe.QRecipeIngredient;
import imwhs.eatz_server.domain.recipe.QRecipeKitchenware;
import imwhs.eatz_server.domain.liked.QLikedRecipe;
import imwhs.eatz_server.domain.recipe.*;

import java.util.List;

/**
 * Recipe 엔티티와 관련된 공통 서브쿼리 및 동적 필터링 적용에 필요한 표현식을 제공하는 유틸리티 클래스입니다.
 */
// @UtilityClass
public final class RecipeQueryUtil {

    private RecipeQueryUtil() {}

    static public NumberExpression<Long> getRecipeCount(QEatzUser author) {
        return Expressions.asNumber(createRecipeCountQuery(author)).longValue();
    }

    static private JPQLQuery<Long> createRecipeCountQuery(QEatzUser author) {
        // createRecipeCountQuery를 호출하는, 메인 쿼리를 설계하는 메서드의 별칭과 충돌하지 않도록
        // subRecipe라는 새 별칭을 정의, 사용합니다.
        QRecipe recipe = new QRecipe("subRecipe");

        return JPAExpressions
                .select(recipe.count())
                .from(recipe)
                .where(
                        recipe.author.eq(author),
                        recipe.deletedAt.isNull());
    }

    static public NumberExpression<Long> getMissingIngredientCount(
            QRecipe recipe,
            List<Long> ingredientIdsByUser) {
        return Expressions.asNumber(createMissingIngredientCountQuery(recipe, ingredientIdsByUser)).longValue();
    }

    static private JPQLQuery<Long> createMissingIngredientCountQuery(
            QRecipe recipe,
            List<Long> ingredientIdsByUser) {
        // 외부의 다른 쿼리와의 별칭 충돌을 피하기 위해, 이 메서드에서 설계할 쿼리에서만 사용할
        // IngredientRecipe의 별칭 subIngredientRecipe를 생성합니다.
        QRecipeIngredient subIngredientRecipe = new QRecipeIngredient("subIngredientRecipe");

        if (ingredientIdsByUser == null || ingredientIdsByUser.isEmpty()) {
            return JPAExpressions
                    .select(subIngredientRecipe.count())
                    .from(subIngredientRecipe)
                    .where(
                            subIngredientRecipe.recipe.eq(recipe),
                            subIngredientRecipe.deletedAt.isNull());
        } else {
            return JPAExpressions
                    .select(subIngredientRecipe.count())
                    .from(subIngredientRecipe)
                    .where(
                            subIngredientRecipe.recipe.eq(recipe),
                            subIngredientRecipe.ingredient.id.notIn(ingredientIdsByUser),
                            subIngredientRecipe.deletedAt.isNull());
        }
    }

    static public NumberExpression<Long> getMissingKitchenwareCount(
            QRecipe recipe,
            List<Long> kitchenwareIdsByUser) {
        return Expressions.asNumber(createMissingKitchenwareCountQuery(recipe, kitchenwareIdsByUser)).longValue();
    }

    static private JPQLQuery<Long> createMissingKitchenwareCountQuery(
            QRecipe recipe,
            List<Long> kitchenwareIdsByUser) {
        // 외부의 다른 쿼리와의 별칭 충돌을 피하기 위해, 이 메서드에서 설계할 쿼리에서만 사용할
        // KitchenwareRecipe의 별칭 subKitchenwareRecipe를 생성합니다.
        QRecipeKitchenware subKitchenwareRecipe = new QRecipeKitchenware("subKitchenwareRecipe");

        if (kitchenwareIdsByUser == null || kitchenwareIdsByUser.isEmpty()) {
            return JPAExpressions
                    .select(subKitchenwareRecipe.count())
                    .from(subKitchenwareRecipe)
                    .where(
                            subKitchenwareRecipe.recipe.eq(recipe),
                            subKitchenwareRecipe.deletedAt.isNull());
        } else {
            return JPAExpressions
                    .select(subKitchenwareRecipe.count())
                    .from(subKitchenwareRecipe)
                    .where(
                            subKitchenwareRecipe.recipe.eq(recipe),
                            subKitchenwareRecipe.kitchenware.id.notIn(kitchenwareIdsByUser),
                            subKitchenwareRecipe.deletedAt.isNull());
        }
    }

    /**
     * Recipe와 연관 관계인 모든 Liked 레코드 수를 서브쿼리를 통해 조회합니다.
     * 조회된 레코드가 없으먼 0을 반환합니다.
     */
    public static NumberExpression<Long> getLikedCount(QRecipe recipe) {
        return Expressions.asNumber(createLikedCountQuery(recipe)).longValue();
    }

    /**
     * Recipe와 연관 관계인 LikedRecipe 레코드 수를 집계하는 QueryDSL 상관 서브쿼리를 생성합니다.
     * @param recipe LikedRecipe를 서브쿼리로 연결할 때 외래 키로 사용할 QRecipe
     * @return Recipe와 연관 관계인 LikedRecipe 레코드 수를 집계하는 JPQLQuery.
     *         LikedRecipe가 하나도 없을 경우 조회 결과로 0을 반환합니다.
     */
    private static JPQLQuery<Long> createLikedCountQuery(QRecipe recipe) {
        // 외부의 다른 쿼리와의 별칭 충돌을 피하기 위해, 이 메서드에서 설계할 쿼리에서만 사용할
        // Liked의 별칭 subLiked를 생성합니다.
        QLikedRecipe subLikedRecipe = new QLikedRecipe("subLikedRecipe");
        return JPAExpressions
                .select(subLikedRecipe.count())
                .from(subLikedRecipe)
                .where(
                        subLikedRecipe.isLiked.isTrue(),
                        subLikedRecipe.recipe.eq(recipe),
                        subLikedRecipe.deletedAt.isNull());
    }

    /**
     * Recipe와 연관 관계인 모든 Comment 레코드 수를 서브쿼리를 통해 조회합니다.
     * 조회된 레코드가 없으먼 0을 반환합니다.
     */
    public static NumberExpression<Long> getCommentCount(QRecipe recipe) {
        return Expressions.asNumber(createCommentCountQuery(recipe)).longValue();
    }

    /**
     * Recipe와 연관 관계인 Comment 레코드 수를 집계하는 QueryDSL 상관 서브쿼리를 생성합니다.
     * @param recipe Comment를 서브쿼리로 연결할 때 외래 키로 사용할 QRecipe
     * @return Recipe와 연관 관계인 Comment 레코드 수를 집계하는 JPQLQuery.
     *         Comment가 하나도 없을 경우 조회 결과로 0을 반환합니다.
     */
    private static JPQLQuery<Long> createCommentCountQuery(QRecipe recipe) {
        // 외부의 다른 쿼리와의 별칭 충돌을 피하기 위해, 이 메서드에서 설계할 쿼리에서만 사용할
        // Comment의 별칭 subComment를 생성합니다.
        QComment subComment = new QComment("subComment");
        return JPAExpressions
                .select(subComment.count())
                .from(subComment)
                .where(
                        subComment.recipe.id.eq(recipe.id),
                        subComment.deletedAt.isNull(),
                        subComment.isHidden.isFalse());
    }

    /**
     * Recipe와 연관 관계인 모든 Rating 레코드 수를 서브쿼리를 통해 조회합니다.
     * 조회된 레코드가 없으먼 0을 반환합니다.
     */
    public static NumberExpression<Long> getRatingCount(QRecipe recipe) {
        return Expressions.asNumber(createRatingCountQuery(recipe)).longValue();
    }

    /**
     * Recipe와 연관 관계인 Rating 레코드 수를 집계하는 QueryDSL 상관 서브쿼리를 생성합니다.
     * @param recipe Rating을 서브쿼리로 연결할 때 외래 키로 사용할 QRecipe
     * @return Recipe와 연관 관계인 Rating 레코드 수를 집계하는 JPQLQuery.
     *         Rating이 하나도 없을 경우 조회 결과로 0을 반환합니다.
     */
    private static JPQLQuery<Long> createRatingCountQuery(QRecipe recipe) {
        // 외부의 다른 쿼리와의 별칭 충돌을 피하기 위해, 이 메서드에서 설계할 쿼리에서만 사용할
        // Rating의 별칭 subRating를 생성합니다.
        QRating subRating = new QRating("subRating");
        return JPAExpressions
                .select(subRating.count())
                .from(subRating)
                .where(
                        subRating.recipe.id.eq(recipe.id),
                        subRating.deletedAt.isNull(),
                        subRating.isHidden.isFalse());
    }

    /**
     * Recipe와 연관 관계인 모든 Rating 레코드의 평균 점수를 서브쿼리를 통해 조회합니다.
     * 조회된 레코드가 없으먼 0.0을 보장하는 표현식을 반환합니다.
     */
    public static NumberExpression<Double> getRatingAverageScore(QRecipe recipe) {
        return Expressions.asNumber(createRatingAverageScoreQuery(recipe)).doubleValue();
    }

    /**
     * Recipe와 연관 관계인 모든 Rating 레코드의 평균 점수를 집계하는 QueryDSL 상관 서브쿼리를 생성합니다.
     * @param recipe Rating을 서브쿼리로 연결할 때 외래 키로 사용할 QRecipe
     * @return Recipe와 연관 관계인 모든 Rating 레코드의 평균 점수를 집계하는 JPQLQuery.
     *         Rating이 하나도 없을 경우 조회 결과로 0.0을 반환합니다.
     */
    private static JPQLQuery<Double> createRatingAverageScoreQuery(QRecipe recipe) {
        // 외부의 다른 쿼리와의 별칭 충돌을 피하기 위해, 이 메서드에서 설계할 쿼리에서만 사용할
        // Rating의 별칭 subRatingForAverage를 생성합니다.
        QRating subRatingForAverage = new QRating("subRatingForAverage");
        return JPAExpressions
                .select(subRatingForAverage.score.avg().coalesce(0.0))
                .from(subRatingForAverage)
                .where(
                        subRatingForAverage.recipe.id.eq(recipe.id),
                        subRatingForAverage.deletedAt.isNull(),
                        subRatingForAverage.isHidden.isFalse());
    }

    /**
     * Recipe에 대한 특정 사용자의 작성자 여부를 확인하는 QueryDSL 표현식을 생성합니다.
     * @param recipe 메인 쿼리의 Recipe 레코드에서 작성자를 확인하기 위한 QRecipe
     * @param userId 사용자의 ID
     * @return Recipe의 작성자 여부를 확인하는 BooleanExpression.
     *         userID가 null일 경우 false를 보장하는 표현식을 반환합니다.
     */
    public static BooleanExpression createIsAuthorExpression(QRecipe recipe, Long userId) {
        if (userId == null) { return Expressions.asBoolean(false); }
        return recipe.author.id.eq(userId);
    }

    /**
     * Recipe에 대한 특정 사용자의 LikedRecipe 레코드 존재(레시피를 좋아하는 사용자) 여부를
     * 서브쿼리를 통해 확인하는 QueryDSL 표현식을 생성합니다.
     * @param recipe 메인 쿼리의 Recipe 레코드에서 서브쿼리 연결 시 외래 키를 참조하기 위한 QRecipe
     * @param userId 사용자의 ID
     * @return Recipe와 연관 관계인 LikedRecipe 레코드가 하나라도 존재하는지 여부를 확인하는 BooleanExpression.
     *         사용자의 ID가 null일 경우 false를 보장하는 표현식을 반환합니다.
     */
    public static BooleanExpression createIsLikedExpression(QRecipe recipe, Long userId) {
        if (userId == null) { return Expressions.asBoolean(false); }

        // 외부의 다른 쿼리와의 별칭 충돌을 피하기 위해, 이 메서드에서 설계할 쿼리에서만 사용할
        // Liked의 별칭 subLikedForIsLiked를 생성합니다.
        QLikedRecipe subLikedForIsLiked = new QLikedRecipe("subLikedForIsLiked");
        return JPAExpressions
                .selectOne()
                .from(subLikedForIsLiked)
                .where(
                        subLikedForIsLiked.isLiked.isTrue(),
                        subLikedForIsLiked.user.id.eq(userId),
                        subLikedForIsLiked.recipe.eq(recipe),
                        subLikedForIsLiked.deletedAt.isNull())
                .exists();
    }

    /**
     * Recipe에 대한 특정 사용자의 SavedRecipe 레코드 존재 여부를 서브쿼리를 통해 확인하는 QueryDSL 표현식을 생성합니다.
     * @param recipe 메인 쿼리의 Recipe 레코드에서 서브쿼리 연결 시 외래 키를 참조하기 위한 QRecipe
     * @param userId 사용자의 ID
     * @return Recipe와 연관 관계인 SavedRecipe 레코드가 하나라도 존재하는지 여부를 확인하는 BooleanExpression.
     *         사용자의 ID가 null일 경우 false를 보장하는 표현식을 반환합니다.
     */
    public static BooleanExpression createIsSavedExpression(QRecipe recipe, Long userId) {
        if (userId == null) return Expressions.asBoolean(false);

        // 외부의 다른 쿼리와의 별칭 충돌을 피하기 위해, 이 메서드에서 설계할 쿼리에서만 사용할
        // SavedRecipe의 별칭 subSavedRecipeForIsSaved를 생성합니다.
        QSavedRecipe subSavedRecipeForIsSaved = new QSavedRecipe("subSavedRecipeForIsSaved");
        return JPAExpressions
                .selectOne()
                .from(subSavedRecipeForIsSaved)
                .where(
                        subSavedRecipeForIsSaved.recipe.eq(recipe),
                        subSavedRecipeForIsSaved.user.id.eq(userId),
                        subSavedRecipeForIsSaved.deletedAt.isNull())
                .exists();
    }

    /**
     * Recipe에 대한 1회 제공량의 동일 여부를 확인하는 QueryDSL 표현식을 생성합니다.
     * @param recipe 메인 쿼리의 Recipe 레코드에서 레시피의 1회 제공량을 확인하기 위한 QRecipe
     * @param servings 1회 제공량
     * @return Recipe의 1회 제공량의 동일 여부를 확인하는 BooleanExpression.
     *         사용자의 ID가 null일 경우 false를 보장하는 표현식을 반환합니다.
     */
    public static BooleanExpression createRecipeServingsFilter(QRecipe recipe, Integer servings) {
        if (servings == null) { return null; }
        return recipe.servings.eq(servings);
    }

    /**
     * Recipe의 title 또는 description에 검색 대상 문자열이 포함되어 있는지에 대한 여부를 확인하는 QueryDSL 표현식을 생성합니다.
     * @param recipe 비교 대상 QRecipe
     * @param keyword 검색 대상 문자열
     * @return Recipe의 title 또는 description에 검색 대상 문자열의 포함 여부를 확인하는 BooleanExpression
     */
    public static BooleanExpression createRecipeKeywordFilter(QRecipe recipe, String keyword) {
        if (keyword == null || keyword.isEmpty()) return null;
        return recipe.title.contains(keyword).or(recipe.description.contains(keyword));
    }

    /**
     * Recipe의 cookingTime+prepTime이 최대 소요 시간 이내에 해당하는지에 대한 여부를 확인하는 QueryDSL 표현식을 생성합니다.
     * @param recipe 비교 대상 QRecipe
     * @param maxTotalTime 최대 소요 시간
     * @return Recipe의 cookingTime + prepTime이 최대 소요 시간 이내에 해당하는지에 대한 여부를 확인하는 BooleanExpression
     */
    public static BooleanExpression createRecipeMaxTotalTimeFilter(QRecipe recipe, Integer maxTotalTime) {
        if (maxTotalTime == null) return null;
        int maxTotalTimeInSeconds = maxTotalTime * 60;
        NumberExpression<Integer> totalTime = recipe.cookingTime.add(recipe.prepTime);
        return totalTime.loe(maxTotalTimeInSeconds);
    }

    /**
     * Recipe의 특정 Tag 포함 여부를 서브쿼리를 통해 확인하는 QueryDSL 표현식을 생성합니다.
     * @param recipe 메인 쿼리의 Recipe 레코드에서 서브쿼리 연결 시 외래 키를 참조하기 위한 QRecipe
     * @param tagId 태그의 ID
     * @return Recipe와 연관 관계인 RecipeTag 레코드가 하나라도 존재하는지 여부를 확인하는 BooleanExpression.
     *         태그의 ID가 null일 경우 false를 보장하는 표현식을 반환합니다.
     */
    public static BooleanExpression createRecipeTagFilter(QRecipe recipe, Long tagId) {
        if (tagId == null) return null;
        QRecipeTag subRecipeTag = new QRecipeTag("subRecipeTag");

        return JPAExpressions
                .selectOne()
                .from(subRecipeTag)
                .where(
                        subRecipeTag.tag.id.eq(tagId),
                        subRecipeTag.recipe.eq(recipe),
                        subRecipeTag.deletedAt.isNull())
                .exists();
    }

    /**
     * Recipe에서 RecipeTag로의 연관 관계 경로를 탐색해서, Recipe와 RecipeTag를 inner join 한 후
     * 특정 ID의 Tag에 속하는 Recipe 레코드만 필터링하도록 쿼리를 수정합니다.
     * @param query 수정할 JPAQuery
     * @param recipe RecipeTag에 대한 join 및 연관 관계 경로 시작점이 될 QRecipe
     * @param tagId Recipe를 필터링 할 특정 Tag의 ID
     */
    public static void applyRecipeTagFilterOld(JPAQuery<?> query, QRecipe recipe, Long tagId) {
        // tagId가 없을 경우, join 자체를 진행하지 않습니다.
        if (tagId == null) return;

        QRecipeTag recipeTag = QRecipeTag.recipeTag;
        query
                .distinct() // Recipe와 1:N 연관 관계인 RecipeTag join 시, 조회 결과에 발생할 수 있는 중복 레코드를 제거합니다.
                .innerJoin(recipe.tags, recipeTag).on(recipeTag.tag.id.eq(tagId).and(recipeTag.deletedAt.isNull()));
    }

}
