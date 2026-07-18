package imwhs.eatz_server.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 특정 레시피에 달린 평가의 점수 분포를 전달할 때 사용합니다.
 */
@Data
@AllArgsConstructor
public class RatingIndicatorScoresDistributionDto {

    private long countScore5;

    private long countScore4;

    private long countScore3;

    private long countScore2;

    private long countScore1;

}
