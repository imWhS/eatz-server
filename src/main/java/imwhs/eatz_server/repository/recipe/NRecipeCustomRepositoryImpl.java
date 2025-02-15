package imwhs.eatz_server.repository.recipe;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.eatzuser.QEatzUser;
import imwhs.eatz_server.domain.likes.LikesType;
import imwhs.eatz_server.domain.likes.QLikes;
import imwhs.eatz_server.domain.recipe.*;
import imwhs.eatz_server.dto.eatzuser.EatzUserWithRecipeSummaryDto;
import imwhs.eatz_server.dto.rating.RatingSummaryDto;
import imwhs.eatz_server.dto.recipe.CategoryDto;
import imwhs.eatz_server.dto.recipe.NRecipeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class NRecipeCustomRepositoryImpl implements NRecipeCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<NRecipeDto> findRecipeWithUser(Long id) {
        QRecipe recipe = QRecipe.recipe;
        QEatzUser user = QEatzUser.eatzUser;

        NRecipeDto recipeDto = queryFactory
                .select(
                        Projections.constructor(NRecipeDto.class,
                                recipe.id,
                                recipe.title,
                                recipe.description,
                                recipe.imageUrl,
                                recipe.createdAt,
                                recipe.updatedAt,
                                Projections.constructor(NRecipeDto.UserDto.class,
                                        user.id,
                                        user.username,
                                        user.imageUrl,
                                        JPAExpressions
                                                .select(recipe.count().intValue())
                                                .from(recipe)
                                                .where(recipe.user.eq(user))
                                )
                        ))
                .from(recipe)
                .join(recipe.user, user)
                .where(recipe.id.eq(id).and(recipe.deletedAt.isNull()))
                .fetchOne();

        return Optional.ofNullable(recipeDto);
    }

    public Optional<NRecipeDto> findRecipe_old(Long id) {
        QRecipe recipe = QRecipe.recipe;
        QRecipe subRecipe = new QRecipe("subRecipe");
        QEatzUser user = QEatzUser.eatzUser;
        QComment comment = QComment.comment;
        QRating rating = QRating.rating;
        QLikes likes = QLikes.likes;
        QRecipeCategory recipeCategory = QRecipeCategory.recipeCategory;

        NRecipeDto recipeDto = queryFactory
                .select(Projections.constructor(NRecipeDto.class,
                        recipe.id,
                        recipe.title,
                        recipe.description,
                        recipe.imageUrl,
                        recipe.createdAt,
                        recipe.updatedAt,

                        // Recipe와 xToOne 연관 관계인 EatzUser 관련 데이터를 가져옵니다.
                        Projections.constructor(EatzUserWithRecipeSummaryDto.class,
                                user.id,
                                user.username,
                                user.imageUrl,
                                JPAExpressions
                                        .select(subRecipe.count().intValue())
                                        .from(subRecipe)
                                        .where(subRecipe.user.eq(user).and(subRecipe.deletedAt.isNull()))
                        ),

                        // Recipe와 xToMany 연관 관계인 Comment 관련 데이터를 가져옵니다.
                        JPAExpressions
                                .select(comment.id.countDistinct().longValue())
                                .from(comment)
                                .where(comment.deletedAt.isNull()),

                        // Recipe와 xToMany 연관 관계인 Likes 관련 데이터를 가져옵니다.
                        JPAExpressions
                                .select(likes.id.countDistinct().longValue())
                                .from(likes)
                                .where(likes.isLiked.isTrue()),

                        // Recipe와 xToMany 연관 관계인 Category 관련 데이터를 가져옵니다.
                        JPAExpressions
                                .select(
                                        Projections.constructor(CategoryDto.class,
                                                recipeCategory.category.id,
                                                recipeCategory.category.name)
                                )
                                .from(recipeCategory)
                                .where(recipeCategory.recipe.eq(recipe))
                ))
                .from(recipe)
                .innerJoin(recipe.user, user)
                .leftJoin(comment).on(comment.recipe.eq(recipe))
                .leftJoin(likes).on(likes.entityId.eq(id).and(likes.type.eq(LikesType.COMMENT)))
                .where(recipe.id.eq(id).and(recipe.deletedAt.isNull()))
                .groupBy(recipe.id)
                .fetchOne();

        return Optional.ofNullable(recipeDto);
    }


    /**
     * 특정 ID에 해당하는 레시피 정보를 조회합니다.
     * @param id 레시피 ID.
     * @return 레시피 정보가 담긴 NRecipeDto 인스턴스. ID에 해당하는 레시피가 존재하지 않을 경우 Optional을 반환합니다.
     */
    public Optional<NRecipeDto> findRecipeById_old(Long id) {
        QRecipe recipe = QRecipe.recipe;
        QRecipe subRecipe = new QRecipe("subRecipe");
        QEatzUser user = QEatzUser.eatzUser;
        QComment comment = QComment.comment;
        QLikes likes = QLikes.likes;
        QRating rating = QRating.rating;

        // Recipe와 xToOne 관계인 EatzUser를 조인해 필요한 정보만 조회합니다.
        NRecipeDto recipeDto = queryFactory
                .select(Projections.constructor(NRecipeDto.class,
                        recipe.id,
                        recipe.title,
                        recipe.description,
                        recipe.imageUrl,
                        recipe.createdAt,
                        recipe.updatedAt,
                        Projections.constructor(EatzUserWithRecipeSummaryDto.class,
                                user.id,
                                user.username,
                                user.imageUrl,
                                // 사용자가 올린 모든 게시물 수 조회: 서브쿼리로 사용자가 올린 모든 게시물을 필터링한 후 집계합니다.
                                JPAExpressions
                                        .select(subRecipe.count().intValue())
                                        .from(subRecipe)
                                        .where(subRecipe.user.eq(user))
                        )
                ))
                .from(recipe)
                .innerJoin(recipe.user, user)
                .where(recipe.id.eq(id))
                .fetchOne();

        if (recipeDto == null) return Optional.empty();

        // Recipe와 xToMany 관계인 Comment를 조인해 필요한 정보만 조회합니다.
        Long commentCount = queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.recipe.id.eq(recipeDto.getId()))
                .fetchOne();

        recipeDto.setCommentCount(commentCount);

        // Recipe와 xToMany 관계인 Like를 조인해 필요한 정보만 조회합니다.
        Long likeCount = queryFactory
                .select(likes.count())
                .from(likes)
                .where(likes.type.eq(LikesType.RECIPE).and(likes.entityId.eq(id)))
                .fetchOne();

//        recipeDto.setLikeCount(likeCount);

        // Recipe와 xToMany 관계인 Rating을 조인해 필요한 정보만 조회합니다.
        RatingSummaryDto ratingSummaryDto = queryFactory
                .select(Projections.constructor(RatingSummaryDto.class,
                        rating.count(),
                        rating.score.avg()))
                .from(rating)
                .where(rating.recipe.id.eq(id))
                .fetchOne();

//        recipeDto.setRating(ratingSummaryDto);

        return Optional.of(recipeDto);
    }

}
