package imwhs.eatz_server.repository.recipe.ingredient;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.QIngredient;
import imwhs.eatz_server.domain.recipe.QRecipeIngredient;
import imwhs.eatz_server.dto.ingredient.IngredientEssentialDto;
import imwhs.eatz_server.dto.recipe.ingredient.IngredientRequirementDto;
import imwhs.eatz_server.repository.util.IngredientQueryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RecipeIngredientQueryRepositoryImpl implements RecipeIngredientQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Map<Long, List<IngredientEssentialDto>> findAllMissingEssentials(
            List<Long> recipeIds,
            List<Long> ingredientIds) {
        if (recipeIds == null || recipeIds.isEmpty()) { return Collections.emptyMap(); }

        QRecipeIngredient recipeIngredient = QRecipeIngredient.recipeIngredient;
        QIngredient ingredient = QIngredient.ingredient;

        return queryFactory
                .from(recipeIngredient)
                .innerJoin(recipeIngredient.ingredient, ingredient).on(ingredient.deletedAt.isNull())
                .where(
                        recipeIngredient.recipe.id.in(recipeIds),
                        recipeIngredient.deletedAt.isNull(),
                        createHasMissingIngredientsExpression(ingredientIds, ingredient))
                .transform(
                        GroupBy.groupBy(recipeIngredient.recipe.id).as(
                                GroupBy.list(
                                        Projections.constructor(IngredientEssentialDto.class,
                                                ingredient.id,
                                                ingredient.name))));
    }

    @Override
    public Map<Long, List<IngredientEssentialDto>> findAllEssentialsByRecipeIds(List<Long> ids) {
        if (ids.isEmpty()) { return Collections.emptyMap(); }

        QRecipeIngredient ingredientRecipe = QRecipeIngredient.recipeIngredient;
        QIngredient ingredient = QIngredient.ingredient;

        return queryFactory
                .from(ingredientRecipe)
                .innerJoin(ingredientRecipe.ingredient, ingredient).on(ingredient.deletedAt.isNull())
                .where(
                        ingredientRecipe.recipe.id.in(ids),
                        ingredientRecipe.deletedAt.isNull())
                .transform(
                        GroupBy.groupBy(ingredientRecipe.recipe.id).as(
                                GroupBy.list(
                                        Projections.constructor(IngredientEssentialDto.class,
                                                ingredient.id,
                                                ingredient.name))));
    }

    @Override
    public List<IngredientRequirementDto> findAllIngredientRequirementsByRecipeId(Long id, Long userId) {
        QRecipeIngredient ingredientRecipe = QRecipeIngredient.recipeIngredient;
        QIngredient ingredient = QIngredient.ingredient;

        return queryFactory
                .select(Projections.constructor(IngredientRequirementDto.class,
                        ingredient.id,
                        ingredient.name,
                        IngredientQueryUtil.createIsOwnedExpression(ingredient, userId),
                        IngredientQueryUtil.createIsLikedExpression(ingredient, userId)))
                .from(ingredientRecipe)
                .innerJoin(ingredientRecipe.ingredient, ingredient).on(ingredient.deletedAt.isNull())
                .where(
                        ingredientRecipe.recipe.id.eq(id),
                        ingredientRecipe.deletedAt.isNull()
                )
                .fetch();
    }

    @Nullable
    private static BooleanExpression createHasMissingIngredientsExpression(
            List<Long> ingredientIdsByUser,
            QIngredient ingredient) {
        // QueryDSL의 notIn의 파라미터로 null 또는 빈 컬렉션이 전달돼선 안 되기 때문에,
        // ingredientIdsByUser가 null이거나 비어 있으면 notIn이 아닌 null 자체를 반환합니다.
        return ingredientIdsByUser == null || ingredientIdsByUser.isEmpty()
                ? null
                : ingredient.id.notIn(ingredientIdsByUser);
    }

}
