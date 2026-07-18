package imwhs.eatz_server.repository.rating;

import imwhs.eatz_server.dto.rating.RatingBasicDto;
import imwhs.eatz_server.dto.rating.RatingWithAuthorDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface RatingQueryRepository {

    /**
     * 레시피에 달린 모든 평가를 조회합니다.
     * <ul>
     *     <li> 평가 별 기본 정보 뿐 아니라, 해당 평가를 등록한 사용자의 부가 정보를 함께 조회합니다. </li>
     * </ul>
     * @param id 레시피의 ID
     */
    List<RatingWithAuthorDto> findWithAuthorAllByRecipeId(Long id, Pageable pageable);

    /**
     * 레시피에 달린 총 평가 수를 조회합니다.
     * @param id 레시피의 ID
     * @return 총 평가 수
     */
    Long countByRecipeId(Long id);

    /**
     * 사용자가 등록한 총 평가 수를 조회합니다.
     * @param id 사용자의 ID
     * @return 총 평가 수
     */
    Long countByAuthorId(Long id);
    
    /**
     * 사용자(작성자)가 평가를 등록한 레시피 수를 집계합니다.
     * <p> 삭제 처리된 레시피는 집계 대상에서 제외합니다. </p>
     * @param id 평가 작성자의 ID
     * @return 작성자가 평가를 등록한 레시피 수
     */
    Long countAllRatedRecipesByAuthorId(Long id);


    /**
     * 레시피에 달린 모든 평가의 기본 정보 목록을 조회합니다.
     * <ul>
     *     <li> 차단한 사용자가 작성한 평가는 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param id 레시피의 ID
     * @param pageable 페이징 정보
     * @return 모든 평가의 기본 정보 목록 및 페이징 정보
     */
    Page<RatingBasicDto> findAllBasicsByRecipeId(Long id, Long userId, Pageable pageable);

}
