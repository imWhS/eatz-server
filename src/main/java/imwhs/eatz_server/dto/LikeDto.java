package imwhs.eatz_server.dto;

import imwhs.eatz_server.domain.LikesType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeDto {

    private Long id;

    private Long entityId;

    private LikesType type;

    private Boolean isLiked;

    private Long count;

}
