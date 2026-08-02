package imwhs.eatz_server.repository.pantry.kitchenware;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.pantry.QPantryKitchenware;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class PantryKitchenwareQueryRepositoryImpl implements PantryKitchenwareQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Set<Long> findAllKitchenwareIdsByUserId(Long id, List<Long> kitchenwareIds) {
        QPantryKitchenware pantryKitchenware = QPantryKitchenware.pantryKitchenware;

        List<Long> ids = queryFactory
                .select(pantryKitchenware.kitchenware.id)
                .from(pantryKitchenware)
                .where(
                        createKitchenwareIdsFilterExpression(kitchenwareIds, pantryKitchenware),
                        pantryKitchenware.user.id.eq(id),
                        pantryKitchenware.deletedAt.isNull()
                )
                .fetch();

        return new HashSet<>(ids);
    }

    private static BooleanExpression createKitchenwareIdsFilterExpression(
            List<Long> kitchenwareIds, QPantryKitchenware pantryKitchenware) {
        if (kitchenwareIds == null || kitchenwareIds.isEmpty()) { return null; }
        return pantryKitchenware.kitchenware.id.in(kitchenwareIds);
    }

}
