package imwhs.eatz_server.dto.liked;

import imwhs.eatz_server.domain.liked.LikedType;
import imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LikedDetailDto {

    private Long entityId;

    private LikedType type;

    private Long count;

    private List<EatzUserBasicDto> likedUsers;

    public LikedDetailDto(Long entityId, LikedType type, Long count) {
        this.entityId = entityId;
        this.type = type;
        this.count = count;
    }

}
