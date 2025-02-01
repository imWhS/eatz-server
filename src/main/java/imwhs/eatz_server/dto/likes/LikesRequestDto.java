package imwhs.eatz_server.dto.likes;

import imwhs.eatz_server.domain.likes.LikesType;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LikesRequestDto {

    private Long entityId;

    private LikesType type;

}
