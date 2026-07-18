package imwhs.eatz_server.repository.rating;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.eatzuser.QEatzUser;
import imwhs.eatz_server.domain.QRating;
import imwhs.eatz_server.domain.recipe.QRecipe;
import imwhs.eatz_server.dto.eatzuser.EatzUserEssentialDto;
import imwhs.eatz_server.dto.rating.*;
import imwhs.eatz_server.repository.util.BlockedQueryUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class RatingQueryRepositoryImpl implements RatingQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<RatingWithAuthorDto> findWithAuthorAllByRecipeId(Long id, Pageable pageable) {
        QRating rating = QRating.rating;
        QEatzUser author = QEatzUser.eatzUser;

        return queryFactory.select(
                Projections.constructor(RatingWithAuthorDto.class,
                        rating.id,
                        Projections.constructor(UserOfRatingDto.class,
                                author.id,
                                author.username),
                        rating.score,
                        rating.content,
                        rating.isHidden,
                        rating.createdAt,
                        rating.updatedAt)
                )
                .from(rating)
                .leftJoin(rating.author, author)
                .where(
                        rating.author.eq(author),
                        rating.deletedAt.isNull())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public Long countByRecipeId(Long id) {
        QRating rating = QRating.rating;

        return queryFactory.select(rating.count())
                .from(rating)
                .where(
                        rating.recipe.id.eq(id),
                        rating.deletedAt.isNull())
                .fetchOne();
    }

    @Override
    public Long countByAuthorId(Long id) {
        QRating rating = QRating.rating;

        return queryFactory.select(rating.count())
                .from(rating)
                .where(
                        rating.author.id.eq(id),
                        rating.deletedAt.isNull())
                .fetchOne();
    }

    @Override
    public Long countAllRatedRecipesByAuthorId(Long id) {
        QRating rating = QRating.rating;
        QRecipe recipe = QRecipe.recipe;

        return queryFactory
                .select(rating.recipe.id.countDistinct())
                .from(rating)
                .innerJoin(rating.recipe, recipe).on(recipe.deletedAt.isNull())
                .where(
                        rating.author.id.eq(id),
                        rating.deletedAt.isNull())
                .fetchOne();
    }

    @Override
    public Page<RatingBasicDto> findAllBasicsByRecipeId(Long id, Long userId, Pageable pageable) {
        QRating rating = QRating.rating;
        QEatzUser author = QEatzUser.eatzUser;

        Predicate[] filters = {
                rating.recipe.id.eq(id),
                rating.isHidden.isFalse(),
                rating.deletedAt.isNull(),
                BlockedQueryUtil.createBlockedUserFilter(userId, rating.author.id)};

        JPAQuery<RatingBasicDto> mainQuery = queryFactory
                .select(Projections.constructor(RatingBasicDto.class,
                        rating.id,
                        Projections.constructor(EatzUserEssentialDto.class,
                                author.id,
                                author.username,
                                author.imageUrl),
                        rating.score,
                        rating.content,
                        rating.createdAt,
                        rating.updatedAt))
                .from(rating)
                .innerJoin(rating.author, author)
                .where(filters)
                .orderBy(rating.createdAt.desc());

        List<RatingBasicDto> content = mainQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(rating.count())
                .from(rating)
                .where(filters);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

}
