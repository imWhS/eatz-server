package imwhs.eatz_server.repository.kitchenware;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.QKitchenware;
import imwhs.eatz_server.dto.kitchenware.KitchenwareBasicDto;
import imwhs.eatz_server.repository.util.KitchenwareQueryUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class KitchenwareQueryRepositoryImpl implements KitchenwareQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<KitchenwareBasicDto> searchBasics(String keyword, Long userId, Pageable pageable) {
        QKitchenware kitchenware = QKitchenware.kitchenware;

        StringTemplate kitchenwareNameIgnoredBlanks = Expressions.stringTemplate(
                "replace({0}, {1}, {2})", kitchenware.name, " ", "");

        JPAQuery<KitchenwareBasicDto> mainQuery = queryFactory
                .select(Projections.constructor(KitchenwareBasicDto.class,
                        kitchenware.id,
                        kitchenware.name,
                        kitchenware.imageUrl,
                        KitchenwareQueryUtil.createIsOwnedExpression(kitchenware, userId)))
                .from(kitchenware)
                .where(
                        kitchenwareNameIgnoredBlanks.containsIgnoreCase(keyword),
                        kitchenware.deletedAt.isNull())
                .orderBy(kitchenware.name.asc());

        List<KitchenwareBasicDto> content = mainQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(kitchenware.count())
                .from(kitchenware)
                .where(
                        kitchenwareNameIgnoredBlanks.containsIgnoreCase(keyword),
                        kitchenware.deletedAt.isNull());

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

}
