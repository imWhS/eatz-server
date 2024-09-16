package imwhs.eatz_server.domain.reaction;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("COMMENT")
public class Comment extends Reaction {

    private String content;

}
