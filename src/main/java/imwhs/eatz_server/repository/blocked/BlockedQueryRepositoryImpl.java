package imwhs.eatz_server.repository.blocked;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.Blocked;
import imwhs.eatz_server.domain.QBlocked;
import imwhs.eatz_server.domain.eatzuser.QEatzUser;
import imwhs.eatz_server.dto.eatzuser.EatzUserEssentialDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class BlockedQueryRepositoryImpl implements BlockedQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Blocked> findAllBlockedUsersByBlockerIdAndDeletedAtIsNull(Long blockerId) {
        QBlocked blocked = QBlocked.blocked;
        QEatzUser blockedUser = QEatzUser.eatzUser;

        return queryFactory
                .selectFrom(blocked)
                .innerJoin(blocked.blockedUser, blockedUser).fetchJoin()
                .where(
                        blocked.blocker.id.eq(blockerId),
                        blocked.blocker.deletedAt.isNull(),
                        blocked.blockedUser.deletedAt.isNull()
                )
                .fetch();
    }

    @Override
    public Page<EatzUserEssentialDto> findAllBlockedUserEssentialsByBlockerIdAndDeletedAtIsNull(
            Long blockerId, Pageable pageable) {
        QBlocked blocked = QBlocked.blocked;
        QEatzUser blockedUser = QEatzUser.eatzUser;

        Predicate[] filters = {
                blocked.blocker.id.eq(blockerId),
                blocked.blocker.deletedAt.isNull(),
                blocked.blockedUser.deletedAt.isNull() };

        JPAQuery<EatzUserEssentialDto> mainQuery = queryFactory
                .select(Projections.constructor(EatzUserEssentialDto.class,
                        blockedUser.id,
                        blockedUser.username,
                        blockedUser.imageUrl))
                .from(blocked)
                .innerJoin(blocked.blockedUser, blockedUser)
                .where(filters)
                .orderBy(blockedUser.updatedAt.desc());

        List<EatzUserEssentialDto> content = mainQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(blocked.count())
                .from(blocked)
                .innerJoin(blocked.blockedUser, blockedUser)
                .where(filters);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

}
