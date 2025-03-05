package imwhs.eatz_server.repository.eatzuser;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * EatzUserRepository 클래스입니다.<br/>
 * EatzUser 엔티티의 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
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

    boolean existsByEmailOrUsername(@Email(message = "유효한 이메일 주소가 아닙니다.") String email, String username);
}
