package imwhs.eatz_server.repository.util;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import imwhs.eatz_server.domain.QBlocked;

/**
 * Blocked 엔티티와 관련된 공통 서브쿼리 및 동적 필터링 적용에 필요한 표현식을 제공하는 유틸리티 클래스입니다.
 */
public final class BlockedQueryUtil {

    private BlockedQueryUtil() {}

    public static BooleanExpression createBlockedUserFilter(Long blockerId, NumberPath<Long> blockedUserIdPath) {
        if (blockerId == null) return null;

        QBlocked subBlocked = new QBlocked("subBlocked");

        return JPAExpressions
                .selectOne()
                .from(subBlocked)
                .where(
                        subBlocked.blocker.id.eq(blockerId),
                        subBlocked.blockedUser.id.eq(blockedUserIdPath),
                        subBlocked.deletedAt.isNull()
                )
                .notExists();
    }

}
