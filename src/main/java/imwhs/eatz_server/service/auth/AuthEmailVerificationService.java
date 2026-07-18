package imwhs.eatz_server.service.auth;

import imwhs.eatz_server.dto.auth.EmailVerificationCodeResponse;
import imwhs.eatz_server.exception.*;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.service.MailService;
import imwhs.eatz_server.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthEmailVerificationService {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    /**
     * 이메일 주소 인증 코드의 재발급 대기 시간입니다. 이메일 주소를 다시 인증할 수 있는 시간에 해당합니다.
     * 시간 단위 '초'를 사용합니다.
     */
    private static final long REISSUABLE_INTERVAL_SECONDS = 60;

    /**
     * 이메일 주소 인증 코드의 유효 시간입니다.
     * 시간 단위 '초'를 사용합니다.
     */
    private static final long CODE_EXPIRATION_SECONDS = 180;

    /**
     * 해당 이메일 주소에 대한 오늘 날짜 기준 인증 코드 최대 발급 가능 횟수입니다.
     * 다음 날짜의 자정에 초기화됩니다.
     */
    private static final int CODE_DAILY_CREATION_LIMIT = 3;

    private final EatzUserRepository userRepository;
    private final RedisService redisService;
    private final MailService mailService;

    /**
     * 이메일 인증 코드를 생성하고, 이메일 주소로 발급합니다.
     * <p>
     *     예시
     *     <ul>
     *          처음 이메일 인증 요청을 받아서 인증 코드를 생성한 경우
     *          <li> auth:email_verification_code:test@email.com의 key에
     *               123456이라는 value를 할당해 Redis에 저장, TTL 3분 설정 </li>
     *          <li> auth:email_verification_code:test@email.com:count:20260101의 key에
     *               1이라는 value를 할당해 Redis에 저장, TTL 당일까지로 설정 </li>
     *     </ul>
     *     <ul>
     *          이메일 재인증 요청을 받아서 인증 코드를 생성한 경우
     *          <li> auth:email_verification_code:test@email.com의 key에
     *               123457이라는 value를 할당해 Redis에 저장, TTL 3분 설정 </li>
     *          <li> F:test@email.com:count:20260101의 key에
     *               기존 value에 +1해서 2라는 value를 할당해 Redis에 저장, TTL 당일까지로 설정 </li>
     *     </ul>
     * </p>
     * @param email 이메일 주소
     * @param timeZone 타임 존(시간대)의 ID
     */
    public EmailVerificationCodeResponse sendCode(String email, String timeZone) {
        validateEmailWithUser(email);

        // 기존 인증 코드 발급 대기 시간 유효 여부 확인
        // 1. 마지막 발급 시점에서부터 재발급 대기 시간(1분)이 지났는지 여부
        //  - 마지막 발급 시점 기준 다음 날짜가 되었더라도, 재발급 최소 시간이 지나야 발급 가능하기 때문에 먼저 검사
        // 2. 요청 날짜 기준 3회 발급 받지 않았는지 여부
        ZoneId zoneId = ZoneId.of(timeZone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        String nowDate = now.format(DateTimeFormatter.BASIC_ISO_DATE);

        String codeKey = generateRedisCodeKey(email);

        // 인증 코드 발급 시 필요한 기존 인증 코드를 조회합니다.
        String existingCode = redisService.get(codeKey);

        // 직전에 발급 받은 인증 코드가 있다면, 재발급 대기 시간이 지나지 않았는지 확인합니다.
        if (!Objects.isNull(existingCode)) { validateReissuableInterval(codeKey); }

        // 오늘 날짜를 기준으로 인증 코드 최대 발급 가능 횟수가 초과하지 않았는지 확인합니다.
        String dailyCreationCountKey = generateRedisCodeCreationCountKey(email, nowDate);
        long dailyCreationCount = redisService.increase(dailyCreationCountKey);
        if (CODE_DAILY_CREATION_LIMIT < dailyCreationCount) {
            throw new VerificationCodeDailyCreationLimitExceededException();
        }

        // 인증 코드를 생성한 후 저장합니다.
        String code = generateVerificationCode(existingCode);
        redisService.setForValue(codeKey, code, Duration.ofSeconds(CODE_EXPIRATION_SECONDS));

        long remainingIssuableAttempts = CODE_DAILY_CREATION_LIMIT - dailyCreationCount;
        EmailVerificationCodeResponse response = new EmailVerificationCodeResponse(
                CODE_DAILY_CREATION_LIMIT, remainingIssuableAttempts);

        // 오늘 날짜 기준으로 해당 이메일 주소에 대한 인증 코드를 최초로 발급하는 경우
        // redis code count key의 만료 시간을 오늘 날짜로 설정합니다.
        if (dailyCreationCount == 1) {
            ZonedDateTime startOfNextDay = now.toLocalDate().plusDays(1).atStartOfDay(zoneId);
            redisService.setExpire(dailyCreationCountKey, Duration.between(now, startOfNextDay));
        }

        log.info("이메일 발송을 요청할게요. | {}", email);
        mailService.sendMail(
                email,
                "EATZ 이메일 인증 코드",
                "인증 코드: " + code + "\n" +
                        "인증 코드를 이용해 입력하신 이메일 주소(" + email + ")를 " + "인증한 후, EATZ 회원 가입을 계속 진행해주세요. " +
                        "인증 코드는 이메일 인증을 요청하신 시간으로부터 " + (CODE_EXPIRATION_SECONDS / 60) +"분까지 사용할 수 있어요.");
        return response;
    }

    /**
     * 이메일 인증 코드를 검증합니다. 검증 성공 시 이메일 인증 완료 내역을 Redis에 저장합니다.
     * <ul>
     *     <li> 인증 코드의 유효 시간은 발급 시점에 타임존(시간대)을 고려해 부여됐기 때문에, 타임존까지 포함해서 검증하지 않습니다. </li>
     *     <li> 인증 코드가 발급된 날짜는 최대 발급 가능 횟수를 제한하기 위해 사용하기 때문에, 인증 코드까지 포함해서 검증하지 않습니다. </li>
     *     <li> 이메일 인증 완료 내역은 검증 시점 기준 1시간동안 유효합니다. </li>
     *     <li> 이메일 인증 성공 시 이메일 인증 코드 관련 데이터를 삭제합니다.
     *          단, 가입 도중 재가입을 시도하는 등 무분별하게 인증 코드를 발급하는 상황이 발생하는 것을 막기 위해
     *          이메일 인증 완료 여부와 관계 없이, 이메일 인증 코드 발급 횟수 관련 데이터는 유지합니다. </li>
     * </ul>
     * @param email 이메일 주소
     * @param verificationCode 인증 코드
     */
    public void verifyEmail(String email, String verificationCode) {
        validateEmailWithUser(email);

        String codeKey = generateRedisCodeKey(email);
        String code = redisService.get(codeKey);

        // 이메일 인증 코드를 검증합니다.
        if (Objects.isNull(code)) { throw new VerificationCodeValidationInvalidException(); }
        if (!verificationCode.equals(code)) { throw new VerificationCodeValidationMismatchException(); }

        // 검증에 성공한 경우, 이메일 인증 완료 내역을 Redis에 저장한 후,
        // 이메일 인증 코드 관련 데이터를 Redis에서 삭제합니다.
        String verifiedEmailKey = generateRedisVerifiedEmailKey(email);
        redisService.setForValue(verifiedEmailKey, "true", Duration.ofHours(1));
        redisService.delete(codeKey); // TODO: scan해서 삭제
    }

    private void validateEmailWithUser(String email) {
        validateEmail(email);
        boolean isEmailDuplicated = userRepository.existsByEmailAndDeletedAtIsNull(email);
        if (isEmailDuplicated) { throw new EatzUserDuplicatedEmailException(); }
    }

    private void validateReissuableInterval(String codeKey) {
        long codeTtl = redisService.getTtl(codeKey);
        long reissuableTtl = CODE_EXPIRATION_SECONDS - REISSUABLE_INTERVAL_SECONDS;
        if (reissuableTtl < codeTtl) {
            long remainingReissuableSeconds = codeTtl - reissuableTtl;
            throw new VerificationCodeReissuableIntervalRemainingException(remainingReissuableSeconds);
        }
    }

    private String generateVerificationCode(String existing) {
        SecureRandom secureRandom = new SecureRandom();
        int minimum = 100000;
        int maximum = 999999;
        String code = null;
        do {
            // 100000부터 999999 사이의 난수를 생성합니다.
            // 기존 코드와 동일한 경우, 다른 코드가 나올 때까지 다시 시도합니다.
            int randomNumber = secureRandom.nextInt(
                    maximum + 1 - minimum);
            code = String.format("%06d", minimum + randomNumber);
        } while (Objects.nonNull(existing) && existing.equals(code));

        return code;
    }

    @NonNull
    private static String generateRedisVerifiedEmailKey(String email) {
        return "auth:email_verified:" + email;
    }

    @NonNull
    private static String generateRedisCodeKey(String email) {
        return "auth:email_verification_code:" + email;
    }

    @NonNull
    private static String generateRedisCodeCreationCountKey(String email, String nowDate) {
        return "auth:email_verification_code:" + email + ":creation_count:" + nowDate;
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("필수 항목인 이메일 주소가 비어 있어요.");
        }
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new IllegalArgumentException("이메일 주소(" + email + ")가 올바른 형식이 아니에요.");
        }
    }

}
