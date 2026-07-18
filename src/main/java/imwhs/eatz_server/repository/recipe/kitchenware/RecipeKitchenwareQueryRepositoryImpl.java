package imwhs.eatz_server.repository.recipe.kitchenware;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.QKitchenware;
import imwhs.eatz_server.domain.recipe.QRecipeKitchenware;
import imwhs.eatz_server.dto.kitchenware.KitchenwareEssentialDto;
import imwhs.eatz_server.dto.recipe.kitchenware.KitchenwareRequirementDto;
import imwhs.eatz_server.repository.util.KitchenwareQueryUtil;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RecipeKitchenwareQueryRepositoryImpl implements RecipeKitchenwareQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Map<Long, List<KitchenwareEssentialDto>> findAllMissingEssentials(
            List<Long> recipeIds,
            List<Long> kitchenwareIds) {
        if (recipeIds == null || recipeIds.isEmpty()) { return Collections.emptyMap(); }

        QRecipeKitchenware recipeKitchenware = QRecipeKitchenware.recipeKitchenware;
        QKitchenware kitchenware = QKitchenware.kitchenware;

        return queryFactory
                .from(recipeKitchenware)
                .innerJoin(recipeKitchenware.kitchenware, kitchenware).on(kitchenware.deletedAt.isNull())
                .where(
                        recipeKitchenware.recipe.id.in(recipeIds),
                        recipeKitchenware.deletedAt.isNull(),
                        createHasMissingKitchenwaresExpression(kitchenwareIds, kitchenware))
                .transform(
                        GroupBy.groupBy(recipeKitchenware.recipe.id).as(
                                GroupBy.list(
                                        Projections.constructor(KitchenwareEssentialDto.class,
                                                kitchenware.id,
                                                kitchenware.name,
                                                kitchenware.imageUrl))));
    }

    @Override
    public Map<Long, List<KitchenwareEssentialDto>> findAllEssentialsByRecipeIds(List<Long> ids) {
        QRecipeKitchenware recipeKitchenware = QRecipeKitchenware.recipeKitchenware;
        QKitchenware kitchenware = QKitchenware.kitchenware;

        return queryFactory
                .from(recipeKitchenware)
                .innerJoin(recipeKitchenware.kitchenware, kitchenware).on(kitchenware.deletedAt.isNull())
                .where(
                        recipeKitchenware.recipe.id.in(ids),
                        recipeKitchenware.deletedAt.isNull())
                .transform(
                        GroupBy.groupBy(recipeKitchenware.recipe.id).as(
                                GroupBy.list(
                                        Projections.constructor(KitchenwareEssentialDto.class,
                                                kitchenware.id,
                                                kitchenware.name,
                                                kitchenware.imageUrl))));
    }

    @Override
    public List<KitchenwareRequirementDto> findAllKitchenwareRequirementsByRecipeId(Long id, Long userId) {
        QRecipeKitchenware recipeKitchenware = QRecipeKitchenware.recipeKitchenware;
        QKitchenware kitchenware = QKitchenware.kitchenware;

        return queryFactory
                .select(Projections.constructor(KitchenwareRequirementDto.class,
                        kitchenware.id,
                        kitchenware.name,
                        kitchenware.imageUrl,
                        KitchenwareQueryUtil.createIsOwnedExpression(kitchenware, userId)
                        ))
                .from(recipeKitchenware)
                .innerJoin(recipeKitchenware.kitchenware, kitchenware).on(kitchenware.deletedAt.isNull())
                .where(
                        recipeKitchenware.recipe.id.eq(id),
                        recipeKitchenware.deletedAt.isNull()
                )
                .fetch();
    }

    @Nullable
    private static BooleanExpression createHasMissingKitchenwaresExpression(
            List<Long> kitchenwareIdsByUser,
            QKitchenware kitchenware) {
        // QueryDSL의 notIn의 파라미터로 null 또는 빈 컬렉션이 전달돼선 안 되기 때문에,
        // kitchenwareIdsByUser가 null이거나 비어있으면 notIn이 아닌, null 자체를 반환합니다.
        return kitchenwareIdsByUser == null ||  kitchenwareIdsByUser.isEmpty()
                ? null
                : kitchenware.id.notIn(kitchenwareIdsByUser);
    }

}
