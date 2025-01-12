package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.dto.eatzuser.EatzUserSummaryDto;
import imwhs.eatz_server.dto.eatzuser.EatzUserDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.repository.eatzuser.EatzUserQueryRepository;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

/**
 * 사용자(EatzUser) 관련 정보를 조회하는 EatzUserQueryService 클래스입니다.
 * EatzUser에 대한 읽기 전용 쿼리 메서드를 제공합니다.
 */
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class EatzUserQueryService {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    private final EatzUserRepository userRepository;

    private final EatzUserQueryRepository userQueryRepository;

    /**
     * 모든 사용자를 조회합니다.
     * <ul>
     *     <li>등록된 모든 사용자의 엔티티를 조회합니다.</li>
     *     <li>페이징이 적용됩니다.</li>
     * </ul>
     */
    public Page<EatzUserDto> findAllUsers(Pageable pageable) {
        Page<EatzUser> foundUsers = userRepository.findAll(pageable);
        return foundUsers.map(EatzUserDto::new);
    }

    /**
     * 모든 사용자와 사용자 별 활동 요약을 함께 조회합니다.
     * <ul>
     *     <li>페이징을 적용할 수 있습니다.</li>
     * </ul>
     */
    public Page<EatzUserSummaryDto> findAllUsersWithActivity(Pageable pageable) {
        return userQueryRepository.findAllWithActivity(pageable);
    }

    /**
     * 식별자로 사용자를 조회합니다.
     * <p>
     * 특정 식별자에 해당하는 사용자 엔티티를 조회합니다.
     * </p>
     */
    public EatzUserDto findUserById(Long id) {
        EatzUser user = userRepository.findById(id).orElseThrow(
                () -> new EatzUserNotFoundException("id가 " + id + "인 사용자를 찾지 못했습니다."));

        return new EatzUserDto(user);
    }

    /**
     * 이메일 주소로 사용자를 조회합니다.
     * <p>
     *     특정 이메일 주소에 해당하는 사용자 엔티티를 조회합니다.
     * </p>
     */
    public EatzUserDto findUserByEmail(String email) {
        validateEmail(email);

        EatzUser user = userRepository.findByEmail(email).orElseThrow(
                () -> new EatzUserNotFoundException("이메일 주소가 " + email + "인 사용자를 찾지 못했습니다."));

        return new EatzUserDto(user);
    }

    private void validateEmail(String email) {
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new IllegalArgumentException("이메일 주소(" + email + ")가 올바른 형식이 아닙니다.");
        }
    }

}
