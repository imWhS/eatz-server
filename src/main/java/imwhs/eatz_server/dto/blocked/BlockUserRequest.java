package imwhs.eatz_server.dto.blocked;

import lombok.Data;

@Data
public class BlockUserRequest {

    Long blockerId;

    Long blockedUserId;

}
