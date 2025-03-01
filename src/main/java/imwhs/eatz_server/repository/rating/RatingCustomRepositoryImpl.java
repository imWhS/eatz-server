package imwhs.eatz_server.repository.rating;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.eatzuser.QEatzUser;
import imwhs.eatz_server.domain.recipe.QRating;
import imwhs.eatz_server.domain.recipe.QRecipe;
import imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto;
import imwhs.eatz_server.dto.eatzuser.EatzUserSummaryDto;
import imwhs.eatz_server.dto.rating.*;
import imwhs.eatz_server.dto.recipe.RecipeBasicDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RatingCustomRepositoryImpl implements RatingCustomRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 식별자에 해당하는 평가의 기본 정보 및 평가를 등록한 사용자와 레시피 부가 정보를 함께 조회합니다.<br/>
     * 삭제 처리된 평가는 조회 대상에서 제외됩니다.
     * @param id 평가 식별자
     * @return Optional로 wrapping된 RatingDetailResponseDto. 평가의 상세 정보를 담은 DTO입니다.
     */
    @Override
    public Optional<RatingDetailDtoOld> findRatingDetailById(Long id) {
        QRating rating = QRating.rating;
        QEatzUser user = QEatzUser.eatzUser;
        QRecipe recipe = QRecipe.recipe;

        return Optional.ofNullable(queryFactory
                .select(
                        Projections.constructor(RatingDetailDtoOld.class,
                                rating.id,
                                Projections.constructor(EatzUserSummaryDto.class,
                                        user.id,
                                        user.username,
                                        JPAExpressions
                                                .select(recipe.count().intValue())
                                                .from(recipe)
                                                .where(recipe.user.eq(user))
                                ),
                                Projections.constructor(RecipeBasicDto.class,
                                        recipe.id,
                                        recipe.title,
                                        recipe.imageUrl
                                ),
                                rating.score,
                                rating.content
                        )
                )
                .from(rating)
                .leftJoin(rating.user, user)
                .leftJoin(rating.recipe, recipe)
                .where(rating.id.eq(id)
                        .and(rating.deletedAt.isNull()))
                .fetchOne()
        );
    }

    /**
     * 특정 레시피에 달린 모든 평가를 조회합니다.<br/>
     * 평가 별 기본 정보 뿐 아니라 해당 평가를 등록한 사용자의 부가 정보를 함께 조회합니다.<br/>
     * 삭제 처리된 평가는 조회 대상에서 제외됩니다.
     * @param id 레시피 ID.
     */
    @Override
    public List<RatingByRecipeDto> findRatingsByRecipe(Long id, Pageable pageable) {
        QRating rating = QRating.rating;
        QEatzUser user = QEatzUser.eatzUser;

        return queryFactory.select(
                Projections.constructor(RatingByRecipeDto.class,
                        rating.id,
                        Projections.constructor(RatingUserDto.class,
                                user.id,
                                user.username
                        ),
                        rating.score,
                        rating.content,
                        rating.isHidden,
                        rating.createdAt,
                        rating.updatedAt,
                        rating.deletedAt)
                )
                .from(rating)
                .leftJoin(rating.user, user)
                .where(rating.user.eq(user)
                        .and(rating.deletedAt.isNull())
                )
                .offset((long) pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    /**
     * 특정 레시피에 달린 총 평가 수를 조회합니다.<br/>
     * 삭제 처리된 평가는 조회 대상에서 제외합니다.
     * @param id 레시피 식별자
     * @return 총 평가 수
     */
    @Override
    public Long countRatingsByRecipe(Long id) {
        QRating rating = QRating.rating;

        return queryFactory.select(rating.count())
                .from(rating)
                .where(rating.recipe.id.eq(id)
                        .and(rating.deletedAt.isNull()))
                .fetchOne();

    }

    /**
     * 특정 사용자가 등록한 모든 평가를 조회합니다.<br/>
     * 평가 별 기본 정보 뿐 아니라 해당 평가를 등록한 사용자의 부가 정보를 함께 조회합니다.<br/>
     * 삭제 처리된 평가는 조회 대상에서 제외됩니다.
     * @param id 사용자 ID.
     */
    @Override
    public List<RatingByUserDto> findRatingsByUser(Long id, Pageable pageable) {
        QRating rating = QRating.rating;
        QRecipe recipe = QRecipe.recipe;

        return queryFactory.select(
                Projections.constructor(RatingByUserDto.class,
                        rating.id,
                        Projections.constructor(RatingRecipeDto.class,
                                recipe.id,
                                recipe.title,
                                recipe.description
                        ),
                        rating.score,
                        rating.content,
                        rating.isHidden,
                        rating.createdAt,
                        rating.updatedAt,
                        rating.deletedAt)
                )
                .from(rating)
                .leftJoin(rating.recipe, recipe)
                .where(rating.user.id.eq(id))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    /**
     * 특정 사용자가 등록한 총 평가 수를 조회합니다.<br/>
     * 삭제 처리된 평가는 조회 대상에서 제외합니다.
     * @param id 사용자 식별자
     * @return 총 평가 수
     */
    @Override
    public Long countRatingsByUser(Long id) {
        QRating rating = QRating.rating;

        return queryFactory.select(rating.count())
                .from(rating)
                .where(rating.user.id.eq(id)
                        .and(rating.deletedAt.isNull()))
                .fetchOne();
    }

}
