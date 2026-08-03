package imwhs.eatz_server.repository;

import imwhs.eatz_server.domain.RefreshToken;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Boolean existsByToken(String token);

    @Modifying(clearAutomatically = true)
    void deleteByToken(String token);

    @Modifying(clearAutomatically = true)
    void deleteAllByEmail(String email);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM RefreshToken r where r.expiration < :now")
    void deleteAllExpired(@Param("now") LocalDateTime now);

}
