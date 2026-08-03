package imwhs.eatz_server.service.auth;

import imwhs.eatz_server.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthRefreshTokenCleanupService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(rollbackFor = Exception.class)
    public void cleanUpExpiredRefreshTokens() {
        log.info("만료된 리프레시 토큰을 cleanup 할게요.");
        refreshTokenRepository.deleteAllExpired(LocalDateTime.now());
        log.info("만료된 리프레시 토큰을 cleanup 했어요.");
    }

}
