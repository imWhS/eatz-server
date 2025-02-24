package imwhs.eatz_server.repository.liked;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.liked.LikedType;
import imwhs.eatz_server.domain.eatzuser.QEatzUser;
import imwhs.eatz_server.domain.liked.QLiked;
import imwhs.eatz_server.dto.liked.LikedDetailDto;
import imwhs.eatz_server.dto.eatzuser.EatzUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.querydsl.core.types.Projections.list;

@RequiredArgsConstructor
@Repository
public class LikedQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * ID에 해당하는 항목에 대한 좋아요 상세 정보를 조회합니다.
     */

    public void findAll(Long entityId, LikedType type) throws JsonProcessingException {
        QLiked liked = QLiked.liked;
        QEatzUser user = QEatzUser.eatzUser;

        LikedDetailDto likedDetailDto = queryFactory
                .select(Projections.constructor(LikedDetailDto.class,
                        liked.entityId,
                        liked.type,
                        liked.user.count(),
                        list(Projections.constructor(EatzUserDto.class, user))
                ))
                .from(liked)
                .innerJoin(liked.user, user)
                .where(liked.entityId.eq(entityId).and(liked.type.eq(type)).and(liked.isLiked.isTrue()))
                .groupBy(liked.entityId, liked.type)
                .orderBy(liked.updatedAt.desc())
                .fetchOne();

        ObjectMapper objectMapper = new ObjectMapper();
        String output = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(likedDetailDto);
    }


}
