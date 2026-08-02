package imwhs.eatz_server.repository.pantry.kitchenware;

import java.util.List;
import java.util.Set;

public interface PantryKitchenwareQueryRepository {

    /**
     * 사용자가 보관함에 추가한 도구의 ID 집합을 조회합니다. 도구의 ID 목록으로 조회 대상 범위를 한정할 수 있습니다.
     * @param id 사용자의 ID
     * @param kitchenwareIds 도구의 ID 목록. null 또는 비어 있는 목록이면 조회 대상 범위를 한정하지 않습니다.
     * @return 사용자가 보관함에 추가한 도구의 ID 집합
     */
    Set<Long> findAllKitchenwareIdsByUserId(Long id, List<Long> kitchenwareIds);

}
