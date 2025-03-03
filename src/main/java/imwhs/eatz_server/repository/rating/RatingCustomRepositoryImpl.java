package imwhs.eatz_server.repository.rating;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.eatzuser.QEatzUser;
import imwhs.eatz_server.domain.recipe.QRating;
import imwhs.eatz_server.dto.rating.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RatingCustomRepositoryImpl implements RatingCustomRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 특정 레시피에 달린 모든 평가를 조회합니다.<br/>
     * 평가 별 기본 정보 뿐 아니라 해당 평가를 등록한 사용자의 부가 정보를 함께 조회합니다.<br/>
     * 삭제 처리된 평가는 조회 대상에서 제외됩니다.
     * @param id 레시피 ID.
     */
    @Override
    public List<RatingWithUserDto> findRatingsByRecipe(Long id, Pageable pageable) {
        QRating rating = QRating.rating;
        QEatzUser user = QEatzUser.eatzUser;

        return queryFactory.select(
                Projections.constructor(RatingWithUserDto.class,
                        rating.id,
                        Projections.constructor(UserOfRatingDto.class,
                                user.id,
                                user.username
                        ),
                        rating.score,
                        rating.content,
                        rating.isHidden,
                        rating.createdAt,
                        rating.updatedAt)
                )
                .from(rating)
                .leftJoin(rating.author, user)
                .where(rating.author.eq(user)
                        .and(rating.deletedAt.isNull())
                )
                .offset(pageable.getOffset())
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
                .where(rating.author.id.eq(id)
                        .and(rating.deletedAt.isNull()))
                .fetchOne();
    }

}
