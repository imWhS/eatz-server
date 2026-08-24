package imwhs.eatz_server.service.auth;

import imwhs.eatz_server.auth.TokenManager;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.RefreshToken;
import imwhs.eatz_server.dto.auth.EmailVerificationCodeResponse;
import imwhs.eatz_server.dto.auth.VerifyResetTokenResponse;
import imwhs.eatz_server.exception.*;
import imwhs.eatz_server.repository.RefreshTokenRepository;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.service.MailService;
import imwhs.eatz_server.service.RedisService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private static final String REDIS_KEY_PREFIX_RESET_PASSWORD_TOKEN = "reset_password_token:";
    private static final Duration RESET_PASSWORD_TOKEN_EXPIRATION = Duration.ofMinutes(15);

    private static final String REDIS_KEY_PREFIX_VERIFICATION_CODE_OF = "verification_code_of:";
    private static final String REDIS_KEY_PREFIX_VERIFIED_TIME_OF = "verified_time_of:";
    private static final String REDIS_KEY_PREFIX_VERIFICATION_SEND_COUNT = "verification_send_count:";
    public static final int MAX_SEND_COUNT_PER_DAY = 3;

    private final EatzUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenManager tokenManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MailService mailService;
    private final RedisService redisService;

    private static final SecureRandom secureRandom = new SecureRandom();

    public void checkEmailVerificationStatus(String email) {
        String verifiedTimeKey = REDIS_KEY_PREFIX_VERIFIED_TIME_OF + email;
        Long ttl = redisService.getTtl(verifiedTimeKey);

        if (ttl == null || ttl <= 0) {
            throw new EmailNotVerifiedException();
        }
    }

    /**
     * 회원 역할을 가진 새 사용자를 등록합니다.
     * <ul>
     *     <li>EatzUser 엔티티를 생성하고 리포지토리를 통해 저장합니다.</li>
     *     <li>EatzUser 엔티티 생성에 필요한 데이터가 많기 때문에, 사용자 생성 DTO로 사용자 생성에 필요한 데이터를 전달 받습니다.</li>
     * </ul>
     * @return 등록 완료된 사용자의 ID
     * TODO: UUID
     */
    @Transactional
    public Long signUp(String username, String email, String password) {
        EatzUser.validateRawPassword(password);

        validateDuplicateByUsername(username);
        validateDuplicateByEmail(email);

        String verifiedEmailKey = generateRedisVerifiedEmailKey(email);

        String verifiedEmail = redisService.get(verifiedEmailKey);
        if (verifiedEmail == null) { throw new EmailNotVerifiedException(); }

        EatzUser member = EatzUser.createMember(username, email, passwordEncoder.encode(password));
        userRepository.save(member);
        redisService.delete(verifiedEmailKey);
        return member.getId();
    }

    @Transactional
    public String reissueAccessToken(String refreshToken, String email, String role) {
        try {
            refreshTokenRepository.findByToken(refreshToken).orElseThrow(InvalidRefreshTokenException::new);
            String type = tokenManager.getType(refreshToken);

            if (!Objects.equals(type, "refresh")) {
                throw new InvalidRefreshTokenException();
            }

            log.info("액세스 토큰을 재발급했어요!");
        } catch (InvalidRefreshTokenException e) {
            refreshTokenRepository.deleteAllByEmail(email);
            log.warn("유효하지 않은 리프레시 토큰을 이용해 토큰 재발급을 시도했어요. " +
                    "{} 이메일 주소를 사용 중인 계정의 모든 리프레시 토큰을 삭제할게요.", email);
        }

        return tokenManager.createAccessToken(email, role);
    }

    @Transactional
    public String reissueRefreshToken(String refreshToken, String email, String role) {
        deleteRefreshToken(refreshToken);

        String newRefreshToken = tokenManager.createRefreshToken(email, role);
        LocalDateTime expiration = tokenManager.getExpiration(newRefreshToken);
        RefreshToken refreshTokenEntity = new RefreshToken(email, expiration, newRefreshToken);
        refreshTokenRepository.save(refreshTokenEntity);
        log.info("{} 이메일 주소를 사용 중인 계정의 리프레시 토큰을 재발급했어요.", email);
        return newRefreshToken;
    }

    @Transactional
    public void logout(String refreshToken) {
        deleteRefreshToken(refreshToken);
    }

    public void verifyEmail(String email, String code) {
        String storedCode = redisService.get(REDIS_KEY_PREFIX_VERIFICATION_CODE_OF + email);

        if (code == null || !Objects.equals(storedCode, code)) {
            throw new VerificationCodeValidationMismatchException();
        }

        redisService.setForValue(
                REDIS_KEY_PREFIX_VERIFIED_TIME_OF + email,
                LocalDateTime.now().toString(), Duration.ofDays(1));
        redisService.delete(REDIS_KEY_PREFIX_VERIFICATION_CODE_OF + email);
    }

    public VerifyResetTokenResponse validateResetToken(String token) {
        String tokenKey = REDIS_KEY_PREFIX_RESET_PASSWORD_TOKEN + token;

        // Redis에서 토큰으로 사용자 ID를 조회합니다.
        String userIdStr = redisService.get(tokenKey);
        if (userIdStr == null) { throw new PasswordResetTokenInvalidException(); }

        // 사용자 ID로 사용자 엔티티를 조회합니다.
        Long userId = Long.parseLong(userIdStr);
        EatzUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        return new VerifyResetTokenResponse("", user.getEmail());
    }

    public void requestPasswordReset(String email) {
        // 사용자의 유효성을 확인합니다.
        EatzUser user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 일회용 토큰을 생성합니다.
        String token = UUID.randomUUID().toString();

        // 토큰에 사용자 ID를 매핑한 후 저장합니다.
        redisService.setForValue(
                REDIS_KEY_PREFIX_RESET_PASSWORD_TOKEN + token,
                user.getId().toString(),
                RESET_PASSWORD_TOKEN_EXPIRATION
        );

        // iOS 앱을 실행시킬 딥링크를 생성합니다.
        String deepLink = "eatzuserauth://reset-password?token=" + token;

        mailService.sendMail(
                email,
                "EATZ 암호 설정 링크",
                "암호를 다시 설정하려면, EATZ 앱이 설치된 iOS 기기에서 아래 링크로 이동하세요. \n\n" + deepLink + "\n\n" +
                        "이 링크는 편지를 보내드린 시간으로부터 15분 동안 사용할 수 있어요."
        );
    }

    /**
     * 딥 링크를 통해 전달받은 토큰으로 암호를 변경합니다.
     */
    @Transactional
    public void confirmPasswordReset(String token, String newPassword) {
        String tokenKey = REDIS_KEY_PREFIX_RESET_PASSWORD_TOKEN + token;

        // Redis에서 토큰으로 사용자 ID를 조회합니다.
        String userIdStr = redisService.get(tokenKey);
        if (userIdStr == null) {
            throw new InvalidRefreshTokenException();
        }

        if (newPassword == null || newPassword.length() < 8) {
            throw new EatzInvalidRequestArgumentException("암호는 8자리 이상의 길이여야 해요.");
        }

        Long userId = Long.parseLong(userIdStr);
        EatzUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없어요."));
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.updatePassword(user, encodedPassword);

        // 사용 완료한 토큰을 삭제합니다.
        redisService.delete(tokenKey);
    }

    public EmailVerificationCodeResponse sendVerificationCodeToEmail(String email) {
        String verificationCodeKey = REDIS_KEY_PREFIX_VERIFICATION_CODE_OF + email;
        String sendCountKey = REDIS_KEY_PREFIX_VERIFICATION_SEND_COUNT + email;

        Long verificationCodeTtl = redisService.getTtl(verificationCodeKey);

        if (verificationCodeTtl > 0) {
            // 유효한 인증 코드가 아직 존재하는 경우: 인증 번호를 생성하지 않습니다.
            String currentCountValue = redisService.get(sendCountKey);
            int currentCount = 0;
            if (currentCountValue != null) {
                try {
                    currentCount = Integer.parseInt(currentCountValue);
                } catch (NumberFormatException e) { /* 무시 */ }
            }

            return new EmailVerificationCodeResponse(
                    MAX_SEND_COUNT_PER_DAY,
                    MAX_SEND_COUNT_PER_DAY - currentCount
            );
        } else {
            return createAndSendVerificationCode(email);
        }
    }

    public EmailVerificationCodeResponse resendVerificationCode(String email) {
        return createAndSendVerificationCode(email);
    }

    private EmailVerificationCodeResponse createAndSendVerificationCode(String email) {
        String verificationCodeKey = REDIS_KEY_PREFIX_VERIFICATION_CODE_OF + email;
        String sendCountKey = REDIS_KEY_PREFIX_VERIFICATION_SEND_COUNT + email;

        Long currentCount = redisService.increase(sendCountKey);

        // 첫 요청인 경우
        if (currentCount == 1) {
            redisService.setExpire(sendCountKey, Duration.ofDays(1));
        }

        if (currentCount > MAX_SEND_COUNT_PER_DAY) {
            throw new VerificationCodeDailyCreationLimitExceededException();
        }

        validateDuplicateByEmail(email);
        String verificationCode = generateVerificationCode();
        redisService.setForValue(verificationCodeKey, verificationCode, Duration.ofMinutes(5));

        mailService.sendMail(
                email,
                "EATZ 이메일 주소 인증 코드",
                "인증 코드: " + verificationCode + "\n" +
                        "인증 코드를 이용해 입력하신 이메일 주소(" + email + ")를 " + "인증한 후, EATZ 회원 가입을 계속 진행해주세요. " +
                        "인증 코드는 이메일 인증을 요청하신 시간으로부터 5분까지 사용할 수 있어요.");
        return new EmailVerificationCodeResponse(
                MAX_SEND_COUNT_PER_DAY,
                (int) (MAX_SEND_COUNT_PER_DAY - currentCount));
    }

    private void validateDuplicateByUsername(String username) {
        if (userRepository.existsByUsernameAndDeletedAtIsNull(username)) {
            throw new EatzUserDuplicatedEmailException();
        }
    }

    private void validateDuplicateByEmail(String email) {
        if (userRepository.existsByEmailAndDeletedAtIsNull(email)) {
            throw new EatzUserDuplicatedEmailException();
        }
    }

    private void deleteRefreshToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue).orElseThrow(
                InvalidRefreshTokenException::new);

        refreshTokenRepository.delete(refreshToken);
        log.info("서버 데이터베이스의 리프레시 토큰을 삭제했어요!");
    }

    /**
     * 100000부터 999999 사이의 자연수로 구성된 난수로 인증 번호를 생성합니다.
     * @return 인증 번호
     */
    private String generateVerificationCode() {
        int verificationCode = secureRandom.nextInt(900000) + 100000;
        return String.valueOf(verificationCode);
    }

    @NonNull
    private static String generateRedisVerifiedEmailKey(String email) {
        return "auth:email_verified:" + email;
    }

}
