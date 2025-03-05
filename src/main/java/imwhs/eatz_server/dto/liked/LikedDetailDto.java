package imwhs.eatz_server.dto.liked;

import imwhs.eatz_server.domain.liked.EntityType;
import imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LikedDetailDto {

    private Long entityId;

    private EntityType type;

    private Long count;

    private List<EatzUserBasicDto> likedUsers;

    public LikedDetailDto(Long entityId, EntityType type, Long count) {
        this.entityId = entityId;
        this.type = type;
        this.count = count;
    }

}
