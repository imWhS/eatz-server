package imwhs.eatz_server.repository;

import imwhs.eatz_server.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query
    Boolean existsByToken(String token);

    @Query
    void deleteByToken(String token);

}
