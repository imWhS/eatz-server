package imwhs.eatz_server.repository.ingredient;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.QIngredient;
import imwhs.eatz_server.domain.liked.QLikedIngredient;
import imwhs.eatz_server.dto.ingredient.IngredientBasicDto;
import imwhs.eatz_server.repository.util.IngredientQueryUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class IngredientQueryRepositoryImpl implements IngredientQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<IngredientBasicDto> findAllBasicsByParentId(Long id, Long userId) {
        QIngredient ingredient = QIngredient.ingredient;
        QIngredient parent = new QIngredient("parent");

        return queryFactory
                .select(Projections.constructor(IngredientBasicDto.class,
                        ingredient.id,
                        ingredient.isParentCoupled,
                        parent.name,
                        ingredient.name,
                        IngredientQueryUtil.createHasChildrenExpression(ingredient),
                        IngredientQueryUtil.createIsOwnedExpression(ingredient, userId),
                        IngredientQueryUtil.createIsLikedExpression(ingredient, userId)))
                .from(parent)
                .innerJoin(parent.children, ingredient).on(ingredient.deletedAt.isNull())
                .where(
                        parent.id.eq(id),
                        parent.deletedAt.isNull()
                )
                .orderBy(ingredient.name.asc())
                .fetch();
    }

    @Override
    public Page<IngredientBasicDto> findAllIngredientsByLikedUserId(Long userId, Pageable pageable) {
        QIngredient ingredient = QIngredient.ingredient;
        QIngredient parent = new QIngredient("parent");
        QLikedIngredient likedIngredient = QLikedIngredient.likedIngredient;

        JPAQuery<IngredientBasicDto> mainQuery = queryFactory
                .select(Projections.constructor(IngredientBasicDto.class,
                        ingredient.id,
                        ingredient.isParentCoupled,
                        parent.name,
                        ingredient.name,
                        IngredientQueryUtil.createHasChildrenExpression(ingredient),
                        IngredientQueryUtil.createIsOwnedExpression(ingredient, userId),
                        IngredientQueryUtil.createIsLikedExpression(ingredient, userId)))
                .from(ingredient)
                .innerJoin(likedIngredient).on(
                        likedIngredient.ingredient.eq(ingredient)
                                .and(likedIngredient.user.id.eq(userId))
                                .and(likedIngredient.isLiked.isTrue())
                                .and(likedIngredient.deletedAt.isNull())
                )
                .leftJoin(ingredient.parent, parent)
                .where(
                        ingredient.deletedAt.isNull()
                )
                .orderBy(likedIngredient.updatedAt.desc(), ingredient.name.asc());

        List<IngredientBasicDto> content = mainQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(ingredient.count())
                .from(ingredient)
                .innerJoin(likedIngredient).on(
                        likedIngredient.ingredient.eq(ingredient)
                                .and(likedIngredient.user.id.eq(userId))
                                .and(likedIngredient.deletedAt.isNull())
                )
                .where(
                        ingredient.deletedAt.isNull()
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<IngredientBasicDto> findAllRootBasics(Long userId, Pageable pageable) {
        QIngredient ingredient = QIngredient.ingredient;

        JPAQuery<IngredientBasicDto> mainQuery = queryFactory.select(Projections.constructor(IngredientBasicDto.class,
                        ingredient.id,
                        ingredient.isParentCoupled,
                        Expressions.nullExpression(String.class),
                        ingredient.name,
                        IngredientQueryUtil.createHasChildrenExpression(ingredient),
                        IngredientQueryUtil.createIsOwnedExpression(ingredient, userId),
                        IngredientQueryUtil.createIsLikedExpression(ingredient, userId)))
                .from(ingredient)
                .where(
                        ingredient.parent.isNull(),
                        ingredient.deletedAt.isNull()
                )
                .orderBy(ingredient.name.asc());

        List<IngredientBasicDto> content = mainQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(ingredient.count())
                .from(ingredient)
                .where(
                        ingredient.parent.isNull(),
                        ingredient.deletedAt.isNull()
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<IngredientBasicDto> searchBasics(String keyword, Long userId, Pageable pageable) {
        QIngredient ingredient = QIngredient.ingredient;
        QIngredient parent = new QIngredient("parent");

        StringTemplate ingredientNameIgnoredBlanks = Expressions.stringTemplate(
                "replace({0}, {1}, {2})", ingredient.name, " ", "");

        JPAQuery<IngredientBasicDto> mainQuery = queryFactory
                .select(Projections.constructor(IngredientBasicDto.class,
                        ingredient.id,
                        ingredient.isParentCoupled,
                        parent.name,
                        ingredient.name,
                        IngredientQueryUtil.createHasChildrenExpression(ingredient),
                        IngredientQueryUtil.createIsOwnedExpression(ingredient, userId),
                        IngredientQueryUtil.createIsLikedExpression(ingredient, userId)))
                .from(ingredient)
                .leftJoin(ingredient.parent, parent)
                .where(
                        ingredientNameIgnoredBlanks.containsIgnoreCase(keyword),
                        ingredient.deletedAt.isNull()
                )
                .orderBy(ingredient.name.asc());

        List<IngredientBasicDto> content = mainQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(ingredient.count())
                .from(ingredient)
                .where(
                        ingredientNameIgnoredBlanks.containsIgnoreCase(keyword),
                        ingredient.deletedAt.isNull());

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

}
