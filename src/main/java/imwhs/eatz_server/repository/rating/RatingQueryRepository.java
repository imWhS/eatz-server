package imwhs.eatz_server.repository.rating;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.QEatzUser;
import imwhs.eatz_server.domain.QRating;
import imwhs.eatz_server.domain.QRecipe;
import imwhs.eatz_server.dto.eatzuser.EatzUserSummaryDto;
import imwhs.eatz_server.dto.rating.*;
import imwhs.eatz_server.dto.recipe.RecipeSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RatingQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 식별자에 해당하는 평가의 기본 정보 및 평가를 등록한 사용자와 레시피 부가 정보를 함께 조회합니다.<br/>
     * 삭제 처리된 평가는 조회 대상에서 제외됩니다.
     * @param id 평가 식별자
     * @return Optional로 wrapping된 RatingDetailResponseDto. 평가의 상세 정보를 담은 DTO입니다.
     */
    public Optional<RatingDetailResponseDto> findRatingDetailById(Long id) {
        QRating rating = QRating.rating;
        QEatzUser user = QEatzUser.eatzUser;
        QRecipe recipe = QRecipe.recipe;

        return Optional.ofNullable(queryFactory
                .select(
                        Projections.constructor(RatingDetailResponseDto.class,
                                rating.id,
                                Projections.constructor(EatzUserSummaryDto.class,
                                        user.id,
                                        user.username,
                                        JPAExpressions
                                                .select(recipe.count().intValue())
                                                .from(recipe)
                                                .where(recipe.user.eq(user))
                                ),
                                Projections.constructor(RecipeSummaryDto.class,
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
     * @param id 레시피 식별자.
     * @param page 페이징 처리 시, 조회할 페이지 인덱스. 0부터 시작하며 선택 사항입니다.
     * @param size 페이징 처리 시, 하나의 페이지에 포함할 레시피 수. 선택 사항입니다.
     */
    public List<RatingByRecipeResponseDto> findRatingsByRecipe(Long id, int page, int size) {
        QRating rating = QRating.rating;
        QEatzUser user = QEatzUser.eatzUser;

        return queryFactory.select(
                Projections.constructor(RatingByRecipeResponseDto.class,
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
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }

    /**
     * 특정 레시피에 달린 총 평가 수를 조회합니다.<br/>
     * 삭제 처리된 평가는 조회 대상에서 제외합니다.
     * @param id 레시피 식별자
     * @return 총 평가 수
     */
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
     * @param id 사용자 식별자
     * @param page 페이징 처리 시, 조회할 페이지 인덱스. 0부터 시작하며 선택 사항입니다.
     * @param size 페이징 처리 시, 하나의 페이지에 포함할 레시피 수. 선택 사항입니다.
     */
    public List<RatingByUserResponseDto> findRatingsByUser(Long id, int page, int size) {
        QRating rating = QRating.rating;
        QRecipe recipe = QRecipe.recipe;

        return queryFactory.select(
                Projections.constructor(RatingByUserResponseDto.class,
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
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }

    /**
     * 특정 사용자가 등록한 총 평가 수를 조회합니다.<br/>
     * 삭제 처리된 평가는 조회 대상에서 제외합니다.
     * @param id 사용자 식별자
     * @return 총 평가 수
     */
    public Long countRatingsByUser(Long id) {
        QRating rating = QRating.rating;

        return queryFactory.select(rating.count())
                .from(rating)
                .where(rating.user.id.eq(id)
                        .and(rating.deletedAt.isNull()))
                .fetchOne();
    }



}
