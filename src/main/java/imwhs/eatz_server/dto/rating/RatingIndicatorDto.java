package imwhs.eatz_server.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 여러 평가들의 지표 정보를 담는 DTO입니다.
 * <ul>
 *      <li> 여러 평가들에 대한 요약 정보와 점수 분포를 함께 담은 지표로서 사용합니다. </li>
 *      <li> 주로 컬렉션으로 조회할 때 사용합니다. </li>
 * </ul>
 */
@AllArgsConstructor
@Data
public class RatingIndicatorDto {

    private RatingIndicatorSummaryDto summary;

    private RatingIndicatorScoresDistributionDto scoresDistribution;

}
