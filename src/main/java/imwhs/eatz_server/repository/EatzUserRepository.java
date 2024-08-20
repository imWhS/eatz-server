package imwhs.eatz_server.repository;

import imwhs.eatz_server.domain.EatzUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * EatzUserRepository 리포지토리.<br/>
 * <p>
 * EatzUser 엔티티에 대해 CRUD를 포함한 데이터 처리 작업을 수행합니다.
 */
@Repository
public interface EatzUserRepository extends JpaRepository<EatzUser, Long> {
}
