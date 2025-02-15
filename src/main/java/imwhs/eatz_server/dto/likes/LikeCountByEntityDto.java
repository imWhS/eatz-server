package imwhs.eatz_server.dto.likes;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeCountByEntityDto {

    Long entityId;

    Long likeCount;

}
