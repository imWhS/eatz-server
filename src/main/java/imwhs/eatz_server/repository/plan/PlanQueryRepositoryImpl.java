package imwhs.eatz_server.repository.plan;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.QPlan;
import imwhs.eatz_server.domain.eatzuser.QEatzUser;
import imwhs.eatz_server.domain.recipe.*;
import imwhs.eatz_server.dto.plan.PlanBasicDto;
import imwhs.eatz_server.dto.plan.PlanDetailDto;
import imwhs.eatz_server.repository.util.BlockedQueryUtil;
import imwhs.eatz_server.repository.util.RecipeQueryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class PlanQueryRepositoryImpl implements PlanQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PlanDetailDto> findAllDetailsByUserId(
            Long id,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        QPlan plan = QPlan.plan;
        QRecipe recipe = QRecipe.recipe;
        QEatzUser author = QEatzUser.eatzUser;

        JPAQuery<PlanDetailDto> mainQuery = queryFactory.select(Projections.constructor(PlanDetailDto.class,
                        plan.id,
                        plan.user.id,
                        plan.scheduledAt,
                        recipe.id,
                        author.id,
                        author.username,
                        recipe.title,
                        recipe.imageUrl,
                        recipe.cookingTime,
                        recipe.prepTime,
                        RecipeQueryUtil.getRatingCount(recipe),
                        RecipeQueryUtil.getRatingAverageScore(recipe),
                        RecipeQueryUtil.createIsAuthorExpression(recipe, id),
                        RecipeQueryUtil.createIsLikedExpression(recipe, id),
                        RecipeQueryUtil.createIsSavedExpression(recipe, id)))
                .from(plan)
                .innerJoin(plan.recipe, recipe).on( // TODO: LEFT JOIN - ON
                        recipe.deletedAt.isNull(),
                        BlockedQueryUtil.createBlockedUserFilter(id, recipe.author.id))
                .leftJoin(recipe.author, author)
                .where(
                        plan.user.id.eq(id),
                        plan.deletedAt.isNull(),
                        createScheduledAtFilter(plan, startDate, endDate));

        mainQuery.orderBy(
                plan.priority.asc(), // 우선 순위가 높은 플랜부터 정렬합니다. Ex. 0, 1...
                plan.updatedAt.asc() // 플래너에 먼저 추가된 플랜부터 표시합니다.
        );

        return mainQuery.fetch();
    }

    @Override
    public List<LocalDateTime> findScheduledDatesByUserIdAndRecipeId(
            Long userId,
            Long recipeId,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        QPlan plan = QPlan.plan;
        QRecipe recipe = QRecipe.recipe;

        JPAQuery<LocalDateTime> mainQuery = queryFactory
                .select(plan.scheduledAt)
                .from(plan)
                .innerJoin(plan.recipe, recipe).on(
                        recipe.deletedAt.isNull(),
                        BlockedQueryUtil.createBlockedUserFilter(userId, recipe.author.id))
                .where(
                        plan.user.id.eq(userId),
                        plan.recipe.id.eq(recipeId),
                        plan.deletedAt.isNull(),
                        createScheduledAtFilter(plan, startDate, endDate));

        mainQuery.orderBy(plan.scheduledAt.desc());

        return mainQuery.fetch();
    }

    @Override
    public List<PlanBasicDto> findAllBasicsByUserId(
            Long id,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        QPlan plan = QPlan.plan;
        QRecipe recipe = QRecipe.recipe;
        QEatzUser author = QEatzUser.eatzUser;

        JPAQuery<PlanBasicDto> mainQuery = queryFactory
                .select(Projections.constructor(PlanBasicDto.class,
                        plan.id,
                        plan.scheduledAt,
                        recipe.id,
                        recipe.title,
                        recipe.imageUrl,
                        recipe.cookingTime,
                        recipe.prepTime,
                        author.id,
                        author.username,
                        RecipeQueryUtil.createIsAuthorExpression(recipe, id),
                        RecipeQueryUtil.createIsLikedExpression(recipe, id),
                        RecipeQueryUtil.createIsSavedExpression(recipe, id)))
                .from(plan)
                .innerJoin(plan.recipe, recipe).on(
                        recipe.deletedAt.isNull(),
                        BlockedQueryUtil.createBlockedUserFilter(id, recipe.author.id))
                .leftJoin(recipe.author, author)
                .where(
                        plan.user.id.eq(id),
                        plan.deletedAt.isNull(),
                        createScheduledAtFilter(plan, startDate, endDate));

//        if (startDate != null) { mainQuery.where(plan.scheduledAt.goe(startDate)); }
//        if (endDate != null) { mainQuery.where(plan.scheduledAt.loe(endDate)); }

        mainQuery.orderBy(
                plan.priority.asc(), // 우선 순위가 높은 플랜부터 정렬합니다. Ex. 0, 1...
                plan.updatedAt.asc() // 플래너에 먼저 추가된 플랜부터 표시합니다.
        );

        return mainQuery.fetch();
    }

    private static BooleanExpression createScheduledAtFilter(
            QPlan plan,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        if (startDate == null && endDate == null) { return null; }
        if (startDate == null) { return plan.scheduledAt.loe(endDate); }
        if (endDate == null) { return plan.scheduledAt.goe(startDate); }
        return plan.scheduledAt.goe(startDate).and(plan.scheduledAt.loe(endDate));
    }

}
