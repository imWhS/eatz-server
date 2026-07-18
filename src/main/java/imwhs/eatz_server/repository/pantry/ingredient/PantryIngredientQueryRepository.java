package imwhs.eatz_server.repository.pantry.ingredient;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.dto.ingredient.IngredientBasicDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface PantryIngredientQueryRepository {

    /**
     * 사용자가 보관함에 추가한 모든 재료의 기본 정보 목록을 조회합니다.
     * @param user 사용자의 EatzUser 엔티티
     * @param pageable 페이징 정보
     * @return 모든 재료의 기본 정보 목록과 페이징 정보
     */
    Page<IngredientBasicDto> findAllByUser(@Param("user") EatzUser user, Pageable pageable);

}
