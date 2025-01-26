package imwhs.eatz_server.repository.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.LikesType;
import imwhs.eatz_server.domain.QEatzUser;
import imwhs.eatz_server.domain.QLikes;
import imwhs.eatz_server.dto.LikeDetailDto;
import imwhs.eatz_server.dto.eatzuser.EatzUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class LikeQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * ID에 해당하는 항목에 대한 좋아요 상세 정보를 조회합니다.
     */

    public Optional<LikeDetailDto> findLikeDetailsOf(Long entityId, LikesType type) {
        QLikes likes = QLikes.likes;
        QEatzUser user = QEatzUser.eatzUser;

        return Optional.ofNullable(queryFactory
                        .select(Projections.constructor(LikeDetailDto.class,
                                likes.entityId,
                                likes.type,
                                likes.id.count().longValue(),
                                Projections.list(
                                        Projections.constructor(EatzUserDto.class, user))
                                ))
                        .from(likes)
                        .leftJoin(user).on(likes.user.eq(user).and(likes.isLiked.isTrue()))
                        .where(likes.entityId.eq(entityId).and(likes.type.eq(type).and(likes.isLiked.isTrue())))
                .fetchOne()
        );
    }

    public void test(Long entityId, LikesType type) {
        QLikes likes = QLikes.likes;
        QEatzUser user = QEatzUser.eatzUser;

        List<LikeDetailDto> transform = queryFactory
                .from(likes)
                .join(likes.user, user)
                .transform(
                        GroupBy.groupBy(likes.entityId, likes.type).list(
                                Projections.constructor(LikeDetailDto.class,
                                        likes.entityId,
                                        likes.type,
                                        likes.id.count().longValue(),
                                        GroupBy.list(Projections.constructor(EatzUserDto.class, user))
                                )
                        )
                );

        try {
            // ObjectMapper를 사용해 JSON 형식으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonOutput = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(transform);

            // JSON 출력
            System.out.println(jsonOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
