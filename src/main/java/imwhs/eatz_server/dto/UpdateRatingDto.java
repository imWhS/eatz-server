package imwhs.eatz_server.dto;

import imwhs.eatz_server.domain.reaction.Rating;
import imwhs.eatz_server.domain.reaction.Reaction;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class UpdateRatingDto extends UpdateReactionDto {

    private Integer score;

    public UpdateRatingDto(Integer score) {
        this.score = score;
    }

}
