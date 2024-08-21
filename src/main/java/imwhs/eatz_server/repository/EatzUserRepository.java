package imwhs.eatz_server.repository;

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
     * 사용자 이름으로 엔티티 조회.
     * <p>
     * 사용자 이름으로 단일 EatzUser 엔티티를 조회합니다.
     * 조회할 엔티티가 없으면 Optional을 반환합니다.
     *
     * @param username 조회할 사용자 이름.
     * @return 사용자 이름에 해당하는 EatzUser 엔티티(Optional).
     */
    Optional<EatzUser> findByUsername(String username);

}
