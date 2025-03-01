package imwhs.eatz_server.dto.rating;

import imwhs.eatz_server.dto.Paged;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RatingsDetailDto {

    private RatingsSummaryDistributionDto summary;

    Paged<RatingItemDto> ratings;

}
