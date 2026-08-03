package imwhs.eatz_server.scheduler;

import imwhs.eatz_server.service.auth.AuthRefreshTokenCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RefreshTokenCleanupScheduler {

    private final AuthRefreshTokenCleanupService refreshTokenCleanupService;

    // 매년 매월 매일 오전 4시가 될 때 실행합니다.
    @Scheduled(cron = "0 0 4 * * *")
    public void cleanupExpiredRefreshTokens() {
        refreshTokenCleanupService.cleanUpExpiredRefreshTokens();
    }

}
