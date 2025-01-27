package imwhs.eatz_server.dto;

import imwhs.eatz_server.domain.LikesType;
import imwhs.eatz_server.dto.eatzuser.EatzUserDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LikeDetailDto {

    private Long entityId;

    private LikesType type;

    private Long count;

    private List<LikedUserDto> likedUsers;

    public LikeDetailDto(Long entityId, LikesType type, Long count) {
        this.entityId = entityId;
        this.type = type;
        this.count = count;
    }

}
