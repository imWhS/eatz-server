package imwhs.eatz_server.dto.liked;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikedStatusByEntityDto {

    Long entityId;

    boolean liked;

}
