package imwhs.eatz_server.domain.reaction;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.dto.UpdateRatingDto;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

@Getter
@Entity
@DiscriminatorValue("RATING")
public class Rating extends Reaction {

    private Integer score;

    public Rating() {}

    public Rating(Recipe recipe, EatzUser user, Integer score) {
        super(recipe, user);
        this.score = score;
    }

    public void update(UpdateRatingDto dto) {
        score = dto.getScore();
    }

}
