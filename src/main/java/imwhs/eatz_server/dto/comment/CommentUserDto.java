package imwhs.eatz_server.dto.comment;

import imwhs.eatz_server.domain.EatzUser;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentUserDto {

    private Long id;

    private String username;

    public CommentUserDto(EatzUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }

}
