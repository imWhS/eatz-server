package imwhs.eatz_server.scheduler;

import imwhs.eatz_server.service.eatzuser.EatzUserCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class EatzUserCleanupScheduler {

    private final EatzUserCleanupService eatzUserCleanupService;

    // 매년 매월 매일 오전 3시가 될 때 실행합니다.
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    public void cleanup() {
        eatzUserCleanupService.cleanup();
    }

}
