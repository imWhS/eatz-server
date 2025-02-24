package imwhs.eatz_server.dto.rating;

import imwhs.eatz_server.dto.eatzuser.EatzUserSummaryDto;
import imwhs.eatz_server.dto.recipe.RecipeBasicDto;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * RatingDetailResponseDto 클래스입니다.
 * <ul>
 *     <li>평가의 기본 정보 뿐 아니라 평가를 등록한 사용자, 평가가 달려 있는 레시피의 부가 정보를 포함하는 DTO입니다.</li>
 *     <li>특정 단일 평가의 상세 정보를 조회해야 하는 경우에 주로 사용합니다.</li>
 * </ul>
 */
@Data
@AllArgsConstructor
public class RatingDetailDtoOld {

    private Long id;

    private EatzUserSummaryDto user;

    private RecipeBasicDto recipe;

    private Integer score;

    private String content;

    // TODO: 작성일, 수정일, 삭제일 추가

}
