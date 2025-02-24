package imwhs.eatz_server.repository.recipe;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.QIngredient;
import imwhs.eatz_server.domain.QIngredientRecipe;
import imwhs.eatz_server.domain.eatzuser.QEatzUser;
import imwhs.eatz_server.domain.liked.LikedType;
import imwhs.eatz_server.domain.liked.QLiked;
import imwhs.eatz_server.domain.recipe.*;
import imwhs.eatz_server.dto.eatzuser.EatzUserEssentialsDto;
import imwhs.eatz_server.dto.ingredient.IngredientDto;
import imwhs.eatz_server.dto.recipe.CategoryDto;
import imwhs.eatz_server.dto.recipe.NRecipeDto;
import imwhs.eatz_server.dto.recipe.RecipeItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RecipeCustomRepositoryImpl implements RecipeCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<NRecipeDto> findRecipeWithUser(Long id) {
        QRecipe recipe = QRecipe.recipe;
        QEatzUser user = QEatzUser.eatzUser;

        NRecipeDto recipeDto = queryFactory
                .select(
                        Projections.constructor(NRecipeDto.class,
                                recipe.id,
                                recipe.title,
                                recipe.description,
                                recipe.imageUrl,
                                recipe.createdAt,
                                recipe.updatedAt,
                                Projections.constructor(NRecipeDto.UserDto.class,
                                        user.id,
                                        user.username,
                                        user.imageUrl,
                                        JPAExpressions
                                                .select(recipe.count())
                                                .from(recipe)
                                                .where(recipe.user.eq(user))
                                )
                        ))
                .from(recipe)
                .join(recipe.user, user)
                .where(recipe.id.eq(id).and(recipe.deletedAt.isNull()))
                .fetchOne();

        return Optional.ofNullable(recipeDto);
    }

    public Optional<NRecipeDto> findRecipeWithUserIngredientsCategories(Long id) {
        QRecipe recipe = QRecipe.recipe;
        QEatzUser user = QEatzUser.eatzUser;
        QIngredientRecipe ingredientRecipe = QIngredientRecipe.ingredientRecipe;
        QIngredient ingredient = QIngredient.ingredient;
        QRecipeCategory recipeCategory = QRecipeCategory.recipeCategory;
        QCategory category = QCategory.category;

        NRecipeDto recipeDto = queryFactory
                .select(
                        Projections.constructor(NRecipeDto.class,
                                recipe.id,
                                recipe.title,
                                recipe.description,
                                recipe.imageUrl,
                                recipe.createdAt,
                                recipe.updatedAt,
                                Projections.constructor(NRecipeDto.UserDto.class,
                                        user.id,
                                        user.username,
                                        user.imageUrl,
                                        JPAExpressions
                                                .select(recipe.count())
                                                .from(recipe)
                                                .where(recipe.user.eq(user))
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
                                Projections.constructor(CategoryDto.class,
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
                .join(recipe.user, user)
                .join(recipe.ingredientRecipes, ingredientRecipe)
                .join(recipe.recipeCategories, recipeCategory)
                .where(recipe.id.eq(id).and(recipe.deletedAt.isNull()))
                .fetchOne();

        return Optional.ofNullable(recipeDto);
    }

    @Override
    public Page<RecipeItemDto> findAllRecipes(Long userId, Pageable pageable) {
        QRecipe recipe = QRecipe.recipe;
        QEatzUser user = QEatzUser.eatzUser;
        QLiked liked = QLiked.liked;
        QComment comment = QComment.comment;
        QNSavedRecipe savedRecipe = QNSavedRecipe.nSavedRecipe;

        List<RecipeItemDto> result = queryFactory
                .select(
                        Projections.constructor(RecipeItemDto.class,
                                recipe.id,
                                recipe.title,
                                recipe.imageUrl,
                                recipe.createdAt,
                                recipe.updatedAt,
                                Projections.constructor(EatzUserEssentialsDto.class,
                                        user.id,
                                        user.username,
                                        user.imageUrl
                                ),
                                JPAExpressions.select(
                                        new CaseBuilder()
                                                .when(liked.count().gt(0))
                                                .then(Boolean.TRUE)
                                                .otherwise(Boolean.FALSE))
                                        .from(liked)
                                        .where(liked.entityId.eq(recipe.id)
                                                .and(liked.type.eq(LikedType.RECIPE))
                                                .and(liked.user.id.eq(user.id))),
                                JPAExpressions.select(comment.count().longValue())
                                        .from(comment)
                                        .where(comment.recipe.id.eq(recipe.id)),
                                JPAExpressions.select(liked.count().longValue())
                                        .from(liked)
                                        .where(liked.entityId.eq(recipe.id).and(liked.type.eq(LikedType.RECIPE))),
                                JPAExpressions.select(savedRecipe.count().longValue())
                                        .from(savedRecipe)
                                        .where(savedRecipe.recipe.eq(recipe))
                        )
                )
                .from(recipe)
                .join(recipe.user, user)
                .where(recipe.deletedAt.isNull())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(result, pageable, result.size());
    }

}
