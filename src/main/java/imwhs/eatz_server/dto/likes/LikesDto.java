package imwhs.eatz_server.dto.likes;

import imwhs.eatz_server.domain.likes.LikesType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikesDto {

    private Long id;

    private Long entityId;

    private LikesType type;

    private Boolean isLiked;

    private Long count;

}
