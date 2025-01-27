package imwhs.eatz_server.repository.recipe;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.*;
import imwhs.eatz_server.dto.eatzuser.EatzUserSummaryDto;
import imwhs.eatz_server.dto.rating.RatingSummaryDto;
import imwhs.eatz_server.dto.recipe.RecipeDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * RecipeQueryRepository 클래스입니다.
 * <p>
 *     Recipe 엔티티의 복잡한 조회 쿼리를 처리하는 리포지토리입니다.
 *     QueryDSL을 기반으로 조회 쿼리를 생성, 실행합니다.
 * </p>
 */
@RequiredArgsConstructor
@Repository
public class RecipeQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * ID에 해당하는 레시피의 상세 정보를 조회합니다.
     * 삭제 처리된 레시피는 조회 대상에서 제외됩니다.
     * @param id 레시피 식별자.
     * @return Optional로 wrapping된 RecipeDetailResponseDto. 레시피의 상세 정보를 담은 DTO입니다.
     */
    public Optional<RecipeDetailDto> findRecipeDetailById(Long id) {
        QRecipe recipe = QRecipe.recipe;
        QEatzUser user = QEatzUser.eatzUser;
        QComment comment = QComment.comment;
        QRating rating = QRating.rating;
        QLikes likes = QLikes.likes;

        return Optional.ofNullable(queryFactory
                .select(
                        Projections.constructor(RecipeDetailDto.class,
                                recipe.id,
                                Projections.constructor(EatzUserSummaryDto.class,
                                        user.id,
                                        user.username,
                                        JPAExpressions
                                                .select(recipe.count().intValue())
                                                .from(recipe)
                                                .where(recipe.user.eq(user))),
                                recipe.title,
                                recipe.url,
                                recipe.imageUrl,
                                recipe.description,
                                comment.id.countDistinct().intValue(),
                                Projections.constructor(RatingSummaryDto.class,
                                        // 사용자는 레시피에 하나의 평가만 남길 수 있기 때문에, 평가 식별자 값 기준으로 distinct를 적용합니다.
                                        rating.id.countDistinct().intValue(),
                                        rating.score.avg().doubleValue()),
                                JPAExpressions
                                        .select(likes.count().longValue())
                                        .from(likes)
                                        .where(likes.entityId.eq(recipe.id))
                        )
                )
                .from(recipe)
                .leftJoin(recipe.user, user)
                .leftJoin(recipe.comments, comment)
                .leftJoin(recipe.ratings, rating)
                .where(recipe.id.eq(id)
                        .and(recipe.deletedAt.isNull())
                )
                .groupBy(recipe.id)
                .fetchOne());
    }

}
