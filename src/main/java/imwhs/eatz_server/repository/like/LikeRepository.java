package imwhs.eatz_server.repository.like;

import imwhs.eatz_server.domain.Likes;
import imwhs.eatz_server.domain.LikesType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {

    @Query
    Optional<Likes> findByUserIdAndEntityIdAndType(Long userId, Long entityId, LikesType type);

    /**
     * 특정 항목의 총 좋아요 수를 조회합니다.
     * @param entityId 조회할 항목의 ID.
     * @param type 조회할 항목의 유형. RECIPE 또는 COMMENT가 될 수 있습니다.
     * @return 총 좋아요 수
     */
    @Query("select count(l) from Likes l where l.entityId = :entityId and l.type = :type and l.isLiked = true")
    long countAllLikes(@Param("entityId") Long entityId, @Param("type") LikesType type);

    /**
     * 특정 항목에 대한 사용자의 좋아요 여부를 조회합니다.
     * @param userId 사용자 ID.
     * @param entityId 조회할 항목의 ID.
     * @param type 조회할 항목의 유형. RECIPE 또는 COMMENT가 될 수 있습니다.
     * @return 좋아요 여부
     */
    boolean existsByUserIdAndEntityIdAndType(Long userId, Long entityId, LikesType type);

    /*
    조회
    1. 사용자가 좋아하는 모든 레시피
    2. 사용자가 좋아하는 모든 댓글
    3. 특정 게시물에 대한 사용자의 좋아요 여부
    4. 특정 댓글에 대한 사용자의 좋아요 여부

    -
    게시물 엔티티 로드 시 사용자의 좋아요 여부: select * from
     */




}
