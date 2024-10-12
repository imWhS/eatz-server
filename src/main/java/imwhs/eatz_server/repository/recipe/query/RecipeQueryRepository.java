package imwhs.eatz_server.repository.recipe.query;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.QComment;
import imwhs.eatz_server.domain.QEatzUser;
import imwhs.eatz_server.domain.QRating;
import imwhs.eatz_server.domain.QRecipe;
import imwhs.eatz_server.dto.eatzuser.EatzUserSummaryDto;
import imwhs.eatz_server.dto.rating.RatingSummaryDto;
import imwhs.eatz_server.dto.recipe.RecipeDetailResponseDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RecipeQueryRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public RecipeDetailResponseDto findRecipeDetailById(Long id) {
        /**
         * id에 대한 Recipe를 먼저 조회하되, Recipe와 연관 관계 설정된 EatzUser, Comment, Rating을
         * LEFT JOIN해서 한 번에 가져온다.
         */

        QRecipe recipe = QRecipe.recipe;
        QRecipe subRecipe = QRecipe.recipe;
        QEatzUser user = QEatzUser.eatzUser;
        QComment comment = QComment.comment;
        QRating rating = QRating.rating;

        return queryFactory
                .select(
                        Projections.constructor(RecipeDetailResponseDto.class, // RecipeDetailResponseDto로 프로젝션합니다.
                                recipe.id,
                                Projections.constructor(EatzUserSummaryDto.class, // EatzUserSummaryDto로 프로젝션합니다.
                                        user.id,
                                        user.username,
                                        JPAExpressions // 해당 레시피를 등록한 사용자가 레시피 앱에 등록한 모든 레시피 개수를 가져오는 서브 쿼리
                                                .select(subRecipe.count())
                                                .from(subRecipe) // user에 해당하는 모든 recipe를 조회합니다.
                                                .join(subRecipe.user, user)
                                                .where(subRecipe.user.eq(user))),
                                recipe.title,
                                recipe.url,
                                recipe.imageUrl,
                                recipe.description,
                                comment.count().intValue(),
                                Projections.constructor(RatingSummaryDto.class, // RatingSummaryDto로 프로젝션합니다.
                                        rating.count().intValue(),
                                        JPAExpressions
                                                .select(rating.score.avg())
                                                .from(rating)
                                                .where(rating.recipe.eq(recipe)))))
                .from(recipe) // id에 해당하는 recipe를 조회합니다.
                .leftJoin(recipe.user, user) // 해당 recipe와 연관 관계에 있는 user, comment, rating을 join해 함께 가져옵니다.
                .leftJoin(recipe.comments, comment)
                .leftJoin(recipe.ratings, rating)
                .where(recipe.id.eq(id))
                .fetchOne();
    }

    public RecipeDetailResponseDto findRecipeDetailById2(Long id) {
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
                                                .where(recipe.user.eq(user))
                                        ),
                                recipe.title,
                                recipe.url,
                                recipe.imageUrl,
                                recipe.description,
                                JPAExpressions
                                        .select(comment.count())
                                        .from(comment)
                                        .where(comment.recipe.eq(recipe)),
                                Projections.constructor(RatingSummaryDto.class,
                                        JPAExpressions
                                                .select(rating.count())
                                                .from(rating)
                                                .where(rating.recipe.eq(recipe)),
                                        JPAExpressions
                                                .select(rating.score.avg())
                                                .from(rating)
                                                .where(rating.recipe.eq(recipe))
                                        )
                                )
                )
                .from(recipe)
                .leftJoin(recipe.user, user) // Recipe와 EatzUser를 LEFT JOIN 합니다.
                .where(recipe.id.eq(id))
                .fetchFirst();
    }

}
