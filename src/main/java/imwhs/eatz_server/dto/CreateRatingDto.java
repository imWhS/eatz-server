package imwhs.eatz_server.dto;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.reaction.Rating;
import imwhs.eatz_server.domain.reaction.Reaction;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class CreateRatingDto extends CreateReactionDto {

    private Integer score;

    protected CreateRatingDto() {}

    public CreateRatingDto(Integer score) {
        this.score = score;
    }

    @Override
    public Reaction toReactionEntity(Recipe recipe, EatzUser user) {
        return new Rating(recipe, user, score);
    }

}
