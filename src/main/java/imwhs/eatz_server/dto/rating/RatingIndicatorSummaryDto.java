package imwhs.eatz_server.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 여러 평가에 대한 평가 평균 점수, 평가 수와 같은 평가 요약 지표 정보를 전달할 때 사용합니다.
 * <p>
 *    주로 특정 레시피에 달린 평가의 평균 점수와 평가 수를 담을 때 사용합니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class RatingIndicatorSummaryDto {

    /**
     * 평가의 평균 점수
     * 1~5점 사이의 실수 값이며, 사용자들이 남긴 평가 점수의 평균입니다.
     */
    private double averageScore;

    /**
     * 평가 수
     */
    private long count;

}
