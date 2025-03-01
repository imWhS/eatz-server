package imwhs.eatz_server.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RatingsSummaryDistributionDto {

    private RatingSummaryDto summary;

    private Long countScore5;

    private Long countScore4;

    private Long countScore3;

    private Long countScore2;

    private Long countScore1;

}
