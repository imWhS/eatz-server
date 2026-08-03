package imwhs.eatz_server.service.eatzuser;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.repository.EatzUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class EatzUserCleanupService {

    private final EatzUserRepository userRepository;

    @Transactional(rollbackFor = Exception.class)
    public void cleanup() {
        LocalDateTime cleanupDate = LocalDateTime.now().minusDays(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatted = cleanupDate.format(formatter);
        List<EatzUser> targetUsers = userRepository.findByDeletedAtBeforeAndNotAnonymised(cleanupDate);
        if (targetUsers.isEmpty()) {
            log.info("Cleanup 대상 계정이 없어요. | 대상: 삭제 시점이 {} 이전인 모든 계정", formatted);
            return;
        }
        log.info("계정 삭제 사용자 별 cleanup을 시작할게요. | 대상: 삭제 시점이 {} 이전인 계정 {}개", formatted, targetUsers.size());
        for (EatzUser targetUser : targetUsers) {
            String previousEmail = targetUser.getEmail();
            targetUser.updateEmail(generateAnonymisedEmail());
            log.debug("Cleanup 완료했어요. | ID: {} | {} → {}", targetUser.getId(), previousEmail, targetUser.getEmail());
        }
    }

    public String generateAnonymisedEmail() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid + "@deleted.invalid";
    }

}
