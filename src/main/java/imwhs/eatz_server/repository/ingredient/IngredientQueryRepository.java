package imwhs.eatz_server.repository.ingredient;

import imwhs.eatz_server.dto.ingredient.IngredientBasicDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface IngredientQueryRepository {

    /**
     * 상위 재료에 포함되어 있는 모든 재료의 기본 정보 목록을 조회합니다.
     * <ul>
     *     <li> 사용자의 ID를 전달받으면, 조회하려는 재료에 대한 해당 사용자의 context를 포함합니다. </li>
     * </ul>
     * @param id 상위 재료의 ID
     * @param userId 사용자의 ID
     * @return 페이징 처리된 조회된 재료의 기본 정보 목록
     */
    List<IngredientBasicDto> findAllBasicsByParentId(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 사용자가 좋아하는 모든 재료의 기본 정보 목록을 조회합니다.
     * <ul>
     *     <li> 조회하려는 재료에 대한 해당 사용자의 context를 포함합니다. </li>
     * </ul>
     * @param userId 사용자의 ID
     * @param pageable 페이징 정보
     * @return 페이징 처리된 조회된 재료의 기본 정보 목록
     */
    Page<IngredientBasicDto> findAllIngredientsByLikedUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 상위 재료에 속하지 않은 최상위 계층(root) 재료의 기본 정보 목록을 조회합니다.
     * <ul>
     *     <li> 사용자의 ID를 전달받으면, 조회하려는 재료에 대한 해당 사용자의 context를 포함합니다. </li>
     * </ul>
     * @param userId 사용자의 ID
     * @param pageable 페이징 정보 
     * @return 페이징 처리된 최상위 계층 재료의 기본 정보 목록
     */
    Page<IngredientBasicDto> findAllRootBasics(@Param("userId") Long userId, Pageable pageable);

    /**
     * 키워드(이름)에 해당하는 재료의 기본 정보 목록을 검색합니다.
     * <ul>
     *     <li> 사용자의 ID를 전달받으면, 조회하려는 재료에 대한 해당 사용자의 context를 포함합니다. </li>
     * </ul>
     * @param keyword 키워드
     * @param userId 사용자의 ID
     * @param pageable 페이징 정보
     * @return 검색된 재료의 기본 정보 목록
     */
    Page<IngredientBasicDto> searchBasics(
            @Param("keyword") String keyword,
            @Param("userId") Long userId,
            Pageable pageable);

}
