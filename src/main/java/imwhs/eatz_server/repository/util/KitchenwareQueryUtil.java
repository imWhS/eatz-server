package imwhs.eatz_server.repository.util;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import imwhs.eatz_server.domain.QKitchenware;
import imwhs.eatz_server.domain.pantry.QPantryKitchenware;

/**
 * Ingredient 엔티티와 관련된 공통 서브쿼리 및 동적 필터링 적용에 필요한 표현식을 제공하는 유틸리티 클래스입니다.
 */
public final class KitchenwareQueryUtil {

    private KitchenwareQueryUtil() {}

    /**
     * Kitchenware에 대한 특정 사용자의 보관함 추가(소유) 여부를 확인하는 QueryDSL 표현식을 생성합니다.
     * @param kitchenware 메인 쿼리의 Kitchenware 레코드에서 사용자의 소유 여부를 확인하기 위한 QKitchenware
     * @param userId 사용자의 ID
     * @return Kitchenware 소유 여부를 확인하는 BooleanExpression.
     *         사용자의 ID가 null일 경우 false를 반환합니다.
     */
    static public BooleanExpression createIsOwnedExpression(QKitchenware kitchenware, Long userId) {
        if (userId == null) { return Expressions.asBoolean(false); }

        QPantryKitchenware subPantryKitchenware = new QPantryKitchenware("subPantryKitchenware");

        return JPAExpressions
                .selectOne()
                .from(subPantryKitchenware)
                .where(
                        subPantryKitchenware.kitchenware.eq(kitchenware),
                        subPantryKitchenware.user.id.eq(userId),
                        subPantryKitchenware.deletedAt.isNull()
                )
                .exists();
    }

}
