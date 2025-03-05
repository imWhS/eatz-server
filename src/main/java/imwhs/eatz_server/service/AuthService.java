package imwhs.eatz_server.service;

import imwhs.eatz_server.auth.TokenManager;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.RefreshToken;
import imwhs.eatz_server.exception.DuplicatedEatzUserException;
import imwhs.eatz_server.exception.InvalidTokenException;
import imwhs.eatz_server.repository.RefreshTokenRepository;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class AuthService {

    private static final String REDIS_KEY_PREFIX_VERIFICATION_CODE_OF = "verification-code-of:";

    private static final String REDIS_KEY_PREFIX_VERIFIED_TIME_OF = "verified-time-of:";

    private final EatzUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenManager tokenManager;

    private final RefreshTokenRepository refreshTokenRepository;
    
    private final MailService mailService;
    
    private final RedisService redisService;

    private static final SecureRandom secureRandom = new SecureRandom();

    public void sendVerificationCodeToEmail(String email) {
        String verificationCode = generateVerificationCode();
        redisService.setValue(REDIS_KEY_PREFIX_VERIFICATION_CODE_OF + email, verificationCode, Duration.ofMinutes(5));
        mailService.sendMail(
                email,
                "EATZ 이메일 주소 인증 코드",
                "인증 코드: " + verificationCode + "\n" +
                        "인증 코드를 이용해 입력하신 이메일 주소(" + email + ")를 " + "인증한 후, EATZ 회원 가입을 계속 진행해주세요. " +
                        "인증 코드는 이메일 인증을 요청하신 시간으로부터 5분까지 사용할 수 있어요.");
    }
    
    public void verifyEmail(String email, String code) {
        String storedCode = redisService.getValue(REDIS_KEY_PREFIX_VERIFICATION_CODE_OF + email);
        
        if (code == null || !Objects.equals(storedCode, code)) {
            throw new IllegalArgumentException("인증 코드가 올바르지 않아요. 유효 시간(이메일 인증 요청한 시간으로부터 5분)이 지났다면, " +
                    "처음부터 다시 진행해주세요.");
        }
        
        redisService.setValue(REDIS_KEY_PREFIX_VERIFIED_TIME_OF + email, LocalDateTime.now().toString(), Duration.ofMinutes(30));
    }
    

    /**
     * 회원 권한을 가진 새 사용자를 등록합니다.
     * <ul>
     *     <li>EatzUser 엔티티를 생성하고 리포지토리를 통해 저장합니다.</li>
     *     <li>EatzUser 엔티티 생성에 필요한 데이터가 많기 때문에, 사용자 생성 DTO로 사용자 생성에 필요한 데이터를 전달 받습니다.</li>
     * </ul>
     * @return 등록 완료된 사용자 엔티티 EatzUser의 ID.
     * TODO: UUID
     */
    @Transactional
    public Long signUp(String username, String email, String password) {
        isEmailExists(email);
        String verifiedTime = redisService.getValue(REDIS_KEY_PREFIX_VERIFIED_TIME_OF + email);

        if (verifiedTime == null) {
            throw new IllegalArgumentException("인증이 완료되지 않은 이메일 주소입니다.");
        }

        // 기존 등록된 사용자에 의해 사용 중인 사용자 이름이 아닌지 확인합니다.
        if (userRepository.existsByEmailOrUsername(email, username)) {
            throw new DuplicatedEatzUserException("이미 " + username + "를 사용자 이름으로 사용 중인 사용자가 존재합니다.");
        }

        EatzUser member = EatzUser.createMember(username, email, passwordEncoder.encode(password));
        userRepository.save(member);

        return member.getId();
    }

    private void isEmailExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicatedEatzUserException("이미 " + email + "를 이메일 주소로 사용 중인 사용자가 존재합니다.");
        }
    }

    public String reissueAccessToken(String refreshToken, String email, String password) {
        String type = tokenManager.getType(refreshToken);

        if (!Objects.equals(type, "refresh")) {
            throw new InvalidTokenException("리프레시 토큰이 없습니다.");
        }

        return tokenManager.createAccessToken(email, password);
    }

    @Transactional
    public String reissueRefreshToken(String refreshToken, String email, String role) {
        deleteRefreshToken(refreshToken);

        String newRefreshToken = tokenManager.createRefreshToken(email, role);
        LocalDateTime expiration = tokenManager.getExpiration(newRefreshToken);
        RefreshToken refreshTokenEntity = new RefreshToken(email, expiration, refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);
        return newRefreshToken;
    }

    @Transactional
    public void logout(String refreshToken) {
        deleteRefreshToken(refreshToken);
    }

    private void deleteRefreshToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue).orElseThrow(
                () -> new InvalidTokenException("유효하지 않은 리프레시 토큰입니다."));

        refreshTokenRepository.delete(refreshToken);
    }

    /**
     * 100000부터 999999 사이의 자연수로 구성된 난수로 인증 번호를 생성합니다.
     * @return 인증 번호
     */
    private String generateVerificationCode() {
        int verificationCode = secureRandom.nextInt(900000) + 100000;
        return String.valueOf(verificationCode);
    }

}
