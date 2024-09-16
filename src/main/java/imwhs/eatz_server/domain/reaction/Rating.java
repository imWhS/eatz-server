package imwhs.eatz_server.domain.reaction;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("COMMENT")
public class Rating extends Reaction {

    private Integer score;

}
