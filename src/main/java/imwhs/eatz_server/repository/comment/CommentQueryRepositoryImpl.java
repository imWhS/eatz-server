package imwhs.eatz_server.repository.comment;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.QComment;
import imwhs.eatz_server.domain.eatzuser.QEatzUser;
import imwhs.eatz_server.domain.recipe.QRecipe;
import imwhs.eatz_server.dto.comment.CommentBasicDto;
import imwhs.eatz_server.dto.eatzuser.EatzUserEssentialDto;
import imwhs.eatz_server.repository.util.BlockedQueryUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CommentQueryRepositoryImpl implements CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CommentBasicDto> findAllBasicsByRecipeId(Long id, Long userId, Pageable pageable) {
        QComment comment = QComment.comment;
        QEatzUser author = QEatzUser.eatzUser;

        Predicate[] filters = {
                comment.recipe.id.eq(id),
                comment.isHidden.isFalse(),
                comment.deletedAt.isNull(),
                BlockedQueryUtil.createBlockedUserFilter(userId, comment.author.id)};

        JPAQuery<CommentBasicDto> mainQuery = queryFactory
                .select(Projections.constructor(CommentBasicDto.class,
                        comment.id,
                        comment.content,
                        Projections.constructor(EatzUserEssentialDto.class,
                                author.id,
                                author.username,
                                author.imageUrl),
                        comment.isHidden,
                        comment.createdAt,
                        comment.updatedAt))
                .from(comment)
                .innerJoin(comment.author, author)
                .where(filters)
                .orderBy(comment.createdAt.desc());

        List<CommentBasicDto> content = mainQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(comment.count())
                .from(comment)
                .where(filters);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
