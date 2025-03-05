package imwhs.eatz_server.repository.liked;

import imwhs.eatz_server.domain.liked.EntityType;
import imwhs.eatz_server.domain.liked.Liked;
import imwhs.eatz_server.dto.liked.LikedCountByEntityDto;
import imwhs.eatz_server.dto.liked.LikedDetailDto;
import imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikedRepository extends JpaRepository<Liked, Long> {

    @Query
    Optional<Liked> findByUserIdAndEntityIdAndType(Long userId, Long entityId, EntityType type);

    /**
     * 특정 항목의 총 좋아요 수를 조회합니다.
     * @param entityId 조회할 항목의 ID.
     * @param type 조회할 항목의 유형. RECIPE 또는 COMMENT가 될 수 있습니다.
     * @return 총 좋아요 수.
     */
    @Query("select count(l) from Liked l where l.entityId = :entityId and l.type = :type and l.isLiked = true")
    long countAllLikeds(@Param("entityId") Long entityId, @Param("type") EntityType type);

    /**
     * 특정 항목에 대한 사용자의 좋아요 여부를 조회합니다.
     * @param userId 사용자 ID.
     * @param entityId 조회할 항목의 ID.
     * @param type 조회할 항목의 유형. RECIPE 또는 COMMENT가 될 수 있습니다.
     * @return 좋아요 여부.
     */
    boolean existsByUserIdAndEntityIdAndTypeAndIsLikedIsTrue(Long userId, Long entityId, EntityType type);

    /**
     * 특정 항목에 대한 좋아요 상세 정보를 조회합니다.<br/>
     * 항목을 좋아하는 모든 사용자 정보는 조회 대상에서 제외합니다.
     * @param entityId 조회할 항목의 ID.
     * @param type 조회할 항목의 유형. RECIPE 또는 COMMENT가 될 수 있습니다.
     * @return 좋아요 여부.
     */
    @Query("select new imwhs.eatz_server.dto.liked.LikedDetailDto(l.entityId, l.type, count(l)) " +
            "from Liked l " +
            "where l.entityId = :entityId and l.type = :type and l.isLiked = true")
    LikedDetailDto findAllByEntityIdAndType(@Param("entityId") Long entityId, @Param("type") EntityType type);

    /**
     * 특정 항목을 좋아하는 모든 사용자에 대한 요약 정보 목록을 조회합니다.
     * @param entityId 조회할 항목의 ID.
     * @param type 조회할 항목의 유형. RECIPE 또는 COMMENT가 될 수 있습니다.
     * @return 특정 항목을 좋아하는 모든 사용자 요약 정보
     */
    @Query("select new imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto(u.id, u.username, u.imageUrl) " +
            "from Liked l " +
            "inner join EatzUser u on l.user = u and l.isLiked = true " +
            "where l.entityId = :entityId and l.type = :type")
    List<EatzUserBasicDto> findLikedUsersByEntityIdAndType(@Param("entityId") Long entityId, @Param("type") EntityType type);

    @Query("select new imwhs.eatz_server.dto.liked.LikedCountByEntityDto(l.entityId, COUNT(l)) " +
            "from Liked l " +
            "where l.type = :type and l.entityId in :entityIds " +
            "group by l.entityId")
    List<LikedCountByEntityDto> countByEntityIdsAndType(@Param("entityIds") List<Long> entityIds, @Param("type") EntityType type);

    @Query("select l.entityId " +
            "from Liked l " +
            "where l.type = :type and l.entityId in :entityIds and l.user.username = :username " +
            "group by l.entityId")
    List<Long> findLikedsByEntityIdsAndTypeAndUserUsername(@Param("entityIds") List<Long> entityIds, @Param("type") EntityType type, @Param("username") String username);

}
