package imwhs.eatz_server.repository.comment;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.eatzuser.QEatzUser;
import imwhs.eatz_server.domain.recipe.QComment;
import imwhs.eatz_server.domain.recipe.QRecipe;
import imwhs.eatz_server.dto.comment.CommentByRecipeResponseDto;
import imwhs.eatz_server.dto.comment.CommentDetailResponseDto;
import imwhs.eatz_server.dto.comment.CommentUserDto;
import imwhs.eatz_server.dto.eatzuser.EatzUserSummaryDto;
import imwhs.eatz_server.dto.recipe.RecipeSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CommentCustomRepositoryImpl implements CommentCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<CommentDetailResponseDto> findCommentDetailById(Long id) {
        QComment comment = QComment.comment;
        QEatzUser user = QEatzUser.eatzUser;
        QRecipe recipe = QRecipe.recipe;

        return Optional.ofNullable(queryFactory
                .select(
                        Projections.constructor(CommentDetailResponseDto.class,
                                comment.id,
                                Projections.constructor(EatzUserSummaryDto.class,
                                        user.id,
                                        user.username,
                                        JPAExpressions
                                                .select(recipe.count().intValue())
                                                .from(recipe)
                                                .where(recipe.user.eq(user))),
                                Projections.constructor(RecipeSummaryDto.class,
                                        recipe.id,
                                        recipe.title,
                                        recipe.imageUrl),
                                comment.content)
                )
                .from(comment)
                .leftJoin(comment.user, user)
                .leftJoin(comment.recipe, recipe)
                .where(comment.id.eq(id)
                        .and(comment.deletedAt.isNull()))
                .fetchOne()
        );
    }

}
