package imwhs.eatz_server.dto.liked;

import imwhs.eatz_server.domain.liked.EntityType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikedDto {

    private Long id;

    private Long entityId;

    private EntityType type;

    private Boolean isLiked;

    private Long count;

}
