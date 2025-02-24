package imwhs.eatz_server.dto.liked;

import imwhs.eatz_server.domain.liked.LikedType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikedDto {

    private Long id;

    private Long entityId;

    private LikedType type;

    private Boolean isLiked;

    private Long count;

}
