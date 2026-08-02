package imwhs.eatz_server.repository.pantry.ingredient;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.QIngredient;
import imwhs.eatz_server.domain.pantry.QPantryIngredient;
import imwhs.eatz_server.dto.ingredient.IngredientBasicDto;
import imwhs.eatz_server.repository.util.IngredientQueryUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class PantryIngredientQueryRepositoryImpl implements PantryIngredientQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<IngredientBasicDto> findAllByUser(EatzUser user, Pageable pageable) {
        QPantryIngredient pantryIngredient = QPantryIngredient.pantryIngredient;
        QIngredient ingredient = QIngredient.ingredient;
        QIngredient parent = new QIngredient("parent");

        JPAQuery<IngredientBasicDto> mainQuery = queryFactory
                .select(Projections.constructor(IngredientBasicDto.class,
                        ingredient.id,
                        ingredient.isParentCoupled,
                        parent.name,
                        ingredient.name,
                        IngredientQueryUtil.createHasChildrenExpression(ingredient),
                        Expressions.asBoolean(true),
                        IngredientQueryUtil.createIsLikedExpression(ingredient, user.getId())))
                .from(pantryIngredient)
                .innerJoin(pantryIngredient.ingredient, ingredient).on(ingredient.deletedAt.isNull())
                .leftJoin(ingredient.parent, parent)
                .where(
                        pantryIngredient.user.eq(user),
                        pantryIngredient.deletedAt.isNull());

        List<IngredientBasicDto> content = mainQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(ingredient.count())
                .from(pantryIngredient)
                .innerJoin(pantryIngredient.ingredient, ingredient).on(ingredient.deletedAt.isNull())
                .where(
                        pantryIngredient.user.eq(user),
                        pantryIngredient.deletedAt.isNull());

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Set<Long> findAllIngredientIdsByUserId(Long id, List<Long> ingredientIds) {
        QPantryIngredient pantryIngredient = QPantryIngredient.pantryIngredient;

        List<Long> ids = queryFactory
                .select(pantryIngredient.ingredient.id)
                .from(pantryIngredient)
                .where(
                        createIngredientIdsFilterExpression(ingredientIds, pantryIngredient),
                        pantryIngredient.user.id.eq(id),
                        pantryIngredient.deletedAt.isNull()
                )
                .fetch();

        return new HashSet<>(ids);
    }

    private static BooleanExpression createIngredientIdsFilterExpression(
            List<Long> ingredientIds, QPantryIngredient pantryIngredient) {
        if (ingredientIds == null || ingredientIds.isEmpty()) { return null; }
        return pantryIngredient.ingredient.id.in(ingredientIds);
    }

}
