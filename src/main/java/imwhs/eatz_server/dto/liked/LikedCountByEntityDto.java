package imwhs.eatz_server.dto.liked;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikedCountByEntityDto {

    Long entityId;

    Long likedCount;

}
