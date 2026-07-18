package imwhs.eatz_server.dto.rating;

import imwhs.eatz_server.domain.Rating;
import imwhs.eatz_server.dto.CreationInfoResponse;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class RatingCreationInfoResponse extends CreationInfoResponse {

    public RatingCreationInfoResponse(Long id, LocalDateTime createdAt) {
        super(id, createdAt);
    }

    public RatingCreationInfoResponse(Rating rating) {
        this(rating.getId(), rating.getCreatedAt());
    }

}
