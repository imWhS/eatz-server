package imwhs.eatz_server.dto;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.reaction.Comment;
import imwhs.eatz_server.domain.reaction.Reaction;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class CreateCommentDto extends CreateReactionDto {

    private String content;

    public CreateCommentDto(String content) {
        this.content = content;
    }

    @Override
    public Reaction toReactionEntity(Recipe recipe, EatzUser user) {
        return new Comment(recipe, user, content);
    }
}
