package imwhs.eatz_server.repository.like;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.likes.LikesType;
import imwhs.eatz_server.domain.eatzuser.QEatzUser;
import imwhs.eatz_server.domain.likes.QLikes;
import imwhs.eatz_server.dto.likes.LikesDetailDto;
import imwhs.eatz_server.dto.eatzuser.EatzUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.querydsl.core.types.Projections.list;

@RequiredArgsConstructor
@Repository
public class LikeQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * ID에 해당하는 항목에 대한 좋아요 상세 정보를 조회합니다.
     */

    public void findAll(Long entityId, LikesType type) throws JsonProcessingException {
        QLikes likes = QLikes.likes;
        QEatzUser user = QEatzUser.eatzUser;

        LikesDetailDto likesDetailDto = queryFactory
                .select(Projections.constructor(LikesDetailDto.class,
                        likes.entityId,
                        likes.type,
                        likes.user.count(),
                        list(Projections.constructor(EatzUserDto.class, user))
                ))
                .from(likes)
                .innerJoin(likes.user, user)
                .where(likes.entityId.eq(entityId).and(likes.type.eq(type)).and(likes.isLiked.isTrue()))
                .groupBy(likes.entityId, likes.type)
                .orderBy(likes.updatedAt.desc())
                .fetchOne();

        ObjectMapper objectMapper = new ObjectMapper();
        String output = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(likesDetailDto);
    }


}
