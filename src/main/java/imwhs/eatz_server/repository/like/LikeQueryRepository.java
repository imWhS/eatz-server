package imwhs.eatz_server.repository.like;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.LikesType;
import imwhs.eatz_server.domain.QEatzUser;
import imwhs.eatz_server.domain.QLikes;
import imwhs.eatz_server.dto.LikeDetailDto;
import imwhs.eatz_server.dto.eatzuser.EatzUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.querydsl.core.types.Projections.list;

/*
        try {
            // ObjectMapper를 사용해 JSON 형식으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonOutput = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(transform);

            // JSON 출력
            System.out.println(jsonOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
 */

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

        LikeDetailDto likeDetailDto = queryFactory
                .select(Projections.constructor(LikeDetailDto.class,
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
        String output = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(likeDetailDto);
    }


}
