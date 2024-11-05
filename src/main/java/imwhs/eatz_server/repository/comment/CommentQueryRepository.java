package imwhs.eatz_server.repository.comment;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.QComment;
import imwhs.eatz_server.domain.QEatzUser;
import imwhs.eatz_server.domain.QRecipe;
import imwhs.eatz_server.dto.comment.CommentDetailResponseDto;
import imwhs.eatz_server.dto.comment.CommentResponseDto;
import imwhs.eatz_server.dto.comment.CommentUserDto;
import imwhs.eatz_server.dto.eatzuser.EatzUserSummaryDto;
import imwhs.eatz_server.dto.recipe.RecipeSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * CommentQueryRepository 클래스입니다.
 * <p>
 *     Comment 엔티티의 복잡한 조회 쿼리를 처리하는 리포지토리입니다.
 *     QueryDSL을 기반으로 조회 쿼리를 생성, 실행합니다.
 * </p>
 */
@Repository
@RequiredArgsConstructor
public class CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 식별자에 해당하는 댓글의 상세 정보를 조회합니다.<br/>
     * 삭제 처리된 댓글은 조회 대상에서 제외됩니다.
     * @param id 댓글 식별자
     * @return Optional로 wrapping된 CommentDetailResponseDto. 댓글의 상세 정보를 담은 DTO입니다.
     */
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

    /**
     * 특정 레시피에 달린 모든 댓글을 조회합니다.<br/>
     * 삭제 처리된 댓글은 조회 대상에서 제외됩니다.
     */
    public List<CommentResponseDto> findCommentsByRecipe(Long id) {
        QComment comment = QComment.comment;
        QEatzUser user = QEatzUser.eatzUser;

        return queryFactory
                .select(
                        Projections.constructor(CommentResponseDto.class,
                                comment.id,
                                Projections.constructor(CommentUserDto.class,
                                        user.id,
                                        user.username),
                                comment.content,
                                comment.isHidden,
                                comment.createdAt,
                                comment.updatedAt,
                                comment.deletedAt
                        )
                )
                .from(comment)
                .leftJoin(comment.user, user)
                .where(comment.recipe.id.eq(id)
                        .and(comment.deletedAt.isNull()))
                .fetch();
    }

}
