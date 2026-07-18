package imwhs.eatz_server.repository.util;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import imwhs.eatz_server.domain.QIngredient;
import imwhs.eatz_server.domain.pantry.QPantryIngredient;
import imwhs.eatz_server.domain.liked.QLikedIngredient;

/**
 * Ingredient 엔티티와 관련된 공통 서브쿼리 및 동적 필터링 적용에 필요한 표현식을 제공하는 유틸리티 클래스입니다.
 */
public final class IngredientQueryUtil {

    private IngredientQueryUtil() {}

    static public BooleanExpression createHasChildrenExpression(QIngredient parent) {
        QIngredient subIngredient = new QIngredient("subIngredient");

        return JPAExpressions
                .selectOne()
                .from(subIngredient)
                .where(
                        subIngredient.parent.eq(parent),
                        subIngredient.deletedAt.isNull()
                )
                .exists();
    }

    /**
     * Ingredient에 대한 특정 사용자의 보관함 추가(소유) 여부를 확인하는 QueryDSL 표현식을 생성합니다.
     * @param ingredient 메인 쿼리의 Ingredient 레코드에서 사용자의 소유 여부를 확인하기 위한 QIngredient
     * @param userId 사용자의 ID
     * @return Ingredient 소유 여부를 확인하는 BooleanExpression.
     *         사용자의 ID가 null일 경우 false를 반환합니다.
     */
    static public BooleanExpression createIsOwnedExpression(QIngredient ingredient, Long userId) {
        if (userId == null) { return Expressions.asBoolean(false); }

        QPantryIngredient subPantryIngredient = new QPantryIngredient("subPantryIngredient");

        return JPAExpressions
                .selectOne()
                .from(subPantryIngredient)
                .where(
                        subPantryIngredient.ingredient.eq(ingredient),
                        subPantryIngredient.user.id.eq(userId),
                        subPantryIngredient.deletedAt.isNull()
                )
                .exists();
    }

    /**
     * Ingredient에 대한 특정 사용자의 LikedIngredient 레코드 존재(재료를 좋아하는 사용자) 여부를
     * 서브쿼리를 통해 확인하는 QueryDSL 표현식을 생성합니다.
     * @param ingredient 메인 쿼리의 Ingredient 레코드에서 서브쿼리 연결 시 외래 키를 참조하기 위한 QIngredient
     * @param userId 사용자의 ID
     * @return Recipe와 연관 관계인 LikedIngredient 레코드가 하나라도 존재하는지 여부를 확인하는 BooleanExpression.
     *         사용자의 ID가 null일 경우 false를 반환합니다.
     */
    static public BooleanExpression createIsLikedExpression(QIngredient ingredient, Long userId) {
        if (userId == null) { return Expressions.asBoolean(false); }

        QLikedIngredient subLikedIngredient =  new QLikedIngredient("subLikedIngredient");

        return JPAExpressions
                .selectOne()
                .from(subLikedIngredient)
                .where(
                        subLikedIngredient.ingredient.eq(ingredient),
                        subLikedIngredient.user.id.eq(userId),
                        subLikedIngredient.isLiked.isTrue(),
                        subLikedIngredient.deletedAt.isNull()
                )
                .exists();
    }

}
