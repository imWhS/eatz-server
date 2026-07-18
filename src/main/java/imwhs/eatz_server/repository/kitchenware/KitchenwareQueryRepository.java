package imwhs.eatz_server.repository.kitchenware;

import imwhs.eatz_server.dto.kitchenware.KitchenwareBasicDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface KitchenwareQueryRepository {

    /**
     * 키워드(이름)에 해당하는 도구의 기본 정보 목록을 검색합니다.
     * <ul>
     *     <li> 사용자의 ID를 전달받으면, 조회하려는 도구에 대한 해당 사용자의 context를 포함합니다. </li>
     * </ul>
     * @param keyword 키워드
     * @param userId 사용자의 ID
     * @param pageable 페이징 정보
     * @return 검색된 도구의 기본 정보 목록
     */
    Page<KitchenwareBasicDto> searchBasics(
            @Param("keyword") String keyword,
            @Param("userId") Long userId,
            Pageable pageable);

}
