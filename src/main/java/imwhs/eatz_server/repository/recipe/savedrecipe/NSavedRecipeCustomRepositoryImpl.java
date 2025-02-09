package imwhs.eatz_server.repository.recipe.savedrecipe;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.eatzuser.QEatzUser;
import imwhs.eatz_server.domain.likes.LikesType;
import imwhs.eatz_server.domain.likes.QLikes;
import imwhs.eatz_server.domain.recipe.QComment;
import imwhs.eatz_server.domain.recipe.QNSavedRecipe;
import imwhs.eatz_server.domain.recipe.QRating;
import imwhs.eatz_server.domain.recipe.QRecipe;
import imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto;
import imwhs.eatz_server.dto.rating.RatingSummaryDto;
import imwhs.eatz_server.dto.recipe.NRecipeDto;
import imwhs.eatz_server.dto.recipe.RecipeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class NSavedRecipeCustomRepositoryImpl implements NSavedRecipeCustomRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RecipeDto> findSavedRecipesByUsername(String username) {
        QNSavedRecipe savedRecipe = QNSavedRecipe.nSavedRecipe;
        QEatzUser user = QEatzUser.eatzUser;
        QRecipe recipe = QRecipe.recipe;
        QComment comment = QComment.comment;
        QLikes likes = QLikes.likes;
        QRating rating = QRating.rating;

        List<RecipeDto> recipeDtos = queryFactory
                .select(Projections.constructor(RecipeDto.class,
                        recipe,
                        user
                ))
                .from(savedRecipe)
                .join(savedRecipe.user, user)
                .join(savedRecipe.recipe, recipe)
                .where(savedRecipe.user.username.eq(username))
                .fetch();

        List<NRecipeDto> nRecipeDtos = queryFactory
                .select(Projections.constructor(NRecipeDto.class,
                        recipe,
                        user
                ))
                .from(savedRecipe)
                .join(savedRecipe.user, user)
                .join(savedRecipe.recipe, recipe)
                .where(savedRecipe.user.username.eq(username))
                .fetch();

        List<Long> recipeIds = recipeDtos.stream().map(RecipeDto::getId).toList();

        Map<Long, Long> likeCountsByRecipeId = queryFactory
                .from(likes)
                .where(likes.entityId.in(recipeIds).and(likes.type.eq(LikesType.RECIPE)))
                .groupBy(likes.entityId)
                .transform(GroupBy.groupBy(likes.entityId).as(likes.count()));

        Map<Long, RatingSummaryDto> ratingSummariesByRecipeId = queryFactory
                .from(rating)
                .where(rating.recipe.id.in(recipeIds))
                .groupBy(rating.recipe.id)
                .transform(GroupBy.groupBy(rating.recipe.id).as(
                        Projections.constructor(RatingSummaryDto.class,
                                rating.count().intValue(),
                                rating.score.avg().doubleValue().coalesce(0.0)
                        )
                ));


//        recipeDtos.forEach(dto -> {
//            dto.setLikeCount(likeCountsByRecipeId.get(dto.getId()));
//            dto.
//        });


        return recipeDtos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EatzUserBasicDto> findSavedUsersByRecipeId(Long id) {
        QNSavedRecipe savedRecipe = QNSavedRecipe.nSavedRecipe;
        QEatzUser user = QEatzUser.eatzUser;

        List<EatzUserBasicDto> dtos = queryFactory
                .select(
                        Projections.constructor(EatzUserBasicDto.class,
                                user.id,
                                user.username,
                                user.email,
                                user.imageUrl
                        )
                )
                .from(savedRecipe)
                .innerJoin(savedRecipe.user, user)
                .where(savedRecipe.recipe.id.eq(id))
                .groupBy(user.id)
                .fetch();

        return dtos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean deleteSavedRecipe(Long userId, Long id) {
        QNSavedRecipe savedRecipe = QNSavedRecipe.nSavedRecipe;

        long deletedCount = queryFactory
                .delete(savedRecipe)
                .where(savedRecipe.user.id.eq(userId)
                        .and(savedRecipe.recipe.id.eq(id)))
                .execute();

        return deletedCount == 1;
    }

}
