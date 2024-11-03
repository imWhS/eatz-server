package imwhs.eatz_server.repository.eatzuser;

import imwhs.eatz_server.domain.EatzUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * EatzUserRepository 리포지토리.<br/>
 * <p>
 * EatzUser 엔티티에 대해 CRUD를 포함한 데이터 처리 작업을 수행합니다.
 */
@Repository
public interface EatzUserRepository extends JpaRepository<EatzUser, Long> {

    /**
     * 사용자 이름으로 사용자 조회.
     * <p>
     *     사용자 이름으로 단일 EatzUser 엔티티를 조회합니다.
     * <p>
     * @param username 사용자 이름.
     * @return Optional로 wrapping된 EatzUser 레시피.
     */
    Optional<EatzUser> findByUsername(String username);

    /**
     * 이메일 주소로 사용자 조회.
     * <p>
     *     이메일 주소로 단일 EatzUser 엔티티를 조회합니다.
     * </p>
     * @param email 이메일 주소.
     * @return Optional로 wrapping된 EatzUser 레시피.
     */
    Optional<EatzUser> findByEmail(String email);

    /**
     * 사용자 이름에 해당하는 사용자의 존재 여부 조회.
     * <p>
     *     사용자 이름에 해당하는 단일 EatzUser 엔티티가 존재하는지에 대한 여부를 조회합니다.
     * </p>
     * @param username 사용자 이름.
     * @return 엔티티의 존재 여부.
     */
    boolean existsByUsername(String username);

    /**
     * 이메일 주소에 해당하는 사용자의 존재 여부 조회.
     * <p>
     *     이메일 주소에 해당하는 단일 EatzUser 엔티티가 존재하는지에 대한 여부를 조회합니다.
     * </p>
     * @param email 사용자 이름.
     * @return 엔티티의 존재 여부.
     */
    boolean existsByEmail(String email);

}
