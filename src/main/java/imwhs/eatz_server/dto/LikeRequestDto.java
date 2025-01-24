package imwhs.eatz_server.dto;

import imwhs.eatz_server.domain.LikesType;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LikeRequestDto {

    private Long entityId;

    private LikesType type;

}
