package imwhs.eatz_server.dto;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.reaction.Reaction;
import lombok.Data;

@Data
public abstract class CreateReactionDto {

    public abstract Reaction toReactionEntity(Recipe recipe, EatzUser user);

    protected CreateReactionDto() {}

}
