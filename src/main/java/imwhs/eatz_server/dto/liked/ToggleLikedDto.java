package imwhs.eatz_server.dto.liked;

import imwhs.eatz_server.domain.liked.EntityType;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ToggleLikedDto {

    private Long entityId;

    private EntityType type;

}
