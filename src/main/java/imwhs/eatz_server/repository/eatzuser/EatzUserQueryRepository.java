package imwhs.eatz_server.repository.eatzuser;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.eatzuser.QEatzUser;
import imwhs.eatz_server.domain.recipe.QRecipe;
import imwhs.eatz_server.dto.eatzuser.EatzUserSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * EatzUserQueryRepository 클래스입니다.
 * <p>
 *     EatzUser 엔티티의 복잡한 조회 쿼리를 처리하는 리포지토리입니다.
 *     QueryDSL을 기반으로 조회 쿼리를 생성, 실행합니다.
 * </p>
 */
@Repository
@RequiredArgsConstructor
public class EatzUserQueryRepository {

    private final JPAQueryFactory queryFactory;

    // TODO: 데이터 조회, 페이징 처리 별도의 메서드로 분리
    //  분리
    /**
     * 모든 사용자의 기본 정보와 각 사용자가 등록한 레시피 수를 조회합니다.
     * @param pageable 페이징 정보.
     * @return 페이징 처리된 EatzUserSummaryDto.
     */
    public Page<EatzUserSummaryDto> findAllWithActivity(Pageable pageable) {
//        QEatzUser eatzUser = QEatzUser.eatzUser;
//        QRecipe recipe = QRecipe.recipe;
//
//        // 모든 사용자를 조회하며, 동시에 각 사용자마다 등록한 레시피 수를 함께 집계한 데이터를 포함해
//        // EatzUserSummaryDto로 반환합니다.
//        JPAQuery<EatzUserSummaryDto> query = queryFactory
//                .select(
//                        Projections.constructor(EatzUserSummaryDto.class,
//                                eatzUser.id,
//                                eatzUser.username,
//                                recipe.count().intValue()
//                        ))
//                .from(eatzUser)
//                .leftJoin(eatzUser.recipes, recipe)
//                .groupBy(eatzUser.id);
//
//        // 페이징 적용이 필요한 경우에만 offset, limit를 쿼리에 적용합니다.
//        if (!pageable.isUnpaged()) {
//            query.offset(pageable.getOffset())
//                    .limit(pageable.getPageSize());
//        }
//
//        List<EatzUserSummaryDto> items = query.fetch();
//
//        // 총 사용자 수를 조회해 totalItems에 할당합니다.
//        Long totalItems =
//                Optional.ofNullable(queryFactory
//                        .select(eatzUser.count())
//                        .from(eatzUser)
//                        .fetchOne())
//                .orElse(0L);
//
//        // 페이징 정보를 포함하는 Page로 감싼 후 데이터를 반환합니다.
//        return new PageImpl<>(items, pageable, totalItems);
        return null;
    }

}