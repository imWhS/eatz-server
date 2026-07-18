package imwhs.eatz_server.repository;

import imwhs.eatz_server.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);


    Boolean existsByToken(String token);

    @Modifying(clearAutomatically = true)
    void deleteByToken(String token);

    @Modifying(clearAutomatically = true)
    void deleteAllByEmail(String email);
}
