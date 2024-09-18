package imwhs.eatz_server.domain.reaction;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.dto.UpdateCommentDto;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@DiscriminatorValue("COMMENT")
public class Comment extends Reaction {

    private String content;

    public Comment() {}

    public Comment(Recipe recipe, EatzUser user, String content) {
        super(recipe, user);
        this.content = content;
    }

    public void update(UpdateCommentDto dto) {
        this.content = dto.getContent();
    }
}
