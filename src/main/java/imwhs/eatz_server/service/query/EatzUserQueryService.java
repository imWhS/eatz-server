package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.dto.PagedResponse;
import imwhs.eatz_server.dto.eatzuser.EatzUserSummaryDto;
import imwhs.eatz_server.dto.eatzuser.EatzUserResponseDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.repository.eatzuser.EatzUserQueryRepository;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자(EatzUser) 관련 정보를 조회하는 EatzUserQueryService 클래스입니다.
 * EatzUser에 대한 읽기 전용 쿼리 메서드를 제공합니다.
 */
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class EatzUserQueryService {

    private final EatzUserRepository userRepository;

    private final EatzUserQueryRepository userQueryRepository;

    /**
     * 페이지 번호 및 크기 기본 값.
     * <p>
     *     컬렉션 조회 결과에 대한 페이징 처리 시 기본으로 설정, 사용되는 값입니다.
     * </p>
     */
    private static final int DEFAULT_CURRENT_PAGE = 0;
    private static final int DEFAULT_PAGING_SIZE = 10;

    /**
     * 모든 사용자와 사용자 별 활동 요약을 함께 조회합니다.
     * <ul>
     *     <li>등록된 모든 사용자를 활동 요약과 함께 조회합니다.</li>
     *     <li>페이징을 적용할 수 있습니다.</li>
     * </ul>
     */
    public PagedResponse<EatzUserSummaryDto> findAllUsersWithActivity(Integer currentPage, Integer pagingSize) {
        int page = (currentPage == null ? DEFAULT_CURRENT_PAGE : currentPage);
        int size = (pagingSize == null ? DEFAULT_PAGING_SIZE : pagingSize);

        return userQueryRepository.findAllWithActivity(page, size);
    }

}
