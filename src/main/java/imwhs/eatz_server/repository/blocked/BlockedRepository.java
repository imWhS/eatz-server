package imwhs.eatz_server.repository.blocked;

import imwhs.eatz_server.domain.Blocked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Block 엔티티의 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 */
@Repository
public interface BlockedRepository extends JpaRepository<Blocked, Long>, BlockedQueryRepository {

    default void validateNotExists(Long blockerId, Long blockedUserId) {
        if (blockerId == null) { throw new IllegalArgumentException("특정 사용자의 차단 여부를 확인하려는 사용자의 ID가 필요해요."); }
        if (blockedUserId == null) { throw new IllegalArgumentException("차단 여부를 확인하려는 사용자의 ID가 필요해요."); }

        if (existsByBlockerIdAndBlockedUserId(blockerId, blockedUserId)) {
            throw new IllegalArgumentException("이미 차단한 사용자예요.");
        }
    }

    /**
     * 특정 사용자와 연관 관계인 차단 레코드를 삭제합니다.
     * 특정 사용자를 차단 취소하기 위해 사용합니다.
     * @param blockerId 요청한 사용자의 ID
     * @param blockedUserId 요청한 사용자에 의해 차단된 사용자의 ID
     * @return 특정 사용자와 연관 관계여서 삭제된 차단 레코드의 수
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Blocked b " +
            "WHERE " +
            "   b.blocker.id = :blockerId AND " +
            "   b.blockedUser.id = :blockedUserId")
    int deleteByBlockerIdAndBlockedUserId(
            @Param("blockerId") long blockerId,
            @Param("blockedUserId") long blockedUserId);

    /**
     * 특정 사용자에 대한 차단 레코드 존재 여부를 조회합니다.
     * 특정 사용자의 차단 여부를 확인하기 위해 사용합니다.
     * @param blockerId 요청한 사용자의 ID
     * @param blockedUserId 요청한 사용자에 의해 차단된 사용자의 ID
     * @return 특정 사용자에 대한 차단 레코드 존재 여부
     */
    boolean existsByBlockerIdAndBlockedUserId(long blockerId, long blockedUserId);

}
