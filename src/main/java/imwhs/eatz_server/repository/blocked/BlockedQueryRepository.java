package imwhs.eatz_server.repository.blocked;

import imwhs.eatz_server.domain.Blocked;
import imwhs.eatz_server.dto.eatzuser.EatzUserEssentialDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface BlockedQueryRepository {

    // 특정 사용자에 의해 차단되어진 사용자 목록 조회
    List<Blocked> findAllBlockedUsersByBlockerIdAndDeletedAtIsNull(Long blockerId);

    Page<EatzUserEssentialDto> findAllBlockedUserEssentialsByBlockerIdAndDeletedAtIsNull(Long blockerId, Pageable pageable);

}
