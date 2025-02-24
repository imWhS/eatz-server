package imwhs.eatz_server.dto.liked;

import imwhs.eatz_server.domain.liked.LikedType;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LikedRequestDto {

    private Long entityId;

    private LikedType type;

}
