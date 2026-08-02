package imwhs.eatz_server.repository.pantry.ingredient;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.dto.ingredient.IngredientBasicDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface PantryIngredientQueryRepository {

    /**
     * 사용자가 보관함에 추가한 모든 재료의 기본 정보 목록을 조회합니다.
     * @param user 사용자의 EatzUser 엔티티
     * @param pageable 페이징 정보
     * @return 모든 재료의 기본 정보 목록과 페이징 정보
     */
    Page<IngredientBasicDto> findAllByUser(@Param("user") EatzUser user, Pageable pageable);

    /**
     * 사용자가 보관함에 추가한 재료의 ID 집합을 조회합니다. 재료의 ID 목록으로 조회 대상 범위를 한정할 수 있습니다.
     * @param id 사용자의 ID
     * @param ingredientIds 재료의 ID 목록. null 또는 비어 있는 목록이면 조회 대상 범위를 한정하지 않습니다.
     * @return 사용자가 보관함에 추가한 재료의 ID 집합
     */
    Set<Long> findAllIngredientIdsByUserId(Long id, List<Long> ingredientIds);

}
