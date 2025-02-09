package imwhs.eatz_server.dto.likes;

import imwhs.eatz_server.domain.likes.LikesType;
import imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LikesDetailDto {

    private Long entityId;

    private LikesType type;

    private Long count;

    private List<EatzUserBasicDto> likedUsers;

    public LikesDetailDto(Long entityId, LikesType type, Long count) {
        this.entityId = entityId;
        this.type = type;
        this.count = count;
    }

}
