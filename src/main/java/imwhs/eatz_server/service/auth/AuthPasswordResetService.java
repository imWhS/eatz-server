package imwhs.eatz_server.service.auth;

import imwhs.eatz_server.common.util.EmailUtil;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.dto.auth.VerifyResetTokenResponse;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.PasswordResetEmailDailySendableLimitExceededException;
import imwhs.eatz_server.exception.PasswordResetUnauthorizedException;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.service.MailService;
import imwhs.eatz_server.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthPasswordResetService {

    /**
     * 이메일 인증 토큰의 유효 시간입니다.
     * 시간 단위 '분'을 사용합니다.
     */
    private static final long EMAIL_VERIFICATION_TOKEN_EXPIRATION_MINUTES = 60;

    /**
     * 이메일 인증 토큰 최대 발급 횟수입니다.
     */
    private static final long EMAIL_DAILY_SENDABLE_LIMIT = 3;

    /**
     * 암호 초기화 토큰의 유효 시간입니다.
     * 시간 단위 '분'을 사용합니다.
     */
    private static final long AUTHORIZED_TOKEN_EXPIRATION_MINUTES = 5;

    private final EatzUserRepository userRepository;
    private final RedisService redisService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 암호를 다시 설정하려는 계정의 이메일 인증을 요청합니다.
     * <p>
     *     암호 다시 설정을 요청한 사람이 계정 사용자인지 확인하기 위해 이메일 인증을 진행해야 합니다.
     *     요청을 받으면 이메일 인증 토큰을 생성하고, 이메일 인증 토큰을 담은 딥 링크를 이메일 주소로 발급합니다.
     * </p>
     * @param email 이메일 주소
     */
    @Transactional(rollbackFor = Exception.class)
    public void requestEmailVerification(String email) {
        // 이메일 주소의 유효성을 검증합니다.
        validateEmailWithUser(email);

        String emailSentCountKey = generateRedisSentEmailCountKey(email);
        long sentEmailCount = redisService.increase(emailSentCountKey);
        if (EMAIL_DAILY_SENDABLE_LIMIT < sentEmailCount) {
            throw new PasswordResetEmailDailySendableLimitExceededException();
        }

        // UUID 기반의 무작위 값을 가지는 일회용 이메일 인증 토큰을 생성합니다.
        String emailVerificationToken = UUID.randomUUID().toString();
        String emailVerificationTokenKey = generateRedisEmailVerificationTokenKey(emailVerificationToken);

        // 이메일 인증 토큰을 key로, 이메일 주소를 값으로 매핑한 데이터로 저장합니다.
        redisService.setForValue(
                emailVerificationTokenKey,
                email,
                Duration.ofMinutes(EMAIL_VERIFICATION_TOKEN_EXPIRATION_MINUTES));

        // 이메일 인증 토큰을 담은 암호 초기화용 딥링크를 생성합니다.
        String deepLink = "eatzuserauth://reset-password?emailVerificationToken=" + emailVerificationToken;

        mailService.sendMail(
                email,
                "EATZ 암호 설정 링크",
                "암호를 다시 설정하려면, EATZ 앱이 설치된 iOS 기기에서 아래 링크로 이동하세요.\n\n" +
                        deepLink + "\n\n" +
                        "이 링크는 편지를 보내드린 시간으로부터 " + EMAIL_VERIFICATION_TOKEN_EXPIRATION_MINUTES + "분 동안 사용할 수 있어요.\n\n" +
                        "한 번 이동한 링크는 바로 폐기돼요. 만약 회원님이 암호 설정을 요청한 적 없다면, 이 편지를 무시하셔도 돼요.");

        // 처음 이메일 인증 토큰을 생성, 전송한 경우에만 TTL을 다음 날짜의 자정으로 설정합니다.
        if (sentEmailCount == 1) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startOfNextDay = now.toLocalDate().plusDays(1).atStartOfDay();
            redisService.setExpire(emailSentCountKey, Duration.between(now, startOfNextDay));
        }
    }

    /**
     * 암호를 다시 설정하려는 계정의 이메일 주소에 암호 다시 설정 권한을 부여합니다.
     * <ul>
     *     <li> 이메일 인증 토큰의 진위 여부를 판별합니다. 이메일 인증 토큰이 정상인 경우, 외부의 hijacking 공격을 방어하기 위해
     *          암호 다시 설정 권한 토큰을 생성한 후 HTTPS 체널(클라이언트)로 발급합니다. </li>
     *     <li> 이메일 인증 토큰은 진위 여부 판별 직후에 Redis에서 삭제됩니다. </li>
     * </ul>
     * @param emailVerificationToken 이메일 인증 토큰
     * @return 암호 다시 설정 권한 부여 토큰과 이메일 주소를 담은 DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public VerifyResetTokenResponse authorizePasswordReset(String emailVerificationToken) {
        // 토큰의 진위 여부를 판별하기 위해 토큰을 key로 사용해, Redis에서 매핑된 이메일 주소 데이터를 조회합니다.
        String emailTokenKey = generateRedisEmailVerificationTokenKey(emailVerificationToken);
        String email = redisService.get(emailTokenKey);
        if (email == null) { throw new PasswordResetUnauthorizedException(); }

        // 이메일 주소의 유효성을 검증합니다.
        validateEmailWithUser(email);

        // 이메일 주소가 유효한 경우, Redis에서 이메일 인증 토큰을 삭제합니다.
        redisService.delete(emailTokenKey);

        // UUID 기반의 무작위 값을 가지는 일회용 암호 설정 토큰을 생성합니다.
        String authorizedToken = UUID.randomUUID().toString();
        String authorizedTokenKey = generateRedisAuthorizedTokenKey(authorizedToken);

        // 암호 초기화 토큰을 key로, 이메일 주소를 값으로 매핑한 데이터로 저장합니다.
        redisService.setForValue(authorizedTokenKey, email, Duration.ofMinutes(AUTHORIZED_TOKEN_EXPIRATION_MINUTES));

        // 암호 설정 토큰과 마스킹 처리된 이메일 주소를 반환합니다.
        return new VerifyResetTokenResponse(authorizedToken, EmailUtil.maskEmail(email));
    }

    /**
     * 이메일 주소에 해당하는 계정의 암호를 재설정합니다.
     * <ul>
     *     <li> 암호 설정 토큰이 필요합니다. </li>
     *     <li> 암호 설정 토큰은 계정의 암호를 새 암호로 업데이트한 직후에 Redis에서 삭제됩니다. </li>
     * </ul>
     * @param authorizedToken 암호 설정 토큰. 중간 침입자에 의한 hijacking 공격을 방지하기 위해 사용합니다.
     * @param newPassword 새 암호
     */
    @Transactional(rollbackFor = Exception.class)
    public void reset(String authorizedToken, String newPassword) {
        String authorizedTokenKey = generateRedisAuthorizedTokenKey(authorizedToken);
        String email = redisService.get(authorizedTokenKey);
        if (email == null) { throw new PasswordResetUnauthorizedException(); }
        
        // 이메일 주소의 유효성을 검증합니다.
        validateEmailWithUser(email);

        // 새 암호의 유효성을 검증합니다.
        EatzUser.validateRawPassword(newPassword);

        // 새 암호를 인코딩합니다.
        String newPasswordEncoded = passwordEncoder.encode(newPassword);

        // 계정의 암호를 설정하기 위해 엔티티를 조회합니다.
        EatzUser user = userRepository.findByEmailAndDeletedAtIsNull(email).orElseThrow(
                () -> new EatzUserNotFoundException(email, true));
        user.updatePassword(user, newPasswordEncoded);

        // 암호 설정이 완료되면, Redis에서 이메일 인증 토큰을 삭제합니다.
        redisService.delete(authorizedTokenKey);

        mailService.sendMail(
                email,
                "EATZ 계정 암호 다시 설정 완료",
                 email + " 이메일 주소를 사용 중인 EATZ 계정의 암호가 다시 설정됐어요.\n\n" +
                         "이제부터 새 암호를 이용해 EATZ에 로그인해주세요.\n\n" +
                         "만약 회원님이 암호 설정을 요청한 적이 없다면, 빠르게 개발자에게 문의해주세요."
        );
    }

    private void validateEmailWithUser(String email) {
        EmailUtil.validateEmail(email);
        boolean isUserExists = userRepository.existsByEmailAndDeletedAtIsNull(email);
        if (!isUserExists) { throw new EatzUserNotFoundException(email, true); }
    }

    @NonNull
    private static String generateRedisSentEmailCountKey(String email) {
        return "auth:password_reset:sent_email_count:" + email;
    }

    @NonNull
    private static String generateRedisEmailVerificationTokenKey(String token) {
        return "auth:password_reset:email_verification_token:" + token;
    }

    @NonNull
    private static String generateRedisAuthorizedTokenKey(String token) {
        return "auth:password_reset:authorized_token:" + token;
    }
}
