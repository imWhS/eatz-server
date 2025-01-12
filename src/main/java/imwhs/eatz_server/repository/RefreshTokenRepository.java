package imwhs.eatz_server.repository;

import imwhs.eatz_server.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query
    Boolean existsByRefreshToken(String refreshToken);

    @Query
    void deleteByRefreshToken(String refreshToken);

    @Query
    void deleteAllByEmail(String email);

}
