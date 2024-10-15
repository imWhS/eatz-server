package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.dto.PagedResponse;
import imwhs.eatz_server.dto.eatzuser.EatzUserSummaryDto;
import imwhs.eatz_server.dto.eatzuser.EatzUserResponseDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.eatzuser.query.EatzUserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * 사용자(EatzUser) 관련 항목을 조회하는 작업을 처리하는 클래스입니다.
 * EatzUser에 대한 읽기 전용 쿼리 메서드를 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class EatzUserQueryService {

    private final EatzUserRepository userRepository;

    private final EatzUserQueryRepository userQueryRepository;

    /**
     * 페이지 번호 및 크기 기본 값.
     * 응답 메시지에 포함시킬 시용자에 대해 페이징 처리를 하기 위해 정의합니다.
     */
    private static final int DEFAULT_CURRENT_PAGE = 0;
    private static final int DEFAULT_PAGING_SIZE = 10;

    /**
     * 식별자로 사용자 조회.
     * 특정 id의 사용자를 조회합니다.
     */
    public EatzUserResponseDto findUserById(Long id) {
        EatzUser user = userRepository.findById(id).orElseThrow(
                () -> new EatzUserNotFoundException("id가 " + id + "인 사용자를 찾지 못했습니다."));

        return new EatzUserResponseDto(user);
    }

    /**
     * 이메일로 사용자 조회.
     */
    public EatzUserResponseDto findUserByEmail(String email) {
        EatzUser user = userRepository.findByEmail(email).orElseThrow(
                () -> new EatzUserNotFoundException("이메일 주소가 " + email + "인 사용자를 찾지 못했습니다."));

        return new EatzUserResponseDto(user);
    }

    /**
     * 모든 사용자 조회.
     * 등록된 모든 사용자를 조회합니다.
     * 페이징이 적용됩니다.
     */
    public Page<EatzUserResponseDto> findAllUsers(Integer currentPage, Integer pagingSize) {
        int page = (currentPage == null ? DEFAULT_CURRENT_PAGE : currentPage);
        int size = (pagingSize == null ? DEFAULT_PAGING_SIZE : pagingSize);

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<EatzUser> foundUsers = userRepository.findAll(pageRequest);

        return foundUsers.map(EatzUserResponseDto::new);
    }

    /**
     * 모든 사용자와 활동 요약 조회.
     * 등록된 모든 사용자를 활동 요약과 함께 조회합니다.
     * 페이징이 적용됩니다.
     */
    public PagedResponse<EatzUserSummaryDto> findAllUsersWithActivity(Integer currentPage, Integer pagingSize) {
        int page = (currentPage == null ? DEFAULT_CURRENT_PAGE : currentPage);
        int size = (pagingSize == null ? DEFAULT_PAGING_SIZE : pagingSize);

        return userQueryRepository.findAllWithActivity(page, size);
    }

}
