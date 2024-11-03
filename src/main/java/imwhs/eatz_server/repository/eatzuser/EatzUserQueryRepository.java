package imwhs.eatz_server.repository.eatzuser;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.QEatzUser;
import imwhs.eatz_server.domain.QRecipe;
import imwhs.eatz_server.dto.PagedResponse;
import imwhs.eatz_server.dto.eatzuser.EatzUserSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * EatzUserQueryRepository 클래스입니다.
 * <p>
 *     EatzUser 엔티티에 대한 복잡한 조회 쿼리 위주로 처리하는 리포지토리입니다.
 *     QueryDSL을 기반으로 조회 쿼리를 생성, 실행합니다.
 * </p>
 */
@Repository
@RequiredArgsConstructor
public class EatzUserQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 모든 사용자의 기본 정보와 각 사용자가 등록한 레시피 수를 조회합니다.
     * @param page 페이징 처리 시, 조회할 페이지 인덱스. 0부터 시작하며 선택 사항입니다.
     * @param size 페이징 처리 시, 하나의 페이지에 포함할 레시피 수. 선택 사항입니다.
     * @return
     */
    public PagedResponse<EatzUserSummaryDto> findAllWithActivity(int page, int size) {
        QEatzUser eatzUser = QEatzUser.eatzUser;
        QRecipe recipe = QRecipe.recipe;

        // 모든 사용자를 조회하며, 동시에 각 사용자마다 등록한 레시피 수를 함께 집계한 데이터를 포함해
        // EatzUserSummaryDto로 반환합니다.
        List<EatzUserSummaryDto> items = queryFactory
                .select(
                        Projections.constructor(EatzUserSummaryDto.class,
                                eatzUser.id,
                                eatzUser.username,
                                recipe.count()
                        ))
                .from(eatzUser)
                .leftJoin(eatzUser.recipes, recipe)
                .groupBy(eatzUser.id)
                .offset((long) page * size)
                .limit(size)
                .fetch();

        // 총 사용자 수를 조회해 totalItems에 할당합니다.
        Long totalItems = Optional.ofNullable(queryFactory
                        .select(eatzUser.count())
                        .from(eatzUser)
                        .fetchOne())
                .orElse(0L);

        // 데이터 페이징 시 필요한 총 페이지 수를 계산합니다.
        // 소수점 이하 값에 대한 반올림 처리를 위해 totalItems를 double로 변환한 값을 계산에 사용합니다.
        int totalPages = (int) Math.ceil((double) totalItems / size);

        // 페이징 정보를 포함하는 PagedResponse로 감싼 후 데이터를 반환합니다.
        return PagedResponse.of(items, totalItems, totalPages, page, size);
    }

}