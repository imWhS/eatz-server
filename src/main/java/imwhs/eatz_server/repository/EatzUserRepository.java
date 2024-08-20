package imwhs.eatz_server.repository;

import imwhs.eatz_server.domain.EatzUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * EatzUserRepository
 */
@Repository
public interface EatzUserRepository extends JpaRepository<EatzUser, Long> {



}
