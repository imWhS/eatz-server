package imwhs.eatz_server.service.eatzuser;

import imwhs.eatz_server.common.util.EmailUtil;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.dto.auth.EmailAvailability;
import imwhs.eatz_server.dto.auth.EmailAvailabilityResponse;
import imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto;
import imwhs.eatz_server.dto.eatzuser.EatzUserBioDto;
import imwhs.eatz_server.dto.eatzuser.EatzUserDetailDto;
import imwhs.eatz_server.repository.EatzUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * 사용자(EatzUser) 관련 정보를 조회하는 EatzUserQueryService 클래스입니다.
 * EatzUser에 대한 읽기 전용 쿼리 메서드를 제공합니다.
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EatzUserQueryService {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private final EatzUserRepository userRepository;
    private final EatzUserCleanupService eatzUserCleanupService;

    /**
     * ID에 해당하는 사용자의 기본 정보를 가져옵니다.
     * @param id 사용자의 ID
     * @return 사용자의 기본 정보
     */
    public EatzUserBasicDto getBasic(Long id) {
        EatzUser user = userRepository.get(id);
        return new EatzUserBasicDto(user);
    }

    /**
     * ID에 해당하는 사용자의 상세한 정보를 가져옵니다.
     * @param id 사용자의 ID
     * @return 사용자의 상세한 정보
     */
    public EatzUserDetailDto getDetail(Long id) {
        EatzUser user = userRepository.get(id);
        return new EatzUserDetailDto(user);
    }

    /**
     * 이메일 주소에 해당하는 사용자의 상세한 정보를 가져옵니다.
     * @param email 사용자의 이메일 주소
     * @return 사용자의 상세한 정보
     */
    public EatzUserDetailDto getDetailByEmail(String email) {
        EmailUtil.validateEmail(email);
        EatzUser user = userRepository.getByEmail(email);
        return new EatzUserDetailDto(user);
    }

    /**
     * 사용자 이름의 중복 여부를 확인합니다.
     * @param username 사용자 이름
     */
    public boolean checkUsernameDuplication(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 이메일 주소에 해당하는 사용자의 상세한 정보를 가져옵니다.
     * @param email 사용자의 이메일 주소
     * @return 사용자의 상세한 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public EmailAvailabilityResponse getEmailStatus(String email) {
        EmailUtil.validateEmail(email);
        Optional<EatzUser> userOpt = userRepository.findByEmail(email);

        // A. 이메일 주소로 사용자 조회했는데 안 찾아지면 한 번도 쓰지 않은 이메일 주소거나,
        // 가입할 때 쓴 적 있더라도, UUID 배치 돌려졌을 것이기에, 새로 가입 가능한 이메일 주소임을 response
        if (!userOpt.isPresent()) {
            return new EmailAvailabilityResponse(EmailAvailability.AVAILABLE, "계정을 생성할 수 있는 이메일 주소예요.");
        }

        EatzUser user = userOpt.get();
        LocalDateTime deletedAt = user.getDeletedAt(); // 12월 1일

        // B. 사용자 조회됐고 deletedAt이 null이라는 건 30일 정상 가입 상태인 사용자임을 response
        if (deletedAt == null) {
            return new EmailAvailabilityResponse(EmailAvailability.IN_USE, "정상 가입된 계정에서 사용 중인 이메일 주소예요.");
        }

        // C. 사용자 조회됐고 deletedAt이 null 아니라는 건 30일 UUID 배치 안 돌려진(탈퇴 30일 안 지난) 이메일 주소인지 확인해서
        // '확실히' 30일 지났는지 확인해서, 정확히 언제부터 해당 이메일 주소로 가입 가능한지 알려주는 response
        LocalDateTime notSignableEndDate = deletedAt.plusDays(30); // 12월 31일
        LocalDateTime now = LocalDateTime.now(); // 1월 1일

        // 혹시나 UUID 배치가 아직 안 돌려져서, 사용자 조회됐고 deletedAt이 null 아니지만,
        // deletedAt이 30일 지난 경우에도 새로 가입 가능한 이메일 주소임을 response
        if (now.isAfter(notSignableEndDate)) {
            String anonymisedEmail = eatzUserCleanupService.generateAnonymisedEmail();
            user.updateEmail(anonymisedEmail);
            return new EmailAvailabilityResponse(EmailAvailability.AVAILABLE, "계정을 생성할 수 있는 이메일 주소예요.");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 H시 m분");
        String formattedDate = notSignableEndDate.format(formatter);

        return new EmailAvailabilityResponse(
                EmailAvailability.IN_COOLDOWN,  "최근에 삭제(회원 탈퇴)한 계정에서 사용했던 이메일 주소예요. "
                + formattedDate + "부터 계정을 생성할 수 있어요.");
    }

    /**
     * 사용자 이름에 해당하는 사용자의 상세한 정보를 가져옵니다.
     * @param username 사용자의 사용자 이름
     * @return 사용자의 상세한 정보
     */
    public EatzUserDetailDto getDetailByUsername(String username) {
        EatzUser user = userRepository.getByUsername(username);
        return new EatzUserDetailDto(user);
    }

    /**
     * 사용자의 소개를 가져옵니다.
     * @param id 사용자의 ID
     * @return 사용자의 소개
     */
    public EatzUserBioDto getBio(Long id) {
        EatzUser user = userRepository.get(id);
        String bio = user.getBio();
        return new EatzUserBioDto(bio);
    }

    /**
     * 모든 사용자의 상세한 정보 목록을 가져옵니다.
     * @param pageable 페이징 정보
     * @return 모든 사용자의 기본 정보 목록 및 페이징 정보
     */
    public Page<EatzUserDetailDto> getAllDetails(Pageable pageable) {
        Page<EatzUser> users = userRepository.findAllByDeletedAtIsNull(pageable);
        return users.map(EatzUserDetailDto::new);
    }

}
