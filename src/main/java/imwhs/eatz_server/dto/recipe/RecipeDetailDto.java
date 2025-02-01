package imwhs.eatz_server.dto.recipe;

import imwhs.eatz_server.dto.eatzuser.EatzUserSummaryDto;
import imwhs.eatz_server.dto.rating.RatingSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * RecipeDetailResponseDto 클래스입니다.
 * <ul>
 * <li>특정 레시피의 상세 정보 데이터를 전달하기 위해 사용하는 DTO입니다.</li>
 * <li>주로 레시피 목록에서 특정 레시피를 조회하거나, 특정 id의 레시피를 조회할 때,
 * 해당 레시피의 상세 정보를 클라이언트가 출력하기 위한 API에 사용됩니다.</li>
 * </ul>
 */
@Data
@AllArgsConstructor
public class RecipeDetailDto {

    private Long id;

    /**
     * 사용자의 기본 정보 및 부가 정보
     */
    private EatzUserSummaryDto user;

    private String title;

    private String url;

    private String imageUrl;

    private String description;

    /**
     * 레시피에 달린 댓글 수
     */
    private int commentCount;

    /**
     * 레시피에 달린 평가들의 요약 정보
     */
    private RatingSummaryDto ratingSummary;

    /**
     * 좋아하는 사용자 수
     */
    private Long likeCount;

    /**
     * 속해있는 카테고리 목록
     */
    private List<CategoryDto> category;

}
