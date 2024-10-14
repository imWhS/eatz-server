package imwhs.eatz_server.repository.recipe.query;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.*;
import imwhs.eatz_server.dto.eatzuser.EatzUserSummaryDto;
import imwhs.eatz_server.dto.rating.RatingSummaryDto;
import imwhs.eatz_server.dto.recipe.RecipeDetailResponseDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RecipeQueryRepository {

    private final JPAQueryFactory queryFactory;

    public RecipeDetailResponseDto findRecipeDetailById(Long id) {
        QRecipe recipe = QRecipe.recipe;
        QEatzUser user = QEatzUser.eatzUser;
        QComment comment = QComment.comment;
        QRating rating = QRating.rating;

        return queryFactory
                .select(
                        Projections.constructor(RecipeDetailResponseDto.class,
                                recipe.id,
                                Projections.constructor(EatzUserSummaryDto.class,
                                        user.id,
                                        user.username,
                                        JPAExpressions
                                                .select(recipe.count())
                                                .from(recipe)
                                                .where(recipe.user.id.eq(user.id))),
                                recipe.title,
                                recipe.url,
                                recipe.imageUrl,
                                recipe.description,
                                comment.id.countDistinct().intValue(),
                                Projections.constructor(RatingSummaryDto.class,
                                        // 사용자는 레시피에 하나의 평가만 남길 수 있기 때문에, 평가 식별자 값 기준으로 distinct를 적용합니다.
                                        rating.countDistinct().intValue(),
                                        rating.score.avg())
                        )
                )
                .from(recipe)
                .leftJoin(recipe.user, user)
                .leftJoin(recipe.comments, comment)
                .leftJoin(recipe.ratings, rating)
                .where(recipe.id.eq(id))
                .groupBy(recipe.id)
                .fetchOne();
    }

}
